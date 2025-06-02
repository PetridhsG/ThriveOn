package gr.aueb.thriveon.domain.interactors

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import gr.aueb.thriveon.domain.model.UserCardInfo
import gr.aueb.thriveon.domain.utils.asTypedList
import kotlinx.coroutines.tasks.await

interface SearchInteractor {
    suspend fun searchUsersByUsername(query: String): List<UserCardInfo>
}

class SearchInteractorImpl(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : SearchInteractor {

    override suspend fun searchUsersByUsername(query: String): List<UserCardInfo> {
        val currentUserId = auth.currentUser?.uid ?: return emptyList()

        val currentUserSnapshot = firestore.collection("users")
            .document(currentUserId)
            .get()
            .await()
        val myPreferences = currentUserSnapshot.get("preferences").asTypedList<String>()

        val allUserSnapshots = firestore.collection("all_users").get().await()

        val allUsers = allUserSnapshots.documents.mapNotNull { doc ->
            val userId = doc.getString("user_id") ?: return@mapNotNull null
            if (userId == currentUserId) return@mapNotNull null

            val username = doc.getString("username") ?: return@mapNotNull null
            val profileUrl = doc.getString("profile_picture_url") ?: ""
            val title = doc.getString("equipped_title") ?: ""
            val prefs = doc.get("preferences").asTypedList<String>()

            UserCardInfo(
                userId = userId,
                username = username,
                profilePictureUrl = profileUrl,
                equippedTitle = title,
                preferences = prefs
            )
        }

        return if (query.isBlank()) {
            val suggested = allUsers.filter { user ->
                user.preferences.any { it in myPreferences }
            }.shuffled()

            val nonSuggested = allUsers
                .filterNot { it.userId in suggested.map { it.userId } }
                .shuffled()

            (suggested + nonSuggested).take(10)
        } else {
            allUsers.filter {
                it.username.contains(query, ignoreCase = true)
            }
        }
    }
}
