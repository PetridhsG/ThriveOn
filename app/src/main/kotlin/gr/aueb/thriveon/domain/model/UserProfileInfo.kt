package gr.aueb.thriveon.domain.model

data class UserProfileInfo(
    val username: String,
    val profilePictureUrl: String,
    val equippedTitle: String,
    val streak: Int,
    val friendsCount: Int,
    val titlesCount: Int,
    val badges: List<String>,
    val bio: String,
    val isFriend: Boolean,
    val isFriendRequestSent: Boolean
)
