package gr.aueb.thriveon.domain.model

data class UserCardInfo(
    val userId: String,
    val username: String,
    val profilePictureUrl: String,
    val equippedTitle: String = "",
    val preferences: List<String> = emptyList()
)
