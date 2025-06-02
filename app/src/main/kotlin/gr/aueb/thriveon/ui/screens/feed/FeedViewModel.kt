package gr.aueb.thriveon.ui.screens.feed

import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import gr.aueb.thriveon.domain.interactors.PostInteractor
import gr.aueb.thriveon.domain.interactors.UserInteractor
import gr.aueb.thriveon.ui.common.mvi.BaseViewModel
import gr.aueb.thriveon.ui.screens.feed.model.FeedContract
import gr.aueb.thriveon.ui.screens.feed.model.FeedContract.Effect.*
import kotlinx.coroutines.launch

class FeedViewModel(
    private val postInteractor: PostInteractor,
    private val userInteractor: UserInteractor
) : BaseViewModel<FeedContract.Event, FeedContract.State, FeedContract.Effect>() {

    override fun setInitialState(): FeedContract.State = FeedContract.State()

    override fun handleEvents(event: FeedContract.Event) {
        when (event) {
            FeedContract.Event.Init -> {
                observeFeedPosts()
            }

            FeedContract.Event.NavigationEvent.OnHomeClick -> {
                setEffect { NavigationEffect.NavigateToHome }
            }

            FeedContract.Event.NavigationEvent.OnSearchClick -> {
                setEffect { NavigationEffect.NavigateToSearch }
            }

            FeedContract.Event.NavigationEvent.OnNotificationsClick -> {
                setEffect { NavigationEffect.NavigateToNotifications }
            }

            FeedContract.Event.NavigationEvent.OnProfileClick -> {
                setEffect { NavigationEffect.NavigateToProfile }
            }

            is FeedContract.Event.OnUserProfileClick -> {
                setEffect { NavigationEffect.NavigateToUserProfile(event.userId) }
            }

            is FeedContract.Event.OnUserNameClick -> {
                setEffect { NavigationEffect.NavigateToUserProfile(event.userId) }
            }

            is FeedContract.Event.OnReact -> {
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
                val postId = event.postId
                val selectedReaction = event.reaction

                val updatedPosts = currentState.posts.map { post ->
                    if (post.postId != postId) return@map post

                    val previousReaction = post.userReacted[userId]
                    val updatedReacts = post.reacts.toMutableMap()
                    val updatedUserReacted = post.userReacted.toMutableMap()

                    if (previousReaction != null) {
                        updatedReacts[previousReaction] = (updatedReacts[previousReaction] ?: 1) - 1
                    }

                    if (previousReaction == selectedReaction) {
                        updatedUserReacted.remove(userId)
                    } else {
                        updatedReacts[selectedReaction] = (updatedReacts[selectedReaction] ?: 0) + 1
                        updatedUserReacted[userId] = selectedReaction
                    }

                    post.copy(reacts = updatedReacts, userReacted = updatedUserReacted)
                }

                setState {
                    copy(posts = updatedPosts)
                }

                viewModelScope.launch {
                    postInteractor.reactToPost(postId, selectedReaction)
                }
            }

            is FeedContract.Event.LoadReactedUsers -> {
                viewModelScope.launch {
                    setState { copy(isReactedUsersLoading = true) }
                    try {
                        val users = userInteractor.getUsersByIds(event.userIds)
                        setState {
                            copy(
                                reactedUsers = users,
                                isReactedUsersLoading = false
                            )
                        }
                    } catch (_: Exception) {
                        setState { copy(isReactedUsersLoading = false) }
                    }
                }
            }
        }
    }

    private fun observeFeedPosts() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }

            try {
                postInteractor.getFeedPostsForFriends().collect { posts ->
                    setState {
                        copy(posts = posts, isLoading = false)
                    }
                }
            } catch (_: Exception) {
                setState { copy(isLoading = false) }
            }
        }
    }
}
