package gr.aueb.thriveon.ui.screens.friends.model

import gr.aueb.thriveon.domain.model.UserCardInfo
import gr.aueb.thriveon.ui.common.mvi.BaseEffect
import gr.aueb.thriveon.ui.common.mvi.BaseEvent
import gr.aueb.thriveon.ui.common.mvi.BaseState

interface FriendsContract {
    sealed class Event : BaseEvent {
        data class Init(val userId: String) : Event()
        data object OnBackClick : Event()
        data class OnUserProfileClick(val userId: String) : Event()
    }

    data class State(
        val isLoading: Boolean = false,
        val friends: List<UserCardInfo> = emptyList(),
    ) : BaseState

    sealed class Effect : BaseEffect {
        data object NavigateBack : Effect()
        data class NavigateToUserProfile(val userId: String) : Effect()
    }
}
