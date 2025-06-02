package gr.aueb.thriveon.domain.model

data class MilestoneTitle(
    val category: String,
    val milestoneTitle: String,
    val badge: String,
    val completedCount: Int,
    val requiredCount: Int
)
