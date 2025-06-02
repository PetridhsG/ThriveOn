package gr.aueb.thriveon.domain.model

import com.google.firebase.Timestamp

data class NotificationItem(
    val id: String = "",
    val type: String = "",
    val from: String = "",
    val fromUsername: String = "",
    val fromProfilePictureUrl: String = "",
    val fromEquippedTitle: String = "",
    val isAccepted: Boolean? = null,
    val postId: String? = null,
    val message: String? = null,
    val timestamp: Timestamp? = null
)
