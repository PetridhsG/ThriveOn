package gr.aueb.thriveon.domain.interactors

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import gr.aueb.thriveon.domain.model.CategoryProgress
import gr.aueb.thriveon.domain.model.CompletedTask
import gr.aueb.thriveon.domain.model.EditableProfile
import gr.aueb.thriveon.domain.model.MilestoneTitle
import gr.aueb.thriveon.domain.model.UserCardInfo
import gr.aueb.thriveon.domain.model.UserProfileInfo
import gr.aueb.thriveon.domain.utils.asNestedStringMap
import gr.aueb.thriveon.domain.utils.asStringListMap
import gr.aueb.thriveon.domain.utils.asStringLongMap
import gr.aueb.thriveon.domain.utils.asStringToListOfMaps
import gr.aueb.thriveon.domain.utils.asTypedList
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import kotlin.String

interface UserInteractor {
    suspend fun getUsername(): String
    suspend fun getUserPreferences(): List<String>
    suspend fun getUserProfilePictureUrl(): String?
    suspend fun getUsersByIds(userIds: List<String>): List<UserCardInfo>
    fun getUserProfile(userId: String): Flow<UserProfileInfo>
    suspend fun getFriendsForUser(userId: String): List<UserCardInfo>
    suspend fun saveUserPreferences(preferences: List<String>)
    suspend fun getUserMilestones(userId: String): List<MilestoneTitle>
    suspend fun getUserEditableProfile(): EditableProfile
    suspend fun updateUserProfile(username: String, bio: String, equippedTitle: String?)
    suspend fun uploadProfilePicture(imageUri: Uri): String
    suspend fun getUserTitles(): List<String>
    suspend fun getPostDateRange(userId: String): Pair<LocalDate, LocalDate>?
    suspend fun sendFriendRequestNotification(toUserId: String)
    suspend fun removeFriend(friendUserId: String)
    suspend fun getUserCategoryProgressWithMilestones(userId: String): List<CategoryProgress>
}

class UserInteractorImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : UserInteractor {
    override suspend fun getUsername(): String {
        val uid = auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")
        val snapshot = firestore.collection("users").document(uid).get().await()
        return snapshot.getString("username") ?: ""
    }

    override suspend fun getUserPreferences(): List<String> {
        val uid = auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")
        val snapshot = firestore.collection("users").document(uid).get().await()
        return snapshot.get("preferences").asTypedList<String>()
    }

    override suspend fun getUserProfilePictureUrl(): String? {
        val uid = auth.currentUser?.uid ?: return null
        val snapshot = firestore.collection("users").document(uid).get().await()
        return snapshot.getString("profile_picture_url")
    }

    override suspend fun getUsersByIds(userIds: List<String>): List<UserCardInfo> {
        if (userIds.isEmpty()) return emptyList()

        val result = mutableListOf<UserCardInfo>()

        userIds.chunked(10).forEach { chunk ->
            val snapshot = firestore.collection("users")
                .whereIn(FieldPath.documentId(), chunk)
                .get()
                .await()

            snapshot.documents.mapNotNullTo(result) { doc ->
                val id = doc.id
                val username = doc.getString("username") ?: return@mapNotNullTo null
                val profilePictureUrl = doc.getString("profile_picture_url") ?: ""
                val equippedTitle = doc.getString("equipped_title") ?: ""
                val preferences = doc.get("preferences").asTypedList<String>()

                UserCardInfo(
                    userId = id,
                    username = username,
                    profilePictureUrl = profilePictureUrl,
                    equippedTitle = equippedTitle,
                    preferences = preferences
                )
            }
        }

        return result
    }

    override fun getUserProfile(userId: String): Flow<UserProfileInfo> = callbackFlow {
        if (userId.isBlank()) {
            close(IllegalArgumentException("Invalid userId passed to getUserProfile()"))
            return@callbackFlow
        }

        val currentUserId = auth.currentUser?.uid ?: run {
            close(IllegalStateException("User not logged in"))
            return@callbackFlow
        }

        val listener = firestore.collection("users")
            .document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    close(error ?: Exception("Snapshot error"))
                    return@addSnapshotListener
                }

                launch {
                    val userFriends =
                        (snapshot.get("friends") as? List<*>)?.filterIsInstance<String>()
                            ?: emptyList()

                    val areFriends = userId != currentUserId && userFriends.contains(currentUserId)

                    val friendRequestSnapshot = firestore
                        .collection("friend_requests")
                        .document(userId)
                        .collection("incoming")
                        .document(currentUserId)
                        .get()
                        .await()

                    val isRequestSent = friendRequestSnapshot.exists()

                    val titles = snapshot.get("titles") as? List<*> ?: emptyList<Any>()
                    val badges = snapshot.get("badges").asTypedList<String>()

                    val profile = UserProfileInfo(
                        username = snapshot.getString("username") ?: "",
                        profilePictureUrl = snapshot.getString("profile_picture_url") ?: "",
                        equippedTitle = snapshot.getString("equipped_title") ?: "",
                        streak = (snapshot.getLong("streak") ?: 0L).toInt(),
                        friendsCount = userFriends.size,
                        titlesCount = titles.size,
                        badges = badges.take(3),
                        bio = snapshot.getString("bio") ?: "",
                        isFriend = areFriends,
                        isFriendRequestSent = isRequestSent
                    )

                    trySend(profile)
                }
            }

        awaitClose { listener.remove() }
    }

    override suspend fun getFriendsForUser(userId: String): List<UserCardInfo> {
        val userDoc = firestore.collection("users").document(userId).get().await()
        val friendIds = userDoc.get("friends").asTypedList<String>()

        if (friendIds.isEmpty()) return emptyList()

        val friendDocs = firestore.collection("users")
            .whereIn(FieldPath.documentId(), friendIds)
            .get().await()

        return friendDocs.documents.mapNotNull { doc ->
            val id = doc.id
            val username = doc.getString("username") ?: return@mapNotNull null
            val profileUrl = doc.getString("profile_picture_url") ?: ""
            val equippedTitle = doc.getString("equipped_title") ?: ""

            UserCardInfo(id, username, profileUrl, equippedTitle)
        }
    }

    override suspend fun saveUserPreferences(preferences: List<String>) {
        val uid = auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")
        firestore.collection("users").document(uid)
            .update("preferences", preferences)
            .await()

        firestore.collection("all_users").document(uid)
            .update("preferences", preferences)
            .await()
    }

    override suspend fun getUserMilestones(userId: String): List<MilestoneTitle> {
        val userSnapshot = firestore.collection("users").document(userId).get().await()
        val progress = userSnapshot.get("category_progress").asStringLongMap()

        val tasksSnapshot = firestore.collection("tasks").get().await()
        val seen = mutableSetOf<Pair<String, Int>>()
        val results = mutableListOf<MilestoneTitle>()

        for (doc in tasksSnapshot.documents) {
            val category = doc.getString("category_title") ?: continue
            val userProgress = progress[category]?.toInt() ?: continue

            val milestones = doc.get("milestones").asNestedStringMap()
            if (milestones.isEmpty()) continue

            milestones.forEach { (thresholdStr, milestoneData) ->
                val threshold = thresholdStr.toIntOrNull() ?: return@forEach
                val key = category to threshold

                if (userProgress >= threshold && key !in seen) {
                    val title = milestoneData["title"] ?: return@forEach
                    val badge = milestoneData["badge"] ?: return@forEach
                    seen.add(key)
                    results.add(
                        MilestoneTitle(
                            category = category,
                            milestoneTitle = title,
                            badge = badge,
                            completedCount = userProgress,
                            requiredCount = threshold
                        )
                    )
                }
            }
        }
        return results
    }

    override suspend fun getUserEditableProfile(): EditableProfile {
        val uid = auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")
        val doc = firestore.collection("users").document(uid).get().await()

        return EditableProfile(
            username = doc.getString("username") ?: "",
            bio = doc.getString("bio") ?: "",
            profilePictureUrl = doc.getString("profile_picture_url") ?: "",
            equippedTitle = doc.getString("equipped_title"),
            titles = doc.get("titles").asTypedList<String>()
        )
    }

    override suspend fun updateUserProfile(username: String, bio: String, equippedTitle: String?) {
        val uid = auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")

        val updates = mutableMapOf<String, Any>(
            "username" to username,
            "bio" to bio
        )
        equippedTitle?.let { updates["equipped_title"] = it }

        firestore.collection("users").document(uid).update(updates).await()

        val userDoc = firestore.collection("users").document(uid).get().await()
        val profilePictureUrl = userDoc.getString("profile_picture_url") ?: ""
        val equippedTitleFromDoc = equippedTitle ?: userDoc.getString("equipped_title") ?: ""
        val preferences = userDoc.get("preferences").asTypedList<String>()

        val allUsersUpdate = mapOf(
            "user_id" to uid,
            "username" to username,
            "profile_picture_url" to profilePictureUrl,
            "equipped_title" to equippedTitleFromDoc,
            "preferences" to preferences
        )

        firestore.collection("all_users").document(uid).set(allUsersUpdate).await()

        val userPosts = firestore.collection("posts")
            .whereEqualTo("user_id", uid)
            .get()
            .await()

        userPosts.documents.forEach { doc ->
            firestore.collection("posts").document(doc.id).update(
                mapOf(
                    "username" to username,
                    "user_profile_picture_url" to profilePictureUrl
                )
            ).await()
        }
    }

    override suspend fun uploadProfilePicture(uri: Uri): String {
        val uid = auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")
        val storageRef = FirebaseStorage.getInstance().reference
            .child("profile_pics/$uid.jpg")

        storageRef.putFile(uri).await()
        return storageRef.downloadUrl.await().toString().also { url ->
            firestore.collection("users").document(uid)
                .update("profile_picture_url", url).await()
        }
    }

    override suspend fun getUserTitles(): List<String> {
        val uid = auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")
        val doc = firestore.collection("users").document(uid).get().await()
        return (doc.get("titles").asTypedList<String>())
    }

    override suspend fun getPostDateRange(userId: String): Pair<LocalDate, LocalDate>? {
        val snapshot = firestore.collection("users").document(userId).get().await()
        val postsMap = snapshot.get("posts").asStringListMap()
        if (postsMap.isEmpty()) return null

        val dates = postsMap.keys.mapNotNull {
            try {
                LocalDate.parse(it)
            } catch (_: Exception) {
                null
            }
        }.sorted()

        return if (dates.isNotEmpty()) Pair(dates.first(), dates.last()) else null
    }

    override suspend fun sendFriendRequestNotification(toUserId: String) {
        val currentUserId = auth.currentUser?.uid?.trim()
            ?: throw IllegalStateException("User not logged in")

        if (currentUserId == toUserId) return

        val existingRequest = firestore
            .collection("friend_requests")
            .document(toUserId)
            .collection("incoming")
            .document(currentUserId)
            .get()
            .await()

        if (existingRequest.exists()) {
            return
        }

        val requestData = mapOf(
            "from_user_id" to currentUserId,
            "timestamp" to FieldValue.serverTimestamp()
        )

        firestore.collection("friend_requests")
            .document(toUserId)
            .collection("incoming")
            .document(currentUserId)
            .set(requestData)
            .await()

        val notificationData = mapOf(
            "notification_type" to "friend_request",
            "from" to currentUserId,
            "isAccepted" to false,
            "timestamp" to FieldValue.serverTimestamp()
        )

        firestore.collection("users")
            .document(toUserId)
            .collection("notifications")
            .add(notificationData)
            .await()
    }

    override suspend fun removeFriend(friendUserId: String) {
        val currentUserId =
            auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")

        firestore.collection("users").document(currentUserId)
            .update("friends", FieldValue.arrayRemove(friendUserId)).await()

        firestore.collection("users").document(friendUserId)
            .update("friends", FieldValue.arrayRemove(currentUserId)).await()
    }

    override suspend fun getUserCategoryProgressWithMilestones(userId: String): List<CategoryProgress> {
        require(userId.isNotBlank()) { "User ID is required to fetch progress." }

        val profileSnapshot = firestore.collection("users").document(userId).get().await()
        val progressMap = profileSnapshot.get("category_progress").asStringLongMap()
        val userMilestones = getUserMilestones(userId)

        val tasksSnapshot = firestore.collection("tasks").get().await()

        val defaultImagePerCategory = mutableMapOf<String, String>()
        val milestoneMapPerCategory = mutableMapOf<String, Map<String, Map<String, String>>>()
        val taskMap = mutableMapOf<String, Pair<String, String>>()

        for (task in tasksSnapshot.documents) {
            val category = task.getString("category_title") ?: continue
            val title = task.getString("title") ?: continue
            val taskId = task.id

            taskMap[taskId] = title to category

            if (category !in defaultImagePerCategory) {
                defaultImagePerCategory[category] = task.getString("default_picture") ?: ""
            }

            if (category !in milestoneMapPerCategory) {
                @Suppress("UNCHECKED_CAST")
                val milestones = task.get("milestones") as? Map<String, Map<String, String>> ?: emptyMap()
                milestoneMapPerCategory[category] = milestones
            }
        }

        val rawDailyTasks = profileSnapshot.get("daily_tasks").asStringToListOfMaps()
        val completedTasksByCategory = mutableMapOf<String, MutableList<CompletedTask>>()

        for ((dateStr, taskList) in rawDailyTasks) {
            val date = try {
                LocalDate.parse(dateStr)
            } catch (_: Exception) {
                println("Invalid date format: $dateStr")
                continue
            }

            taskList.forEach { taskEntry ->
                val taskId = taskEntry["task_id"] as? String
                val isCompleted = taskEntry["is_completed"] as? Boolean == true

                if (!isCompleted || taskId == null) return@forEach

                val taskData = taskMap[taskId]
                if (taskData == null) {
                    println("Missing task in taskMap: $taskId")
                    return@forEach
                }

                val (title, category) = taskData
                val task = CompletedTask(taskId, title, date)
                completedTasksByCategory.getOrPut(category) { mutableListOf() }.add(task)
            }
        }

        return progressMap.map { (category, value) ->
            val progress = value.toInt()
            val milestonesForCategory = userMilestones.filter { it.category == category }
            val current = milestonesForCategory.maxByOrNull { it.requiredCount }
            val imageUrl = defaultImagePerCategory[category] ?: ""

            val badge = current?.requiredCount?.toString()?.let { key ->
                milestoneMapPerCategory[category]?.get(key)?.get("badge")
            }

            CategoryProgress(
                category = category,
                progress = progress,
                milestones = milestonesForCategory,
                currentMilestone = current,
                defaultImageUrl = imageUrl,
                badge = badge,
                completedTasks = completedTasksByCategory[category]?.sortedByDescending { it.date } ?: emptyList()
            )
        }
    }
}
