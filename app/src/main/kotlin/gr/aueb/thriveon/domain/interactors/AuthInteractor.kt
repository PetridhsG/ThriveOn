package gr.aueb.thriveon.domain.interactors

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import gr.aueb.thriveon.core.resources.ResourceProvider
import kotlinx.coroutines.tasks.await
import gr.aueb.thriveon.R

interface AuthInteractor {
    suspend fun signInWithGoogle(idToken: String): AuthResult
    suspend fun signOut()
    fun getCurrentUserId(): String?
}

@Suppress("DEPRECATION")
class AuthInteractorImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val resourceProvider: ResourceProvider
) : AuthInteractor {
    override suspend fun signInWithGoogle(idToken: String): AuthResult {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val user = result.user ?: return AuthResult.Error("Authentication failed")

            val uid = user.uid
            val docRef = firestore.collection("users").document(uid)
            val snapshot = docRef.get().await()

            if (!snapshot.exists()) {
                val userData = mapOf(
                    "user_id" to uid,
                    "username" to (user.displayName?.take(20) ?: ""),
                    "email" to user.email,
                    "profile_picture_url" to user.photoUrl?.toString(),
                    "friends" to listOf(uid),
                    "badges" to listOf<String>(),
                    "titles" to listOf<String>(),
                    "bio" to "",
                    "equipped_title" to "",
                    "streak" to 0,
                    "rerolls" to 5,
                    "preferences" to listOf<String>(),
                    "category_progress" to mapOf(
                        "Learning & Growth" to 0,
                        "Physical Exercise & Health" to 0,
                        "Adventures & Discoveries" to 0,
                        "Cooking & Nutrition" to 0,
                        "Volunteering & Good Deeds" to 0,
                        "Expression & Creativity" to 0,
                        "Cleaning & Space Organization" to 0,
                        "Sustainability & Environment" to 0,
                        "Focus & Work Efficiency" to 0
                    ),
                    "daily_tasks" to mapOf<String, List<Map<String, Any>>>(),
                    "created_at" to FieldValue.serverTimestamp()
                )
                docRef.set(userData).await()

                val allUsersData = mapOf(
                    "user_id" to uid,
                    "username" to (user.displayName?.take(16) ?: ""),
                    "equipped_title" to "",
                    "profile_picture_url" to user.photoUrl?.toString(),
                    "preferences" to listOf<String>()
                )
                firestore.collection("all_users").document(uid).set(allUsersData).await()

                return AuthResult.NewUser
            }

            val prefs = snapshot.get("preferences") as? List<*> ?: emptyList<Any>()
            return if (prefs.isEmpty()) AuthResult.NewUser else AuthResult.ExistingUser

        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun signOut() {
        auth.signOut()

        val context = resourceProvider.provideContext()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(resourceProvider.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val googleClient = GoogleSignIn.getClient(context, gso)
        googleClient.signOut().await()
    }

    override fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
}

sealed class AuthResult {
    object NewUser : AuthResult()
    object ExistingUser : AuthResult()
    data class Error(val message: String) : AuthResult()
}
