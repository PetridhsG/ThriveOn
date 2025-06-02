package gr.aueb.thriveon.ui.screens.search

import androidx.lifecycle.viewModelScope
import gr.aueb.thriveon.domain.interactors.SearchInteractor
import gr.aueb.thriveon.ui.common.mvi.BaseViewModel
import gr.aueb.thriveon.ui.screens.search.model.SearchContract
import gr.aueb.thriveon.ui.screens.search.model.SearchContract.Effect.NavigationEffect.*
import gr.aueb.thriveon.ui.screens.search.model.SearchContract.Event.*
import kotlinx.coroutines.launch

class SearchViewModel(
    private val interactor: SearchInteractor
) : BaseViewModel<SearchContract.Event, SearchContract.State, SearchContract.Effect>() {

    override fun setInitialState(): SearchContract.State = SearchContract.State()

    override fun handleEvents(event: SearchContract.Event) {
        when (event) {
            Init -> {
                setEvent(OnSearchInput(currentState.searchQuery))
            }

            is OnSearchInput -> {
                setState { copy(searchQuery = event.query) }
                viewModelScope.launch {
                    setState { copy(isLoading = true) }
                    val results = interactor.searchUsersByUsername(event.query)
                    setState {
                        copy(
                            results = results,
                            isLoading = false
                        )
                    }
                }
            }

            is OnUserClick -> {
                setEffect { NavigateToUserProfile(event.userId) }
            }

            NavigationEvent.OnHomeClick -> {
                setEffect { NavigateToHome }
            }

            NavigationEvent.OnFeedClick -> {
                setEffect { NavigateToFeed }
            }

            NavigationEvent.OnNotificationsClick -> {
                setEffect { NavigateToNotifications }
            }

            NavigationEvent.OnProfileClick -> {
                setEffect { NavigateToProfile }
            }

            ClearInputField -> {
                setState { copy(searchQuery = "") }
                setEvent(OnSearchInput(currentState.searchQuery))
            }
        }
    }
}
