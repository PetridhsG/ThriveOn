package gr.aueb.thriveon.ui.screens.titles.model

import gr.aueb.thriveon.domain.model.MilestoneTitle
import gr.aueb.thriveon.ui.common.mvi.BaseEffect
import gr.aueb.thriveon.ui.common.mvi.BaseEvent
import gr.aueb.thriveon.ui.common.mvi.BaseState

interface TitlesContract {
    sealed class Event : BaseEvent {
        data class Init(val userId: String) : Event()
        data object OnBackClick : Event()
        data object OnProgressClick : Event()
    }

    data class State(
        val userId: String = "",
        val isLoading: Boolean = false,
        val milestones: List<MilestoneTitle> = emptyList(),
    ) : BaseState

    sealed class Effect : BaseEffect {
        data object NavigateBack : Effect()
        data class NavigateToProgress(val userId: String) : Effect()
    }
}
