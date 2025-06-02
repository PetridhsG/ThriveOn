package gr.aueb.thriveon.domain.interactors

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import gr.aueb.thriveon.domain.model.DailyTask
import gr.aueb.thriveon.domain.model.FirebaseTask
import gr.aueb.thriveon.domain.utils.asMutableStringToListOfMaps
import gr.aueb.thriveon.domain.utils.asNestedStringMap
import gr.aueb.thriveon.domain.utils.asStringList
import gr.aueb.thriveon.domain.utils.asStringListMap
import gr.aueb.thriveon.domain.utils.asStringLongMap
import gr.aueb.thriveon.domain.utils.asStringToListOfMaps
import gr.aueb.thriveon.domain.utils.getListOfMapsForDate
import gr.aueb.thriveon.network.api.ApiClient
import gr.aueb.thriveon.network.model.CompletedTask
import gr.aueb.thriveon.network.model.SerializableTask
import gr.aueb.thriveon.network.model.UserRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.time.LocalDate

interface DailyTaskInteractor {
    suspend fun fetchThreeTasks(categories: List<String>): List<FirebaseTask>
    suspend fun writeTaskToFirebase(taskId: String)
    suspend fun getRerollCount(): Int
    suspend fun decrementRerollCount()
    suspend fun incrementRerollCount()
    fun getTodayDailyTasks(): Flow<List<DailyTask>>
    suspend fun getTasksByIds(ids: List<String>): Map<String, FirebaseTask>
    suspend fun markTaskAsCompleted(slotIndex: Int, rating: Int? = null)
    suspend fun uploadPhotoToFirebase(uri: Uri): String
    suspend fun createPost(
        taskId: String,
        taskTitle: String,
        taskCategory: String,
        imageUrl: String,
        userProfilePictureUrl: String,
        username: String,
    )
    suspend fun updateStreakAfterCompletion()
    suspend fun getTodaySuggestions(): Map<Int, String>
    suspend fun saveTodaySuggestions(suggestions: Map<Int, String>)
}

class DailyTaskInteractorImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val apiClient: ApiClient
) : DailyTaskInteractor {
    override suspend fun fetchThreeTasks(categories: List<String>): List<FirebaseTask> {
        if (categories.isEmpty()) return emptyList()

        val uid = auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")
        val userDocRef = firestore.collection("users").document(uid)
        val userDoc = userDocRef.get().await()

        val categoryProgress = userDoc["category_progress"].asStringLongMap()
        val completedTasks = getCompletedTasks(userDoc)
        val activeTasks = getActiveTasks(userDoc)
        val currentSuggestions = getCurrentSuggestions(userDoc)
        val suggestionHistory = userDoc["suggestion_history"].asStringList()

        // 🔁 Clear history if it's too long
        if (suggestionHistory.size > 30) {
            println("🧹 Clearing suggestion history (size: ${suggestionHistory.size})")
            userDocRef.update("suggestion_history", emptyList<String>())
        }

        val allTasks = firestore.collection("tasks").get().await()
            .documents.mapNotNull { doc ->
                val title = doc.getString("title") ?: return@mapNotNull null
                val categoryTitle = doc.getString("category_title") ?: return@mapNotNull null
                val categoryIcon = doc.getString("category_icon") ?: return@mapNotNull null
                val defaultPicture = doc.getString("default_picture") ?: return@mapNotNull null

                SerializableTask(
                    id = doc.id,
                    title = title,
                    categoryTitle = categoryTitle,
                    categoryIcon = categoryIcon,
                    defaultPicture = defaultPicture
                )
            }

        val request = UserRequest(
            preferences = categories,
            categoryProgress = categoryProgress.mapValues { it.value.toInt() },
            completedTasks = completedTasks,
            activeTasks = activeTasks,
            currentSuggestions = currentSuggestions,
            allTasks = allTasks,
            suggestionHistory = suggestionHistory
        )

        return try {
            val suggestedIds = apiClient.getUserPreferences(request).suggestedTaskIds
            println("Suggested IDs from LLM: $suggestedIds")

            val taskMap = getTasksByIds(suggestedIds)
            val fetchedTasks = taskMap.values.toList()

            fetchedTasks.forEachIndexed { index, task ->
                println("${index + 1}. ${task.title}")
            }

            userDocRef.update("suggestion_history", FieldValue.arrayUnion(*suggestedIds.toTypedArray()))

            if (fetchedTasks.size >= 3) {
                return fetchedTasks
            }

            val missingCount = 3 - fetchedTasks.size
            val backupTasks = allTasks
                .filterNot { it.id in fetchedTasks.map { t -> t.id } + activeTasks + suggestionHistory }
                .shuffled()
                .take(missingCount)
                .map {
                    FirebaseTask(
                        id = it.id,
                        title = it.title,
                        categoryTitle = it.categoryTitle,
                        categoryIcon = it.categoryIcon,
                        defaultPicture = it.defaultPicture
                    )
                }

            return (fetchedTasks + backupTasks).take(3)
        } catch (_: Exception) {
            val preferredTasks = allTasks.filter {
                it.categoryTitle in categories &&
                        it.id !in activeTasks &&
                        it.id !in currentSuggestions &&
                        it.id !in suggestionHistory
            }

            val fallback = if (preferredTasks.size >= 3) {
                preferredTasks.shuffled().take(3)
            } else {
                (preferredTasks + allTasks).distinctBy { it.id }.shuffled().take(3)
            }

            return fallback.map {
                FirebaseTask(
                    id = it.id,
                    title = it.title,
                    categoryTitle = it.categoryTitle,
                    categoryIcon = it.categoryIcon,
                    defaultPicture = it.defaultPicture
                )
            }
        }
    }

    override suspend fun writeTaskToFirebase(taskId: String) {
        val uid = auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")
        val today = LocalDate.now().toString()

        val taskEntry = hashMapOf(
            "task_id" to taskId,
            "is_completed" to false,
            "rating" to 0
        )

        val userRef = firestore.collection("users").document(uid)
        val doc = userRef.get().await()
        val dailyTasksMap = doc.get("daily_tasks").asStringToListOfMaps()
        val todayTasks = dailyTasksMap[today]?.toMutableList() ?: mutableListOf()
        todayTasks.add(taskEntry)

        val updatedDailyTasks = dailyTasksMap.toMutableMap()
        updatedDailyTasks[today] = todayTasks

        userRef.update("daily_tasks", updatedDailyTasks).await()
    }

    override suspend fun getRerollCount(): Int {
        val uid = auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")
        val snapshot = firestore.collection("users").document(uid).get().await()
        return (snapshot.getLong("rerolls") ?: 0L).toInt()
    }

    override suspend fun decrementRerollCount() {
        val uid = auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")
        val userRef = firestore.collection("users").document(uid)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(userRef)
            val currentRerolls = (snapshot.getLong("rerolls") ?: 0L).toInt()
            if (currentRerolls > 0) {
                transaction.update(userRef, "rerolls", currentRerolls - 1)
            }
        }.await()
    }

    override suspend fun incrementRerollCount() {
        val uid = auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")
        val userRef = firestore.collection("users").document(uid)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(userRef)
            val currentRerolls = (snapshot.getLong("rerolls") ?: 0L).toInt()
            transaction.update(userRef, "rerolls", currentRerolls + 1)
        }.await()
    }

    override fun getTodayDailyTasks(): Flow<List<DailyTask>> = callbackFlow {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            close(Exception("Not logged in"))
            return@callbackFlow
        }

        val today = LocalDate.now().toString()

        val listener = firestore.collection("users").document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    close(error ?: Exception("Snapshot error"))
                    return@addSnapshotListener
                }

                val dailyTasksMap = snapshot.get("daily_tasks").asStringToListOfMaps()
                val todayTasks = dailyTasksMap[today] ?: emptyList()

                val parsed = todayTasks.mapNotNull { map ->
                    val taskId = map["task_id"] as? String ?: return@mapNotNull null
                    val isCompleted = map["is_completed"] as? Boolean == true
                    DailyTask(taskId, isCompleted)
                }.take(3)

                trySend(parsed)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun getTasksByIds(ids: List<String>): Map<String, FirebaseTask> {
        if (ids.isEmpty()) return emptyMap()

        val snapshot = firestore.collection("tasks")
            .whereIn(FieldPath.documentId(), ids.take(10))
            .get()
            .await()

        return snapshot.documents.mapNotNull { doc ->
            val id = doc.id
            val title = doc.getString("title") ?: return@mapNotNull null
            val categoryTitle = doc.getString("category_title") ?: return@mapNotNull null
            val categoryIcon = doc.getString("category_icon") ?: ""
            val defaultPicture = doc.getString("default_picture") ?: return@mapNotNull null

            id to FirebaseTask(
                id = id,
                title = title,
                categoryTitle = categoryTitle,
                categoryIcon = categoryIcon,
                defaultPicture = defaultPicture
            )
        }.toMap()
    }

    override suspend fun markTaskAsCompleted(slotIndex: Int, rating: Int?) {
        val uid = auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")
        val today = LocalDate.now().toString()
        val userRef = firestore.collection("users").document(uid)
        val allUsersRef = firestore.collection("all_users").document(uid)

        val userSnapshot = userRef.get().await()
        val tasksMap = userSnapshot["daily_tasks"].getListOfMapsForDate(today)
        if (tasksMap.isEmpty()) return
        if (slotIndex !in tasksMap.indices) return

        val updatedTasks = tasksMap.toMutableList()
        val updatedTask = updatedTasks[slotIndex].toMutableMap().apply {
            this["is_completed"] = true
            rating?.let { this["rating"] = it }
        }
        updatedTasks[slotIndex] = updatedTask

        val dailyTasksMap = userSnapshot["daily_tasks"].asMutableStringToListOfMaps()
        dailyTasksMap[today] = updatedTasks

        val taskId = updatedTask["task_id"] as? String ?: return
        val taskSnapshot = firestore.collection("tasks").document(taskId).get().await()
        val categoryTitle = taskSnapshot.getString("category_title") ?: return
        val milestones = taskSnapshot.get("milestones").asNestedStringMap()

        val currentProgressMap = userSnapshot.get("category_progress").asStringLongMap()
        val currentCount = currentProgressMap[categoryTitle]?.toInt() ?: 0
        val newCount = currentCount + 1

        val updatedCategoryProgress = currentProgressMap.toMutableMap().apply {
            this[categoryTitle] = newCount.toLong()
        }

        val hitMilestone = milestones[newCount.toString()]
        val updatedTitles = userSnapshot.get("titles").asStringList()
        val updatedTitlesList = updatedTitles.toMutableList()
        val updatesMap = mutableMapOf<String, Any>(
            "daily_tasks" to dailyTasksMap,
            "category_progress" to updatedCategoryProgress
        )

        if (hitMilestone != null) {
            val newTitle = hitMilestone["title"] ?: ""
            if (newTitle.isNotEmpty() && !updatedTitlesList.contains(newTitle)) {
                updatedTitlesList.add(newTitle)
                updatesMap["titles"] = updatedTitlesList
                updatesMap["equipped_title"] = newTitle

                allUsersRef.update(mapOf("equipped_title" to newTitle)).await()
            }
        }
        userRef.update(updatesMap).await()
    }

    override suspend fun uploadPhotoToFirebase(uri: Uri): String {
        val uid = auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")
        val fileName = "${System.currentTimeMillis()}.jpg"
        val storageRef = FirebaseStorage.getInstance()
            .reference.child("posts/$uid/$fileName")

        storageRef.putFile(uri).await()
        return storageRef.downloadUrl.await().toString()
    }

    override suspend fun createPost(
        taskId: String,
        taskTitle: String,
        taskCategory: String,
        imageUrl: String,
        userProfilePictureUrl: String,
        username: String,
    ) {
        val uid = auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")
        val today = LocalDate.now().toString()
        val firestore = FirebaseFirestore.getInstance()

        val postData = hashMapOf(
            "user_id" to uid,
            "username" to username,
            "task_id" to taskId,
            "task_title" to taskTitle,
            "task_category" to taskCategory,
            "user_profile_picture_url" to userProfilePictureUrl,
            "image_url" to imageUrl,
            "reacts" to mapOf(
                "like" to 0,
                "heart" to 0,
                "wow" to 0,
                "fire" to 0,
                "party" to 0
            ),
            "user_reacted" to emptyMap<String, String>(),
            "timestamp" to FieldValue.serverTimestamp()
        )

        val postRef = firestore.collection("posts").add(postData).await()
        val postId = postRef.id

        val userRef = firestore.collection("users").document(uid)
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(userRef)

            val postsMap = snapshot.get("posts").asStringListMap()
            val updatedDayPosts = postsMap[today]?.toMutableList() ?: mutableListOf()
            updatedDayPosts.add(postId)

            val updatedMap = postsMap.toMutableMap()
            updatedMap[today] = updatedDayPosts

            transaction.update(userRef, "posts", updatedMap)
        }.await()
    }

    override suspend fun updateStreakAfterCompletion() {
        val uid = auth.currentUser?.uid ?: return
        val userRef = firestore.collection("users").document(uid)
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(userRef)
            val lastDateStr = snapshot.getString("last_completed_date")
            val lastDate = lastDateStr?.let { LocalDate.parse(it) }

            val currentStreak = (snapshot.getLong("streak") ?: 0L).toInt()
            val newStreak = when (lastDate) {
                today -> currentStreak
                yesterday -> currentStreak + 1
                else -> 1
            }

            transaction.update(
                userRef, mapOf(
                    "streak" to newStreak,
                    "last_completed_date" to today.toString()
                )
            )
        }.await()
    }

    override suspend fun getTodaySuggestions(): Map<Int, String> {
        val uid = auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")
        val today = LocalDate.now().toString()

        val userSnapshot = firestore.collection("users").document(uid).get().await()
        val allSuggestions = userSnapshot.get("daily_suggestions").asNestedStringMap()
        val todaySuggestions = allSuggestions[today] ?: return emptyMap()

        return todaySuggestions.mapNotNull { (slot, taskId) ->
            slot.toIntOrNull()?.let { it to taskId }
        }.toMap()
    }

    override suspend fun saveTodaySuggestions(suggestions: Map<Int, String>) {
        val uid = auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")
        val today = LocalDate.now().toString()

        val userRef = firestore.collection("users").document(uid)
        val updatePath = "daily_suggestions.$today"

        firestore.runTransaction { transaction ->
            val data = suggestions.mapKeys { it.key.toString() }
            transaction.update(userRef, updatePath, data)
        }.await()
    }

    private fun getCompletedTasks(userDoc: DocumentSnapshot): List<CompletedTask> {
        val dailyTasks = userDoc.get("daily_tasks") as? Map<*, *> ?: return emptyList()

        return dailyTasks.values.flatMap { dayList ->
            (dayList as? List<*>)?.mapNotNull { task ->
                val taskMap = task as? Map<*, *> ?: return@mapNotNull null
                val isCompleted = taskMap["is_completed"] as? Boolean ?: return@mapNotNull null
                if (!isCompleted) return@mapNotNull null

                val id = taskMap["task_id"] as? String ?: return@mapNotNull null
                val title = taskMap["title"] as? String ?: return@mapNotNull null
                val rating = (taskMap["rating"] as? Long)?.toInt() ?: 0

                CompletedTask(
                    id = id,
                    title = title,
                    rating = rating
                )
            } ?: emptyList()
        }
    }

    private fun getActiveTasks(userDoc: DocumentSnapshot): List<String> {
        val dailyTasks = userDoc.get("daily_tasks") as? Map<*, *> ?: return emptyList()
        return dailyTasks.values.flatMap { dayList ->
            (dayList as? List<*>)?.mapNotNull { task ->
                val taskMap = task as? Map<*, *> ?: return@mapNotNull null
                val isCompleted = taskMap["is_completed"] as? Boolean ?: return@mapNotNull null
                val taskId = taskMap["task_id"] as? String

                if (!isCompleted) taskId else null
            } ?: emptyList()
        }
    }

    private fun getCurrentSuggestions(userDoc: DocumentSnapshot): List<String> {
        val today = LocalDate.now().toString()
        val suggestions = userDoc.get("daily_suggestions") as? Map<*, *> ?: return emptyList()
        val todaySuggestions = suggestions[today] as? Map<*, *> ?: return emptyList()
        return todaySuggestions.values.mapNotNull { it as? String }
    }
}
