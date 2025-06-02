package gr.aueb.thriveon.ui.screens.profile

import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import gr.aueb.thriveon.R
import gr.aueb.thriveon.core.resources.ResourceProvider
import gr.aueb.thriveon.domain.interactors.PostInteractor
import gr.aueb.thriveon.domain.interactors.UserInteractor
import gr.aueb.thriveon.ui.common.mvi.BaseViewModel
import gr.aueb.thriveon.ui.screens.profile.model.ProfileContract
import gr.aueb.thriveon.ui.screens.profile.model.ProfileContract.Effect.*
import gr.aueb.thriveon.ui.screens.profile.model.ProfileContract.Effect.NavigationEffect.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.coroutines.cancellation.CancellationException

class ProfileViewModel(
    private val profileInteractor: UserInteractor,
    private val feedInteractor: PostInteractor,
    private val resourceProvider: ResourceProvider,
) : BaseViewModel<ProfileContract.Event, ProfileContract.State, ProfileContract.Effect>() {

    private var profileJob: Job? = null
    private var postsJob: Job? = null

    override fun setInitialState(): ProfileContract.State = ProfileContract.State()

    override fun handleEvents(event: ProfileContract.Event) {
        when (event) {
            is ProfileContract.Event.Init -> {
                val loggedInUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
                val effectiveUserId = event.userId
                val isCurrentUser = effectiveUserId == loggedInUserId

                setState {
                    copy(
                        userId = effectiveUserId,
                        isCurrentUser = isCurrentUser
                    )
                }

                loadUserProfile()
            }

            ProfileContract.Event.NavigationEvent.OnHomeClick -> {
                setEffect { NavigateToHome }
            }

            ProfileContract.Event.NavigationEvent.OnFeedClick -> {
                setEffect { NavigateToFeed }
            }

            ProfileContract.Event.NavigationEvent.OnSearchClick -> {
                setEffect { NavigateToSearch }
            }

            ProfileContract.Event.NavigationEvent.OnNotificationsClick -> {
                setEffect { NavigateToNotifications }
            }

            ProfileContract.Event.NavigationEvent.OnProfileClick -> {
                setEffect { NavigateToProfile }
            }

            ProfileContract.Event.NavigationEvent.OnFriendsClick -> {
                setEffect { NavigateToFriends(currentState.userId) }
            }

            ProfileContract.Event.NavigationEvent.OnTitlesClick -> {
                setEffect { NavigateToTitles(currentState.userId) }
            }

            ProfileContract.Event.NavigationEvent.OnEditProfileClick -> {
                setEffect { NavigateToEditProfile }
            }

            ProfileContract.Event.OnDateNext -> {
                val next = currentState.selectedDate.plusDays(1)
                setState { copy(selectedDate = next) }
                loadPostsForDate(next)
            }

            ProfileContract.Event.OnDatePrevious -> {
                val prev = currentState.selectedDate.minusDays(1)
                setState { copy(selectedDate = prev) }
                loadPostsForDate(prev)
            }

            is ProfileContract.Event.OnReact -> {
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

                setState {
                    val updatedPosts = postPreviews.map { post ->
                        if (post.postId != event.postId) return@map post

                        val previous = post.userReacted[userId]
                        val updatedReacts = post.reacts.toMutableMap()
                        val updatedUserReacted = post.userReacted.toMutableMap()

                        if (previous != null) {
                            updatedReacts[previous] = (updatedReacts[previous] ?: 1) - 1
                        }

                        if (previous == event.reaction) {
                            updatedUserReacted.remove(userId)
                        } else {
                            updatedReacts[event.reaction] = (updatedReacts[event.reaction] ?: 0) + 1
                            updatedUserReacted[userId] = event.reaction
                        }

                        post.copy(reacts = updatedReacts, userReacted = updatedUserReacted)
                    }

                    copy(postPreviews = updatedPosts)
                }

                viewModelScope.launch {
                    feedInteractor.reactToPost(event.postId, event.reaction)
                }
            }

            is ProfileContract.Event.OnDeletePost -> {
                viewModelScope.launch {
                    feedInteractor.deletePost(event.postId)
                    setState {
                        copy(postPreviews = postPreviews.filterNot { it.postId == event.postId })
                    }
                    showSnackbar(resourceProvider.getString(R.string.toast_post_deleted))
                }
            }

            ProfileContract.Event.OnDatePickerOpen -> {
                setState { copy(isDatePickerVisible = true) }
            }

            ProfileContract.Event.OnDatePickerDismiss -> {
                setState { copy(isDatePickerVisible = false) }
            }

            is ProfileContract.Event.OnDatePicked -> {
                setState {
                    copy(selectedDate = event.date, isDatePickerVisible = false)
                }
                loadPostsForDate(event.date)
            }

            ProfileContract.Event.OnSendFriendRequest -> {
                viewModelScope.launch {
                    try {
                        profileInteractor.sendFriendRequestNotification(currentState.userId)
                        showSnackbar(resourceProvider.getString(R.string.toast_friend_request_sent))

                        setState {
                            copy(isFriendRequestSent = true)
                        }
                    } catch (e: Exception) {
                        showSnackbar(
                            resourceProvider.getString(
                                R.string.toast_friend_request_failed,
                                e.message ?: "unknown"
                            )
                        )
                    }
                }
            }

            ProfileContract.Event.SendFriendRequestEvent.OnRemoveFriendClick -> {
                setState { copy(isRemoveFriendDialogVisible = true) }
            }

            ProfileContract.Event.SendFriendRequestEvent.CancelRemoveFriend -> {
                setState { copy(isRemoveFriendDialogVisible = false) }
            }

            ProfileContract.Event.SendFriendRequestEvent.ConfirmRemoveFriend -> {
                viewModelScope.launch {
                    try {
                        profileInteractor.removeFriend(currentState.userId)
                        setState {
                            copy(
                                isFriend = false,
                                isFriendRequestSent = false,
                                isRemoveFriendDialogVisible = false
                            )
                        }
                        showSnackbar(resourceProvider.getString(R.string.toast_friend_removed))
                    } catch (e: Exception) {
                        showSnackbar(
                            resourceProvider.getString(
                                R.string.toast_remove_friend_failed,
                                e.message ?: "unknown"
                            )
                        )
                    }
                }
            }
        }
    }

    private fun loadUserProfile() {
        profileJob?.cancel()
        profileJob = viewModelScope.launch {
            setState { copy(isLoading = true) }

            try {
                profileInteractor.getUserProfile(currentState.userId).collect { profile ->
                    val range = profileInteractor.getPostDateRange(currentState.userId)

                    setState {
                        copy(
                            username = profile.username,
                            profilePictureUrl = profile.profilePictureUrl,
                            equippedTitle = profile.equippedTitle,
                            streak = profile.streak,
                            friendsCount = if (profile.friendsCount > 0) profile.friendsCount - 1 else 0,
                            titlesCount = profile.titlesCount,
                            badges = profile.badges,
                            bio = profile.bio,
                            minPostDate = range?.first,
                            maxPostDate = range?.second,
                            isFriend = profile.isFriend,
                            isFriendRequestSent = profile.isFriendRequestSent
                        )
                    }

                    observePostsForDate(currentState.selectedDate)
                    setState { copy(isLoading = false) }
                }
            } catch (e: CancellationException) {
                throw e
            }
            catch (e: IllegalArgumentException) {
                e.printStackTrace()
                setState {
                    copy(
                        isLoading = false,
                        errorMessage = resourceProvider.getString(R.string.error_occurred)
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                setState {
                    copy(
                        isLoading = false,
                        errorMessage = resourceProvider.getString(R.string.error_occurred)
                    )
                }
            }
        }
    }

    private fun observePostsForDate(date: LocalDate) {
        postsJob?.cancel()
        postsJob = viewModelScope.launch {
            feedInteractor.getPostsForDate(currentState.userId, date).collect { posts ->
                setState { copy(postPreviews = posts) }
            }
        }
    }

    private fun loadPostsForDate(date: LocalDate) {
        postsJob?.cancel()
        postsJob = viewModelScope.launch {
            feedInteractor.getPostsForDate(currentState.userId, date).collect { posts ->
                setState { copy(postPreviews = posts) }
            }
        }
    }

    private fun showSnackbar(message: String) {
        setEffect { SnackbarEffect.DismissSnackbar }
        setEffect { SnackbarEffect.ShowSnackbar(message) }
    }
}
