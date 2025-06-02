package gr.aueb.thriveon.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    @SerialName("suggested_task_ids")
    val suggestedTaskIds: List<String>
)
