package gr.aueb.thriveon.domain.model

data class Post(
    val postId: String,
    val userId: String,
    val username: String,
    val taskTitle: String,
    val taskCategory: String,
    val userProfilePictureUrl: String,
    val imageUrl: String,
    val reacts: Map<String, Int>,
    val timestamp: Long,
    val userReacted: Map<String, String> = emptyMap()
)
