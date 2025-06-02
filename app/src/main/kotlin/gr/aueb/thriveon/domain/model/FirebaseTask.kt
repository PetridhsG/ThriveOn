package gr.aueb.thriveon.domain.model

data class FirebaseTask(
    val id: String,
    val title: String,
    val categoryTitle: String,
    val categoryIcon: String,
    val defaultPicture: String = ""
)
