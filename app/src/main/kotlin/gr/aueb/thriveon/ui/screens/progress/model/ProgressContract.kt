package gr.aueb.thriveon.ui.screens.progress.model

import gr.aueb.thriveon.domain.model.CategoryProgress
import gr.aueb.thriveon.ui.common.mvi.BaseEffect
import gr.aueb.thriveon.ui.common.mvi.BaseEvent
import gr.aueb.thriveon.ui.common.mvi.BaseState

interface ProgressContract {
    sealed class Event : BaseEvent {
        data class Init(val userId: String) : Event()
        data object OnBackClick : Event()
    }

    data class State(
        val isLoading: Boolean = false,
        val userId: String = "",
        val categoryProgress: List<CategoryProgress> = emptyList(),
    ) : BaseState

    sealed class Effect : BaseEffect {
        data object NavigateBack : Effect()
    }
}
