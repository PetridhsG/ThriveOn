package gr.aueb.thriveon.domain.interactors

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import gr.aueb.thriveon.domain.model.Post
import gr.aueb.thriveon.domain.utils.asMutableStringLongMap
import gr.aueb.thriveon.domain.utils.asMutableStringMap
import gr.aueb.thriveon.domain.utils.asStringAnyMap
import gr.aueb.thriveon.domain.utils.asStringIntMap
import gr.aueb.thriveon.domain.utils.asStringMap
import gr.aueb.thriveon.domain.utils.asTypedList
import gr.aueb.thriveon.domain.utils.getStringListForKey
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter

interface PostInteractor {
    fun getFeedPostsForFriends(): Flow<List<Post>>
    suspend fun reactToPost(postId: String, reaction: String)
    fun getPostsForDate(userId: String, date: LocalDate): Flow<List<Post>>
    suspend fun deletePost(postId: String)
}

class PostInteractorImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : PostInteractor {
    override fun getFeedPostsForFriends(): Flow<List<Post>> = callbackFlow {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            close(IllegalStateException("Not logged in"))
            return@callbackFlow
        }

        val userDoc = firestore.collection("users").document(uid).get().await()
        val friendIds = userDoc.get("friends").asTypedList<String>()
        if (friendIds.isEmpty()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = firestore.collection("posts")
            .whereIn("user_id", friendIds)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    close(error ?: Exception("Snapshot error"))
                    return@addSnapshotListener
                }

                launch {
                    val posts = snapshot.documents.mapNotNull { doc ->
                        val timestamp = doc.getTimestamp("timestamp")?.toDate()?.time ?: return@mapNotNull null
                        val reactsRaw = doc.get("reacts").asStringAnyMap()
                        val reacts = reactsRaw.mapValues { (_, v) -> (v as? Long)?.toInt() ?: 0 }
                        val userReactedRaw = doc.get("user_reacted").asStringAnyMap()
                        val userReacted = userReactedRaw.mapValues { it.value.toString() }

                        Post(
                            postId = doc.id,
                            userId = doc.getString("user_id") ?: return@mapNotNull null,
                            username = doc.getString("username") ?: "",
                            taskTitle = doc.getString("task_title") ?: "",
                            taskCategory = doc.getString("task_category") ?: "",
                            userProfilePictureUrl = doc.getString("user_profile_picture_url") ?: "",
                            imageUrl = doc.getString("image_url") ?: "",
                            reacts = reacts,
                            timestamp = timestamp,
                            userReacted = userReacted
                        )
                    }.sortedByDescending { it.timestamp }

                    trySend(posts)
                }
            }

        awaitClose { listener.remove() }
    }

    override suspend fun reactToPost(postId: String, reaction: String) {
        val userId = auth.currentUser?.uid ?: throw IllegalStateException("Not logged in")
        val postRef = firestore.collection("posts").document(postId)

        var isFirstReaction = false

        firestore.runTransaction { tx ->
            val snapshot = tx.get(postRef)

            val reacts = snapshot.get("reacts").asMutableStringLongMap()
            val userReacted = snapshot.get("user_reacted").asMutableStringMap()

            val previousReaction = userReacted[userId]

            if (previousReaction != null) {
                val currentCount = reacts[previousReaction] ?: 0L
                reacts[previousReaction] = (currentCount - 1).coerceAtLeast(0)
            } else {
                isFirstReaction = true
            }

            if (previousReaction == reaction) {
                userReacted.remove(userId)
            } else {
                reacts[reaction] = (reacts[reaction] ?: 0L) + 1
                userReacted[userId] = reaction
            }

            tx.update(postRef, mapOf(
                "reacts" to reacts,
                "user_reacted" to userReacted
            ))
        }.await()

        if (!isFirstReaction) return

        val postSnapshot = firestore.collection("posts").document(postId).get().await()
        val postOwnerId = postSnapshot.getString("user_id") ?: return
        if (postOwnerId == userId) return

        val reactingUserDoc = firestore.collection("users").document(userId).get().await()
        val fromUsername = reactingUserDoc.getString("username") ?: ""
        val fromProfilePictureUrl = reactingUserDoc.getString("profile_picture_url") ?: ""
        val fromEquippedTitle = reactingUserDoc.getString("equipped_title") ?: ""

        val notificationData = hashMapOf(
            "from" to userId,
            "notification_type" to "reaction",
            "message" to "$fromUsername reacted to your post with $reaction",
            "postId" to postId,
            "fromUsername" to fromUsername,
            "fromProfilePictureUrl" to fromProfilePictureUrl,
            "fromEquippedTitle" to fromEquippedTitle,
            "timestamp" to FieldValue.serverTimestamp()
        )

        firestore.collection("users")
            .document(postOwnerId)
            .collection("notifications")
            .add(notificationData)
    }

    override fun getPostsForDate(userId: String, date: LocalDate): Flow<List<Post>> = callbackFlow {
        val formattedDate = date.format(DateTimeFormatter.ISO_DATE)

        val userDoc = firestore.collection("users").document(userId).get().await()
        val postIds = userDoc.get("posts").getStringListForKey(formattedDate)

        if (postIds.isEmpty()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = firestore.collection("posts")
            .whereIn(FieldPath.documentId(), postIds)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    close(error ?: Exception("Snapshot error"))
                    return@addSnapshotListener
                }

                launch {
                    val posts = snapshot.documents.mapNotNull { doc ->
                        val timestamp = doc.getTimestamp("timestamp")?.toDate()?.time ?: return@mapNotNull null
                        val reacts = doc.get("reacts").asStringIntMap()
                        val userReacted = doc.get("user_reacted").asStringMap()

                        Post(
                            postId = doc.id,
                            userId = doc.getString("user_id") ?: return@mapNotNull null,
                            username = doc.getString("username") ?: "",
                            taskTitle = doc.getString("task_title") ?: "",
                            taskCategory = doc.getString("task_category") ?: "",
                            userProfilePictureUrl = doc.getString("user_profile_picture_url") ?: "",
                            imageUrl = doc.getString("image_url") ?: "",
                            reacts = reacts,
                            timestamp = timestamp,
                            userReacted = userReacted
                        )
                    }

                    trySend(posts.sortedByDescending { it.timestamp })
                }
            }

        awaitClose { listener.remove() }
    }

    override suspend fun deletePost(postId: String) {
        firestore.collection("posts").document(postId).delete().await()
    }
}
