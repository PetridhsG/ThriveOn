package gr.aueb.thriveon.ui.screens.notifications

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.Timestamp
import gr.aueb.thriveon.R
import gr.aueb.thriveon.domain.model.NotificationItem
import gr.aueb.thriveon.ui.common.components.BottomNavBar
import gr.aueb.thriveon.ui.common.components.BottomNavDestination
import gr.aueb.thriveon.ui.common.components.CustomSnackbarHost
import gr.aueb.thriveon.ui.common.components.ThriveOnCircularProgressIndicator
import gr.aueb.thriveon.ui.common.components.wrap.WrapScaffold
import gr.aueb.thriveon.ui.screens.notifications.model.NotificationsContract
import gr.aueb.thriveon.ui.theme.Colors
import gr.aueb.thriveon.ui.theme.Typography
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Composable
fun NotificationsScreen(
    state: NotificationsContract.State,
    onEvent: (NotificationsContract.Event) -> Unit,
    effect: Flow<NotificationsContract.Effect>,
    onNavigateToHome: () -> Unit,
    onNavigateToFeed: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToUserProfile: (String) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        onEvent(NotificationsContract.Event.Init)
        effect.collect { effect ->
            when (effect) {
                NotificationsContract.Effect.NavigationEffect.NavigateToHome -> onNavigateToHome()
                NotificationsContract.Effect.NavigationEffect.NavigateToFeed -> onNavigateToFeed()
                NotificationsContract.Effect.NavigationEffect.NavigateToSearch -> onNavigateToSearch()
                NotificationsContract.Effect.NavigationEffect.NavigateToProfile -> onNavigateToProfile()
                is NotificationsContract.Effect.NavigationEffect.NavigateToUserProfile -> onNavigateToUserProfile(
                    effect.userId
                )

                NotificationsContract.Effect.SnackBarEffect.DismissSnackbar -> {
                    snackbarHostState.currentSnackbarData?.dismiss()
                }

                is NotificationsContract.Effect.SnackBarEffect.ShowSnackbar -> {
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
        bottomBar = {
            BottomNavBar(
                selected = BottomNavDestination.Notifications,
                onNavigate = {
                    when (it) {
                        BottomNavDestination.Home -> onEvent(NotificationsContract.Event.NavigationEvent.OnHomeClick)
                        BottomNavDestination.Feed -> onEvent(NotificationsContract.Event.NavigationEvent.OnFeedClick)
                        BottomNavDestination.Search -> onEvent(NotificationsContract.Event.NavigationEvent.OnSearchClick)
                        BottomNavDestination.Profile -> onEvent(NotificationsContract.Event.NavigationEvent.OnProfileClick)
                        else -> Unit
                    }
                }
            )
        },
        snackbarHost = { CustomSnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.notifications_screen_title),
                    color = MaterialTheme.Colors.textOrange,
                    fontSize = 32.sp,
                    fontFamily = MaterialTheme.Typography.gantari.fontFamily,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 8.dp),
                    textAlign = TextAlign.Center
                )
                HorizontalDivider(color = MaterialTheme.Colors.dividerGray)
            }

            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    ThriveOnCircularProgressIndicator()
                }
            } else if (state.notifications.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.notifications_screen_empty),
                        fontSize = 18.sp,
                        fontFamily = MaterialTheme.Typography.gantari.fontFamily,
                        color = MaterialTheme.Colors.textWhite
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(state.notifications) {  notification ->
                        NotificationItemView(
                            notification = notification,
                            onAccept = {
                                onEvent(
                                    NotificationsContract.Event.AcceptFriendRequest(notification)
                                )
                            },
                            onDelete = {
                                if (notification.type == "friend_request") {
                                    onEvent(
                                        NotificationsContract.Event.DeleteFriendRequest(notification)
                                    )
                                } else {
                                    onEvent(
                                        NotificationsContract.Event.DeleteReaction(notification)
                                    )
                                }
                            },
                            onProfileClick = {
                                onEvent(
                                    NotificationsContract.Event.NavigationEvent.OnUserProfileClick(
                                        notification.from
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationItemView(
    notification: NotificationItem,
    onAccept: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    onProfileClick: () -> Unit,
) {
    val backgroundColor = when (notification.type) {
        "friend_request" -> MaterialTheme.Colors.backgroundMaroonLight
        "reaction" -> MaterialTheme.Colors.backgroundMaroonLight
        else -> MaterialTheme.Colors.dividerGray
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                AsyncImage(
                    model = notification.fromProfilePictureUrl,
                    contentDescription = stringResource(R.string.notifications_profile_picture),
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .clickable { onProfileClick() }
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = notification.fromUsername,
                        fontSize = 16.sp,
                        fontFamily = MaterialTheme.Typography.gantari.fontFamily,
                        color = MaterialTheme.Colors.textOrange
                    )

                    Text(
                        text = when (notification.type) {
                            "friend_request" -> stringResource(R.string.notifications_friend_request_message)
                            "reaction" -> stringResource(R.string.notifications_reaction_message)
                            "informative" -> notification.message
                                ?: stringResource(R.string.notifications_informative_fallback)

                            else -> stringResource(R.string.notifications_unknown_type)
                        },
                        fontSize = 14.sp,
                        color = MaterialTheme.Colors.textWhite
                    )

                    Text(
                        text = timeAgo(notification.timestamp),
                        fontSize = 12.sp,
                        color = MaterialTheme.Colors.navigationIconGray
                    )
                }

                if (notification.type == "friend_request") {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { onAccept?.invoke() },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.Colors.completeGreen
                            )
                        }

                        IconButton(
                            onClick = { onDelete?.invoke() },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
                if (notification.type == "reaction") {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { onDelete?.invoke() },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun timeAgo(timestamp: Timestamp?): String {
    if (timestamp == null) {
        return stringResource(R.string.time_just_now)
    }
    val now = System.currentTimeMillis()
    val timestampMillis = timestamp.seconds * 1000
    val diff = now - timestampMillis
    val minutes = diff / 60000

    return when {
        minutes < 1 -> stringResource(R.string.time_just_now)
        minutes < 60 -> stringResource(R.string.time_minutes_ago, minutes)
        minutes < 1440 -> stringResource(R.string.time_hours_ago, minutes / 60)
        else -> stringResource(R.string.time_days_ago, minutes / 1440)
    }
}
