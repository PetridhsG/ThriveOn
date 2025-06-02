package gr.aueb.thriveon.ui.screens.titles

import androidx.lifecycle.viewModelScope
import gr.aueb.thriveon.domain.interactors.UserInteractor
import gr.aueb.thriveon.ui.common.mvi.BaseViewModel
import gr.aueb.thriveon.ui.screens.titles.model.TitlesContract
import kotlinx.coroutines.launch

class TitlesViewModel(
    private val userInteractor: UserInteractor,
) :
    BaseViewModel<TitlesContract.Event, TitlesContract.State, TitlesContract.Effect>() {

    override fun setInitialState(): TitlesContract.State = TitlesContract.State()

    override fun handleEvents(event: TitlesContract.Event) {
        when (event) {
            is TitlesContract.Event.Init -> {
                setState {
                    copy(userId = event.userId)
                }
                loadMilestones(event.userId)
            }

            TitlesContract.Event.OnBackClick -> {
                setEffect { TitlesContract.Effect.NavigateBack }
            }

            TitlesContract.Event.OnProgressClick -> {
                setEffect { TitlesContract.Effect.NavigateToProgress(currentState.userId) }
            }
        }
    }

    private fun loadMilestones(userId: String) {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val milestones = userInteractor.getUserMilestones(userId)
            setState { copy(milestones = milestones) }
            setState { copy(isLoading = false) }
        }
    }
}
