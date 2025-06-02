package gr.aueb.thriveon.network.model

import kotlinx.serialization.Serializable

@Serializable
data class UserRequest(
    val preferences: List<String>,
    val categoryProgress: Map<String, Int>,
    val completedTasks: List<CompletedTask>,
    val activeTasks: List<String>,
    val currentSuggestions: List<String>,
    val allTasks: List<SerializableTask>,
    val suggestionHistory: List<String>
)

@Serializable
data class SerializableTask(
    val id: String,
    val title: String,
    val categoryTitle: String,
    val categoryIcon: String,
    val defaultPicture: String
)

@Serializable
data class CompletedTask(
    val id: String,
    val title: String,
    val rating: Int
)
