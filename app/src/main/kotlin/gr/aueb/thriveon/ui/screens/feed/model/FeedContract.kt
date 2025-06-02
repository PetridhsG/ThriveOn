package gr.aueb.thriveon.ui.screens.feed.model

import gr.aueb.thriveon.domain.model.Post
import gr.aueb.thriveon.domain.model.UserCardInfo
import gr.aueb.thriveon.ui.common.mvi.BaseEffect
import gr.aueb.thriveon.ui.common.mvi.BaseEvent
import gr.aueb.thriveon.ui.common.mvi.BaseState

interface FeedContract {
    sealed class Event : BaseEvent {
        data object Init : Event()

        sealed class NavigationEvent() : Event() {
            data object OnHomeClick : Event()
            data object OnSearchClick : Event()
            data object OnNotificationsClick : Event()
            data object OnProfileClick : Event()
        }

        data class OnUserProfileClick(val userId: String) : Event()
        data class OnUserNameClick(val userId: String) : Event()
        data class OnReact(val postId: String, val reaction: String) : Event()
        data class LoadReactedUsers(val userIds: List<String>) : Event()
    }

    data class State(
        val posts: List<Post> = emptyList(),
        val isLoading: Boolean = false,
        val reactedUsers: List<UserCardInfo> = emptyList(),
        val isReactedUsersLoading: Boolean = false
    ) : BaseState

    sealed class Effect : BaseEffect {
        sealed class NavigationEffect : Effect() {
            data object NavigateToHome : Effect()
            data object NavigateToSearch : Effect()
            data object NavigateToNotifications : Effect()
            data object NavigateToProfile : Effect()
            data class NavigateToUserProfile(val userId: String) : Effect()
        }
    }
}
