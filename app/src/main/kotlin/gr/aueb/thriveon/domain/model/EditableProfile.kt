package gr.aueb.thriveon.domain.model

data class EditableProfile(
    val username: String,
    val bio: String,
    val profilePictureUrl: String,
    val equippedTitle: String?,
    val titles: List<String>
)
