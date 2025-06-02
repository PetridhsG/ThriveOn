package gr.aueb.thriveon.ui.screens.friends

import androidx.lifecycle.viewModelScope
import gr.aueb.thriveon.domain.interactors.UserInteractor
import gr.aueb.thriveon.ui.common.mvi.BaseViewModel
import gr.aueb.thriveon.ui.screens.friends.model.FriendsContract
import kotlinx.coroutines.launch

class FriendsViewModel(
    private val interactor: UserInteractor,
) : BaseViewModel<FriendsContract.Event, FriendsContract.State, FriendsContract.Effect>() {

    override fun setInitialState(): FriendsContract.State = FriendsContract.State()

    override fun handleEvents(event: FriendsContract.Event) {
        when (event) {
            is FriendsContract.Event.Init -> {
                loadFriends(event.userId)
            }

            FriendsContract.Event.OnBackClick -> {
                setEffect { FriendsContract.Effect.NavigateBack }
            }

            is FriendsContract.Event.OnUserProfileClick -> {
                setEffect { FriendsContract.Effect.NavigateToUserProfile(event.userId) }
            }
        }
    }

    private fun loadFriends(userId: String) {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val friends = interactor.getFriendsForUser(userId)
                .filter { it.userId != userId }
            setState { copy(friends = friends) }
            setState { copy(isLoading = false) }
        }
    }
}
