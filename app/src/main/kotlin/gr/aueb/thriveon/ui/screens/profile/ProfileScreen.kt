package gr.aueb.thriveon.ui.screens.profile

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import gr.aueb.thriveon.R
import gr.aueb.thriveon.domain.model.Post
import gr.aueb.thriveon.ui.common.components.BottomNavBar
import gr.aueb.thriveon.ui.common.components.BottomNavDestination
import gr.aueb.thriveon.ui.common.components.CustomSnackbarHost
import gr.aueb.thriveon.ui.common.components.ThriveOnCircularProgressIndicator
import gr.aueb.thriveon.ui.common.components.spacers.VSpacer
import gr.aueb.thriveon.ui.common.components.wrap.WrapAlertDialog
import gr.aueb.thriveon.ui.common.components.wrap.WrapButton
import gr.aueb.thriveon.ui.common.components.wrap.WrapScaffold
import gr.aueb.thriveon.ui.screens.feed.emojiSymbol
import gr.aueb.thriveon.ui.screens.profile.model.ProfileContract
import gr.aueb.thriveon.ui.theme.Colors
import gr.aueb.thriveon.ui.theme.Typography
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userId: String,
    state: ProfileContract.State,
    onEvent: (ProfileContract.Event) -> Unit,
    effect: Flow<ProfileContract.Effect>,
    onNavigateToHome: () -> Unit,
    onNavigateToFeed: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToFriends: (String) -> Unit,
    onNavigateToTitles: (String) -> Unit,
    onNavigateToEditProfile: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        onEvent(ProfileContract.Event.Init(userId))
        effect.collect { effect ->
            when (effect) {
                ProfileContract.Effect.NavigationEffect.NavigateToHome -> onNavigateToHome()
                ProfileContract.Effect.NavigationEffect.NavigateToFeed -> onNavigateToFeed()
                ProfileContract.Effect.NavigationEffect.NavigateToSearch -> onNavigateToSearch()
                ProfileContract.Effect.NavigationEffect.NavigateToNotifications -> onNavigateToNotifications()
                ProfileContract.Effect.NavigationEffect.NavigateToProfile -> onNavigateToProfile()
                ProfileContract.Effect.NavigationEffect.NavigateToEditProfile -> onNavigateToEditProfile()
                is ProfileContract.Effect.NavigationEffect.NavigateToFriends -> onNavigateToFriends(
                    effect.userId
                )

                is ProfileContract.Effect.NavigationEffect.NavigateToTitles -> onNavigateToTitles(
                    effect.userId
                )

                ProfileContract.Effect.SnackbarEffect.DismissSnackbar -> snackbarHostState.currentSnackbarData?.dismiss()

                is ProfileContract.Effect.SnackbarEffect.ShowSnackbar -> coroutineScope.launch {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = effect.message,
                            duration = SnackbarDuration.Indefinite
                        )
                    }

                    coroutineScope.launch {
                        delay(3000)
                        snackbarHostState.currentSnackbarData?.dismiss()
                    }
                }
            }
        }
    }

    WrapScaffold(
        containerColor = MaterialTheme.Colors.backgroundMaroonDark,
        snackbarHost = { CustomSnackbarHost(snackbarHostState) },
        bottomBar = {
            BottomNavBar(
                selected = BottomNavDestination.Profile,
                onNavigate = {
                    when (it) {
                        BottomNavDestination.Home -> onEvent(ProfileContract.Event.NavigationEvent.OnHomeClick)
                        BottomNavDestination.Feed -> onEvent(ProfileContract.Event.NavigationEvent.OnFeedClick)
                        BottomNavDestination.Search -> onEvent(ProfileContract.Event.NavigationEvent.OnSearchClick)
                        BottomNavDestination.Notifications -> onEvent(ProfileContract.Event.NavigationEvent.OnNotificationsClick)
                        BottomNavDestination.Profile -> onEvent(ProfileContract.Event.NavigationEvent.OnProfileClick)
                        else -> {}
                    }
                },
                showProfileAsSelected = state.isCurrentUser
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(top = 2.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
        ) {
            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        ThriveOnCircularProgressIndicator()
                    }
                }

                state.errorMessage != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = state.errorMessage,
                                fontSize = 18.sp,
                                color = MaterialTheme.Colors.textWhite,
                                fontFamily = MaterialTheme.Typography.gantari.fontFamily
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            WrapButton(
                                onClick = { onEvent(ProfileContract.Event.Init(userId)) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.Colors.buttonOrange
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = stringResource(R.string.retry),
                                    color = MaterialTheme.Colors.textBlack,
                                    fontFamily = MaterialTheme.Typography.pragatiNarrow.fontFamily,
                                    fontSize = 18.sp
                                )
                            }
                        }
                    }
                }

                else -> {
                    ProfileHeaderSection(state, onEvent)

                    Spacer(modifier = Modifier.height(8.dp))

                    ProfileInfoRow(state, onEvent)

                    if (state.bio.isNotBlank()) {
                        Text(
                            text = state.bio,
                            fontFamily = MaterialTheme.Typography.gantari.fontFamily,
                            fontSize = 16.sp,
                            color = MaterialTheme.Colors.textWhite,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }

                    VSpacer.Medium()

                    DateNavigationRow(state, onEvent)

                    Spacer(modifier = Modifier.height(8.dp))

                    if (state.postPreviews.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(540.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.no_posts_for_day),
                                fontFamily = MaterialTheme.Typography.gantari.fontFamily,
                                fontSize = 16.sp,
                                color = MaterialTheme.Colors.textWhite
                            )
                        }
                    } else {
                        ProfilePostCarousel(
                            posts = state.postPreviews,
                            showDelete = state.isCurrentUser,
                            onReact = { postId, reaction ->
                                onEvent(ProfileContract.Event.OnReact(postId, reaction))
                            },
                            onDeletePost = { postId ->
                                onEvent(ProfileContract.Event.OnDeletePost(postId))
                            }
                        )
                    }

                    if (state.isRemoveFriendDialogVisible) {
                        RemoveFriendConfirmationDialog(onEvent)
                    }
                }
            }
            DatePickerHandler(state = state, onEvent = onEvent)
        }
    }
}

@Composable
fun ProfileHeaderSection(state: ProfileContract.State, onEvent: (ProfileContract.Event) -> Unit) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = state.username,
            fontFamily = MaterialTheme.Typography.gantari.fontFamily,
            fontSize = 26.sp,
            color = MaterialTheme.Colors.textOrange,
            modifier = Modifier.align(Alignment.Center)
        )

        if (!state.isCurrentUser) {
            val icon = when {
                state.isFriend -> R.drawable.remove_friend
                else -> R.drawable.add_friend
            }

            val iconTint = when {
                state.isFriend -> MaterialTheme.colorScheme.error
                state.isFriendRequestSent -> MaterialTheme.Colors.textOrange.copy(alpha = 0.4f)
                else -> MaterialTheme.Colors.textOrange
            }

            val isEnabled = when {
                state.isFriend -> true
                state.isFriendRequestSent -> false
                else -> true
            }

            IconButton(
                onClick = {
                    when {
                        state.isFriend -> onEvent(ProfileContract.Event.SendFriendRequestEvent.OnRemoveFriendClick)
                        !state.isFriendRequestSent -> onEvent(ProfileContract.Event.OnSendFriendRequest)
                        else -> {}
                    }
                },
                enabled = isEnabled,
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = when {
                        state.isFriend -> stringResource(R.string.remove_friend)
                        else -> stringResource(R.string.send_friend_request)
                    },
                    tint = iconTint
                )
            }
        } else {
            IconButton(
                onClick = {
                    onEvent(ProfileContract.Event.NavigationEvent.OnEditProfileClick)
                },
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.edit_profile),
                    tint = MaterialTheme.Colors.textOrange
                )
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = state.equippedTitle,
            fontFamily = MaterialTheme.Typography.gantari.fontFamily,
            fontSize = 20.sp,
            color = MaterialTheme.Colors.textOrange
        )
    }
}

@Composable
fun ProfileInfoRow(state: ProfileContract.State, onEvent: (ProfileContract.Event) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(contentAlignment = Alignment.BottomEnd) {
                var isProfilePreviewOpen by remember { mutableStateOf(false) }

                Box(
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = state.profilePictureUrl,
                        contentDescription = stringResource(R.string.profile_image_description),
                        modifier = Modifier
                            .size(80.dp)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.Colors.textOrange,
                                shape = CircleShape
                            )
                            .clip(CircleShape)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onLongPress = {
                                        isProfilePreviewOpen = true
                                    }
                                )
                            }
                    )
                }

                if (isProfilePreviewOpen) {
                    Dialog(
                        onDismissRequest = { isProfilePreviewOpen = false },
                        properties = DialogProperties(
                            usePlatformDefaultWidth = false,
                            dismissOnClickOutside = true
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.7f))
                                .clickable { isProfilePreviewOpen = false },
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = state.profilePictureUrl,
                                contentDescription = stringResource(R.string.profile_image_description),
                                modifier = Modifier
                                    .size(340.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, MaterialTheme.Colors.textOrange, CircleShape)
                            )
                        }
                    }
                }

                Text(
                    text = "🔥 x${state.streak}",
                    fontFamily = MaterialTheme.Typography.gantari.fontFamily,
                    fontSize = 12.sp,
                    color = MaterialTheme.Colors.textOrange,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 2.dp, y = 4.dp)
                        .background(
                            color = MaterialTheme.Colors.backgroundMaroonDark,
                            shape = RoundedCornerShape(22.dp)
                        )
                        .border(
                            width = 0.3.dp,
                            color = MaterialTheme.Colors.textOrange,
                            shape = CircleShape
                        )
                        .clip(CircleShape)
                        .padding(horizontal = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            ProfileStatsBox(
                label = stringResource(R.string.friends_label),
                value = state.friendsCount.toString()
            ) {
                onEvent(ProfileContract.Event.NavigationEvent.OnFriendsClick)
            }

            ProfileStatsBox(
                label = stringResource(R.string.titles_badges_label),
                value = state.titlesCount.toString()
            ) {
                onEvent(ProfileContract.Event.NavigationEvent.OnTitlesClick)
            }
        }
    }
}

@Composable
fun ProfileStatsBox(label: String, value: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            fontFamily = MaterialTheme.Typography.gantari.fontFamily,
            fontSize = 16.sp,
            color = MaterialTheme.Colors.textWhite
        )
        Text(
            text = value,
            fontFamily = MaterialTheme.Typography.gantari.fontFamily,
            fontSize = 16.sp,
            color = MaterialTheme.Colors.textOrange
        )
    }
}

@Composable
fun RemoveFriendConfirmationDialog(onEvent: (ProfileContract.Event) -> Unit) {
    WrapAlertDialog(
        onDismissRequest = {
            onEvent(ProfileContract.Event.SendFriendRequestEvent.CancelRemoveFriend)
        },
        containerColor = MaterialTheme.Colors.backgroundMaroonLight,
        title = {
            Text(
                text = stringResource(R.string.remove_friend),
                fontFamily = MaterialTheme.Typography.gantari.fontFamily,
                color = MaterialTheme.Colors.textWhite
            )
        },
        text = {
            Text(
                text = stringResource(R.string.remove_friend_confirmation),
                fontFamily = MaterialTheme.Typography.gantari.fontFamily,
                color = MaterialTheme.Colors.textWhite
            )
        },
        confirmButton = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        onEvent(ProfileContract.Event.SendFriendRequestEvent.CancelRemoveFriend)
                    },
                    modifier = Modifier.weight(1f),
                    border = BorderStroke(1.dp, MaterialTheme.Colors.buttonOrange),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.Colors.buttonOrange
                    )
                ) {
                    Text(
                        text = stringResource(R.string.cancel),
                        fontFamily = MaterialTheme.Typography.pragatiNarrow.fontFamily,
                        fontSize = 18.sp
                    )
                }

                Button(
                    onClick = {
                        onEvent(ProfileContract.Event.SendFriendRequestEvent.ConfirmRemoveFriend)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.Colors.buttonOrange
                    )
                ) {
                    Text(
                        text = stringResource(R.string.confirm),
                        fontFamily = MaterialTheme.Typography.pragatiNarrow.fontFamily,
                        color = MaterialTheme.Colors.textWhite,
                        fontSize = 18.sp
                    )
                }
            }
        },
        dismissButton = {}
    )
}

@Composable
fun DateNavigationRow(state: ProfileContract.State, onEvent: (ProfileContract.Event) -> Unit) {
    val today = LocalDate.now()
    val canGoPrevious = state.minPostDate != null && state.selectedDate > state.minPostDate
    val canGoNext = state.selectedDate < today

    val showArrows = canGoPrevious || canGoNext

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showArrows && canGoPrevious) {
            IconButton(onClick = { onEvent(ProfileContract.Event.OnDatePrevious) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = stringResource(R.string.previous_date),
                    tint = MaterialTheme.Colors.textOrange
                )
            }
        } else {
            Spacer(modifier = Modifier.size(48.dp))
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onEvent(ProfileContract.Event.OnDatePickerOpen) }
        ) {
            Text(
                text = state.selectedDate.toString(),
                color = MaterialTheme.Colors.textWhite,
                fontSize = 16.sp,
                fontFamily = MaterialTheme.Typography.gantari.fontFamily
            )
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = stringResource(R.string.pick_date),
                tint = MaterialTheme.Colors.textOrange,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        if (showArrows && canGoNext) {
            IconButton(onClick = { onEvent(ProfileContract.Event.OnDateNext) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowForward,
                    contentDescription = stringResource(R.string.next_date),
                    tint = MaterialTheme.Colors.textOrange
                )
            }
        } else {
            Spacer(modifier = Modifier.size(48.dp))
        }
    }
}

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun ProfilePostCarousel(
    posts: List<Post>,
    showDelete: Boolean,
    onReact: (postId: String, reaction: String) -> Unit,
    onDeletePost: (postId: String) -> Unit,
) {
    var currentIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(posts.size) {
        currentIndex = currentIndex.coerceIn(0, posts.lastIndex.coerceAtLeast(0))
    }

    val post = posts.getOrNull(currentIndex) ?: return
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    val userReaction = currentUserId?.let { post.userReacted[it] }
    var showDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(540.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(2.dp, MaterialTheme.Colors.textOrange, RoundedCornerShape(16.dp))
    ) {
        var imageLoading by remember { mutableStateOf(true) }
        var isPreviewOpen by remember { mutableStateOf(false) }

        AsyncImage(
            model = post.imageUrl,
            contentDescription = stringResource(R.string.post_image_description),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = {
                            isPreviewOpen = true
                        }
                    )
                },
            onState = {
                imageLoading = it is AsyncImagePainter.State.Loading
            }
        )

        if (isPreviewOpen) {
            Dialog(
                onDismissRequest = { isPreviewOpen = false },
                properties = DialogProperties(
                    usePlatformDefaultWidth = false,
                    dismissOnClickOutside = true
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.7f))
                        .clickable { isPreviewOpen = false },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .border(
                                    1.dp,
                                    MaterialTheme.Colors.textOrange,
                                    RoundedCornerShape(8.dp)
                                )
                                .background(
                                    MaterialTheme.Colors.backgroundDark.copy(alpha = 0.8f),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = post.taskTitle,
                                fontFamily = MaterialTheme.Typography.gantari.fontFamily,
                                fontSize = 18.sp,
                                color = MaterialTheme.Colors.textOrange
                            )
                        }

                        VSpacer.Tiny()

                        AsyncImage(
                            model = post.imageUrl,
                            contentDescription = stringResource(R.string.post_image_description),
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                        )
                    }
                }
            }
        }

        if (imageLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.Colors.textOrange
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 0.dp, start = 0.dp, end = 72.dp)
        ) {
            Text(
                text = post.taskTitle,
                fontFamily = MaterialTheme.Typography.gantari.fontFamily,
                color = MaterialTheme.Colors.textOrange,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .background(
                        MaterialTheme.Colors.backgroundDark.copy(alpha = 0.8f),
                        RoundedCornerShape(bottomEnd = 8.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            )
        }

        if (showDelete) {
            IconButton(
                onClick = { showDialog = true },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .background(
                        color = MaterialTheme.Colors.backgroundDark.copy(alpha = 0.8f),
                        shape = RoundedCornerShape(
                            topStart = 0.dp,
                            topEnd = 0.dp,
                            bottomEnd = 0.dp,
                            bottomStart = 15.dp
                        )
                    )
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete_post),
                    tint = MaterialTheme.colorScheme.error
                )
            }

            if (showDialog) {
                WrapAlertDialog(
                    onDismissRequest = { showDialog = false },
                    containerColor = MaterialTheme.Colors.backgroundMaroonLight,
                    title = {
                        Text(
                            text = stringResource(R.string.delete_post),
                            fontFamily = MaterialTheme.Typography.gantari.fontFamily,
                            color = MaterialTheme.Colors.textWhite
                        )
                    },
                    text = {
                        Text(
                            text = stringResource(R.string.delete_post_confirmation),
                            fontFamily = MaterialTheme.Typography.gantari.fontFamily,
                            color = MaterialTheme.Colors.textWhite
                        )
                    },
                    confirmButton = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showDialog = false },
                                modifier = Modifier.weight(1f),
                                border = BorderStroke(1.dp, MaterialTheme.Colors.buttonOrange),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.Colors.buttonOrange
                                )
                            ) {
                                Text(
                                    text = stringResource(R.string.cancel),
                                    fontFamily = MaterialTheme.Typography.pragatiNarrow.fontFamily,
                                    fontSize = 18.sp
                                )
                            }

                            Button(
                                onClick = {
                                    onDeletePost(post.postId)
                                    showDialog = false
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.Colors.buttonOrange
                                )
                            ) {
                                Text(
                                    text = stringResource(R.string.confirm),
                                    fontFamily = MaterialTheme.Typography.pragatiNarrow.fontFamily,
                                    color = MaterialTheme.Colors.textWhite,
                                    fontSize = 18.sp
                                )
                            }
                        }
                    },
                    dismissButton = {}
                )
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
                .background(MaterialTheme.Colors.backgroundDark.copy(alpha = 0.9f))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            listOf("like", "heart", "wow", "fire", "party").forEach { emoji ->
                val scale = remember { Animatable(1f) }
                Text(
                    text = "${emojiSymbol(emoji)} ${post.reacts[emoji] ?: 0}",
                    fontSize = 12.sp,
                    fontWeight = if (emoji == userReaction) FontWeight.Bold else FontWeight.Normal,
                    color = if (emoji == userReaction) MaterialTheme.Colors.textOrange else MaterialTheme.Colors.textWhite,
                    modifier = Modifier
                        .clickable {
                            coroutineScope.launch {
                                scale.animateTo(1.5f, tween(250))
                                delay(100)
                                scale.animateTo(1f, tween(250))
                                onReact(post.postId, emoji)
                            }
                        }
                        .scale(scale.value),
                    fontFamily = MaterialTheme.Typography.gantari.fontFamily
                )
            }
        }

        if (posts.size > 1 && currentIndex > 0) {
            IconButton(
                onClick = { currentIndex-- },
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(8.dp)
                    .background(MaterialTheme.Colors.backgroundDark, shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = stringResource(R.string.previous),
                    tint = MaterialTheme.Colors.textOrange
                )
            }
        }

        if (posts.size > 1 && currentIndex < posts.lastIndex) {
            IconButton(
                onClick = { currentIndex++ },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(8.dp)
                    .background(MaterialTheme.Colors.backgroundDark, shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowForward,
                    contentDescription = stringResource(R.string.next),
                    tint = MaterialTheme.Colors.textOrange
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerHandler(
    state: ProfileContract.State,
    onEvent: (ProfileContract.Event) -> Unit,
) {
    if (!state.isDatePickerVisible) return

    val today = LocalDate.now()
    val minDate = state.minPostDate ?: today
    val validInitialDate = state.selectedDate.coerceIn(minDate, today)
    val initialMillis = validInitialDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialMillis,
        yearRange = minDate.year..today.year,
        initialDisplayMode = DisplayMode.Picker,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val date = Instant.ofEpochMilli(utcTimeMillis)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                return date in minDate..today
            }
        }
    )

    DatePickerDialog(
        onDismissRequest = { onEvent(ProfileContract.Event.OnDatePickerDismiss) },
        colors = DatePickerDefaults.colors(
            containerColor = MaterialTheme.Colors.backgroundMaroonLight,
        ),
        confirmButton = {},
        dismissButton = {}
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.Colors.backgroundMaroonLight)

        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = MaterialTheme.Colors.backgroundMaroonLight,
                    todayDateBorderColor = MaterialTheme.Colors.textOrange,
                    todayContentColor = MaterialTheme.Colors.textOrange,
                    selectedDayContainerColor = MaterialTheme.Colors.textOrange,
                    selectedDayContentColor = MaterialTheme.Colors.textBlack,
                    disabledDayContentColor = MaterialTheme.Colors.textWhite.copy(alpha = 0.4f),
                    dayContentColor = MaterialTheme.Colors.textWhite,
                    subheadContentColor = MaterialTheme.Colors.textWhite,
                    navigationContentColor = MaterialTheme.Colors.textWhite,
                    titleContentColor = MaterialTheme.Colors.textWhite,
                    headlineContentColor = MaterialTheme.Colors.textOrange,
                    weekdayContentColor = MaterialTheme.Colors.textWhite
                ),
                title = {
                    Text(
                        text = stringResource(R.string.select_date),
                        fontSize = 28.sp,
                        fontFamily = MaterialTheme.Typography.gantari.fontFamily,
                        color = MaterialTheme.Colors.textWhite,
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp)
                    )
                },
                headline = {
                    val selectedDate = datePickerState.selectedDateMillis?.let {
                        Instant.ofEpochMilli(it)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                    }
                    Text(
                        text = selectedDate?.toString() ?: "",
                        fontFamily = MaterialTheme.Typography.gantari.fontFamily,
                        color = MaterialTheme.Colors.textOrange,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                    )
                },
                showModeToggle = false
            )

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.Colors.textWhite.copy(alpha = 0.2f)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = {
                    onEvent(ProfileContract.Event.OnDatePickerDismiss)
                }) {
                    Text(
                        text = stringResource(R.string.cancel_date),
                        fontSize = 16.sp,
                        color = MaterialTheme.Colors.textWhite,
                        fontFamily = MaterialTheme.Typography.pragatiNarrow.fontFamily
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val selectedDate = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        onEvent(ProfileContract.Event.OnDatePicked(selectedDate))
                    }
                    onEvent(ProfileContract.Event.OnDatePickerDismiss)
                }) {
                    Text(
                        text = stringResource(R.string.confirm_date),
                        fontSize = 16.sp,
                        color = MaterialTheme.Colors.textOrange,
                        fontFamily = MaterialTheme.Typography.pragatiNarrow.fontFamily
                    )
                }
            }
        }
    }
}
