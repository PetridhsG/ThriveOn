package gr.aueb.thriveon.ui.screens.progress

import androidx.lifecycle.viewModelScope
import gr.aueb.thriveon.domain.interactors.UserInteractor
import gr.aueb.thriveon.ui.common.mvi.BaseViewModel
import gr.aueb.thriveon.ui.screens.progress.model.ProgressContract
import kotlinx.coroutines.launch

class ProgressViewModel(
    private val interactor: UserInteractor
) : BaseViewModel<ProgressContract.Event, ProgressContract.State, ProgressContract.Effect>() {

    override fun setInitialState(): ProgressContract.State = ProgressContract.State()

    override fun handleEvents(event: ProgressContract.Event) {
        when (event) {
            is ProgressContract.Event.Init -> {
                setState { copy(isLoading = true) }

                viewModelScope.launch {
                    val uiProgressModels = interactor.getUserCategoryProgressWithMilestones(event.userId)

                    setState {
                        copy(
                            isLoading = false,
                            userId = event.userId,
                            categoryProgress = uiProgressModels
                        )
                    }
                }
            }

            ProgressContract.Event.OnBackClick -> {
                setEffect{
                    ProgressContract.Effect.NavigateBack
                }
            }
        }
    }
}
