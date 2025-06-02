package gr.aueb.thriveon.ui.screens.profile.model

import gr.aueb.thriveon.domain.model.Post
import gr.aueb.thriveon.ui.common.mvi.BaseEffect
import gr.aueb.thriveon.ui.common.mvi.BaseEvent
import gr.aueb.thriveon.ui.common.mvi.BaseState
import java.time.LocalDate

interface ProfileContract {
    sealed class Event : BaseEvent {
        data class Init(val userId: String) : Event()

        sealed class NavigationEvent : Event() {
            data object OnHomeClick : Event()
            data object OnFeedClick : Event()
            data object OnSearchClick : Event()
            data object OnNotificationsClick : Event()
            data object OnProfileClick : Event()
            data object OnFriendsClick : Event()
            data object OnTitlesClick : Event()
            data object OnEditProfileClick : Event()
        }

        data object OnDateNext : Event()
        data object OnDatePrevious : Event()
        data class OnReact(val postId: String, val reaction: String) : Event()
        data class OnDeletePost(val postId: String) : Event()

        data object OnDatePickerOpen : Event()
        data class OnDatePicked(val date: LocalDate) : Event()
        data object OnDatePickerDismiss : Event()

        data object OnSendFriendRequest : Event()
        sealed class SendFriendRequestEvent: Event(){
            data object OnRemoveFriendClick : Event()
            data object ConfirmRemoveFriend : Event()
            data object CancelRemoveFriend : Event()
        }
    }

    data class State(
        val errorMessage: String? = null,
        val isLoading: Boolean = false,
        val isTheCurrentUser: Boolean = false,
        val userId: String = "",
        val username: String = "",
        val profilePictureUrl: String = "",
        val equippedTitle: String = "",
        val streak: Int = 0,
        val friendsCount: Int = 0,
        val titlesCount: Int = 0,
        val badges: List<String> = emptyList(),
        val bio: String = "",
        val selectedDate: LocalDate = LocalDate.now(),
        val postPreviews: List<Post> = emptyList(),
        val isCurrentUser: Boolean = false,
        val isDatePickerVisible: Boolean = false,
        val minPostDate: LocalDate? = null,
        val maxPostDate: LocalDate? = null,
        val isFriend: Boolean = false,
        val isFriendRequestSent: Boolean = false,
        val isRemoveFriendDialogVisible: Boolean = false
    ) : BaseState

    sealed class Effect : BaseEffect {

        sealed class NavigationEffect : Effect() {
            data object NavigateToHome : Effect()
            data object NavigateToFeed : Effect()
            data object NavigateToSearch : Effect()
            data object NavigateToNotifications: Effect()
            data object NavigateToProfile : Effect()
            data class NavigateToFriends(val userId: String) : Effect()
            data class NavigateToTitles(val userId: String) : Effect()
            data object NavigateToEditProfile : Effect()
        }

        sealed class SnackbarEffect{
            data class ShowSnackbar(val message: String) : Effect()
            data object DismissSnackbar : Effect()
        }
    }
}
