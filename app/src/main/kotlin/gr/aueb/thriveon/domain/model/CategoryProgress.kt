package gr.aueb.thriveon.domain.model

import java.time.LocalDate

data class CategoryProgress(
    val category: String,
    val progress: Int,
    val milestones: List<MilestoneTitle>,
    val currentMilestone: MilestoneTitle?,
    val defaultImageUrl: String,
    val badge: String?,
    val completedTasks: List<CompletedTask>
)

data class CompletedTask(
    val taskId: String,
    val taskTitle: String,
    val date: LocalDate
)
