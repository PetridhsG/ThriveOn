package gr.aueb.thriveon.domain.interactors

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import gr.aueb.thriveon.domain.model.NotificationItem
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

interface NotificationsInteractor {
    fun observeUserNotifications(): Flow<List<NotificationItem>>
    suspend fun acceptFriendRequest(notification: NotificationItem)
    suspend fun deleteFriendRequest(notification: NotificationItem)
    suspend fun deleteReaction(notification: NotificationItem)
}

class NotificationsInteractorImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : NotificationsInteractor {
    override fun observeUserNotifications(): Flow<List<NotificationItem>> = callbackFlow {
        val currentUser = auth.currentUser ?: run {
            close(IllegalStateException("User not logged in"))
            return@callbackFlow
        }

        val listener = firestore.collection("users")
            .document(currentUser.uid)
            .collection("notifications")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    close(error ?: Exception("Snapshot error"))
                    return@addSnapshotListener
                }

                launch {
                    val items = snapshot.documents.map { doc ->
                        val fromId = doc.getString("from") ?: ""
                        val type = doc.getString("notification_type") ?: ""

                        var fromUsername = ""
                        var fromProfilePictureUrl = ""
                        var fromEquippedTitle = ""

                        if ((type == "friend_request" || type == "reaction") && fromId.isNotBlank()) {
                            val userDoc =
                                firestore.collection("users").document(fromId).get().await()
                            fromUsername = userDoc.getString("username") ?: fromId
                            fromProfilePictureUrl = userDoc.getString("profile_picture_url") ?: ""
                            fromEquippedTitle = userDoc.getString("equipped_title") ?: ""
                        }else{
                            val userDoc =
                                firestore.collection("users").document(fromId).get().await()
                            fromUsername = userDoc.getString("username") ?: fromId
                            fromProfilePictureUrl = userDoc.getString("profile_picture_url") ?: ""
                            fromEquippedTitle = userDoc.getString("equipped_title") ?: ""
                        }

                        NotificationItem(
                            id = doc.id,
                            type = type,
                            from = fromId,
                            fromUsername = fromUsername,
                            fromProfilePictureUrl = fromProfilePictureUrl,
                            fromEquippedTitle = fromEquippedTitle,
                            isAccepted = doc.getBoolean("isAccepted"),
                            message = doc.getString("message"),
                            timestamp = doc.getTimestamp("timestamp")
                        )
                    }

                    trySend(items)
                }
            }

        awaitClose { listener.remove() }
    }

    override suspend fun acceptFriendRequest(notification: NotificationItem) {
        val currentUserId =
            auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")
        val senderId = notification.from

        firestore.collection("users").document(currentUserId)
            .update("friends", FieldValue.arrayUnion(senderId)).await()
        firestore.collection("users").document(senderId)
            .update("friends", FieldValue.arrayUnion(currentUserId)).await()

        firestore.collection("friend_requests")
            .document(currentUserId)
            .collection("incoming")
            .document(senderId)
            .delete().await()

        firestore.collection("users")
            .document(currentUserId)
            .collection("notifications")
            .document(notification.id)
            .delete().await()
    }

    override suspend fun deleteFriendRequest(notification: NotificationItem) {
        val currentUserId =
            auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")
        val senderId = notification.from

        firestore.collection("friend_requests")
            .document(currentUserId)
            .collection("incoming")
            .document(senderId)
            .delete().await()

        firestore.collection("users")
            .document(currentUserId)
            .collection("notifications")
            .document(notification.id)
            .delete().await()
    }

    override suspend fun deleteReaction(notification: NotificationItem) {
        val currentUserId =
            auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")
        val senderId = notification.from

        firestore.collection("reaction")
            .document(currentUserId)
            .collection("incoming")
            .document(senderId)
            .delete().await()

        firestore.collection("users")
            .document(currentUserId)
            .collection("notifications")
            .document(notification.id)
            .delete().await()
    }
}
