package gr.aueb.thriveon.ui.screens.search.model

import gr.aueb.thriveon.domain.model.UserCardInfo
import gr.aueb.thriveon.ui.common.mvi.BaseEffect
import gr.aueb.thriveon.ui.common.mvi.BaseEvent
import gr.aueb.thriveon.ui.common.mvi.BaseState

interface SearchContract {
    sealed class Event : BaseEvent {
        data object Init : Event()

        sealed class NavigationEvent : Event() {
            data object OnHomeClick : Event()
            data object OnFeedClick : Event()
            data object OnNotificationsClick : Event()
            data object OnProfileClick : Event()
        }

        data class OnSearchInput(val query: String) : Event()
        data object ClearInputField: Event()
        data class OnUserClick(val userId: String) : Event()

    }

    data class State(
        val isLoading: Boolean = false,
        val searchQuery: String = "",
        val results: List<UserCardInfo> = emptyList(),
    ) : BaseState

    sealed class Effect : BaseEffect {

        sealed class NavigationEffect : Effect() {
            data object NavigateToHome : Effect()
            data object NavigateToFeed : Effect()
            data object NavigateToNotifications : Effect()
            data object NavigateToProfile : Effect()
            data class NavigateToUserProfile(val userId: String) : Effect()
        }

    }
}
