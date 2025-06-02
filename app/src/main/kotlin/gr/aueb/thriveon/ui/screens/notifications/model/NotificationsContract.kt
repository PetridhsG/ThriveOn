package gr.aueb.thriveon.ui.screens.notifications.model

import gr.aueb.thriveon.domain.model.NotificationItem
import gr.aueb.thriveon.ui.common.mvi.BaseEffect
import gr.aueb.thriveon.ui.common.mvi.BaseEvent
import gr.aueb.thriveon.ui.common.mvi.BaseState

interface NotificationsContract {
    sealed class Event : BaseEvent {
        data object Init : Event()

        sealed class NavigationEvent() : Event() {
            data object OnHomeClick : Event()
            data object OnFeedClick : Event()
            data object OnSearchClick : Event()
            data object OnProfileClick : Event()
            data class OnUserProfileClick(val userId: String) : Event()
        }

        data class AcceptFriendRequest(val notification: NotificationItem) : Event()
        data class DeleteFriendRequest(val notification: NotificationItem) : Event()
        data class DeleteReaction(val notification: NotificationItem) : Event()
    }

    data class State(
        val isLoading: Boolean = false,
        val isInitialized: Boolean = false,
        val notifications: List<NotificationItem> = emptyList()
    ) : BaseState

    sealed class Effect : BaseEffect {

        sealed class NavigationEffect : Effect() {
            data object NavigateToHome : Effect()
            data object NavigateToFeed : Effect()
            data object NavigateToSearch : Effect()
            data object NavigateToProfile : Effect()
            data class NavigateToUserProfile(val userId: String) : Effect()
        }

        sealed class SnackBarEffect : Effect() {
            data class ShowSnackbar(val message: String) : Effect()
            data object DismissSnackbar : Effect()
        }
    }
}
