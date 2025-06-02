package gr.aueb.thriveon.ui.screens.notifications

import androidx.lifecycle.viewModelScope
import gr.aueb.thriveon.core.resources.ResourceProvider
import gr.aueb.thriveon.domain.interactors.NotificationsInteractor
import gr.aueb.thriveon.domain.model.NotificationItem
import gr.aueb.thriveon.ui.common.mvi.BaseViewModel
import gr.aueb.thriveon.ui.screens.notifications.model.NotificationsContract
import kotlinx.coroutines.launch
import gr.aueb.thriveon.R

class NotificationsViewModel(
    private val interactor: NotificationsInteractor,
    private val resourceProvider: ResourceProvider,
) : BaseViewModel<NotificationsContract.Event, NotificationsContract.State, NotificationsContract.Effect>() {

    override fun setInitialState(): NotificationsContract.State = NotificationsContract.State()

    override fun handleEvents(event: NotificationsContract.Event) {
        when (event) {
            NotificationsContract.Event.Init -> {
                observeNotifications()
            }

            is NotificationsContract.Event.AcceptFriendRequest -> {
                acceptFriendRequest(event.notification)
            }

            is NotificationsContract.Event.DeleteFriendRequest -> {
                deleteFriendRequest(event.notification)
            }

            is NotificationsContract.Event.DeleteReaction -> {
                deleteReaction(event.notification)
            }

            NotificationsContract.Event.NavigationEvent.OnHomeClick -> {
                setEffect { NotificationsContract.Effect.NavigationEffect.NavigateToHome }
            }

            NotificationsContract.Event.NavigationEvent.OnFeedClick -> {
                setEffect { NotificationsContract.Effect.NavigationEffect.NavigateToFeed }
            }

            NotificationsContract.Event.NavigationEvent.OnSearchClick -> {
                setEffect { NotificationsContract.Effect.NavigationEffect.NavigateToSearch }
            }

            NotificationsContract.Event.NavigationEvent.OnProfileClick -> {
                setEffect { NotificationsContract.Effect.NavigationEffect.NavigateToProfile }
            }

            is NotificationsContract.Event.NavigationEvent.OnUserProfileClick -> {
                setEffect {
                    NotificationsContract.Effect.NavigationEffect.NavigateToUserProfile(
                        event.userId
                    )
                }
            }
        }
    }

    private fun observeNotifications() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            interactor.observeUserNotifications().collect { notifications ->
                setState {
                    copy(
                        notifications = notifications,
                        isInitialized = true,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun acceptFriendRequest(notification: NotificationItem) {
        viewModelScope.launch {
            interactor.acceptFriendRequest(notification)
            showSnackbar(resourceProvider.getString(R.string.notifications_friend_request_accepted))
        }
    }

    private fun deleteFriendRequest(notification: NotificationItem) {
        viewModelScope.launch {
            interactor.deleteFriendRequest(notification)
            showSnackbar(resourceProvider.getString(R.string.notifications_friend_request_deleted))
        }
    }

    private fun deleteReaction(notification: NotificationItem) {
        viewModelScope.launch {
            interactor.deleteReaction(notification)
            showSnackbar(resourceProvider.getString(R.string.notifications_Reaction_deleted))
        }
    }

    private fun showSnackbar(message: String) {
        setEffect {
            NotificationsContract.Effect.SnackBarEffect.DismissSnackbar
        }
        setEffect {
            NotificationsContract.Effect.SnackBarEffect.ShowSnackbar(message)
        }
    }
}
