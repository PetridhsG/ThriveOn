package gr.aueb.thriveon.ui.screens.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import gr.aueb.thriveon.R
import gr.aueb.thriveon.ui.common.components.BottomNavBar
import gr.aueb.thriveon.ui.common.components.BottomNavDestination
import gr.aueb.thriveon.ui.common.components.ThriveOnCircularProgressIndicator
import gr.aueb.thriveon.ui.common.components.wrap.WrapOutlinedTextField
import gr.aueb.thriveon.ui.common.components.wrap.WrapScaffold
import gr.aueb.thriveon.ui.screens.search.model.SearchContract
import gr.aueb.thriveon.ui.theme.Colors
import gr.aueb.thriveon.ui.theme.Typography
import kotlinx.coroutines.flow.Flow

@Composable
fun SearchScreen(
    state: SearchContract.State,
    onEvent: (SearchContract.Event) -> Unit,
    effect: Flow<SearchContract.Effect>,
    onNavigateToHome: () -> Unit,
    onNavigateToFeed: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToUserProfile: (String) -> Unit,
) {
    LaunchedEffect(Unit) {
        onEvent(SearchContract.Event.Init)
        effect.collect { effect ->
            when (effect) {
                SearchContract.Effect.NavigationEffect.NavigateToHome -> onNavigateToHome()
                SearchContract.Effect.NavigationEffect.NavigateToFeed -> onNavigateToFeed()
                SearchContract.Effect.NavigationEffect.NavigateToNotifications -> onNavigateToNotifications()
                SearchContract.Effect.NavigationEffect.NavigateToProfile -> onNavigateToProfile()
                is SearchContract.Effect.NavigationEffect.NavigateToUserProfile -> onNavigateToUserProfile(
                    effect.userId
                )
            }
        }
    }

    WrapScaffold(
        containerColor = MaterialTheme.Colors.backgroundMaroonDark,
        bottomBar = {
            BottomNavBar(
                selected = BottomNavDestination.Search,
                onNavigate = {
                    when (it) {
                        BottomNavDestination.Home -> onEvent(SearchContract.Event.NavigationEvent.OnHomeClick)
                        BottomNavDestination.Feed -> onEvent(SearchContract.Event.NavigationEvent.OnFeedClick)
                        BottomNavDestination.Notifications -> onEvent(SearchContract.Event.NavigationEvent.OnNotificationsClick)
                        BottomNavDestination.Profile -> onEvent(SearchContract.Event.NavigationEvent.OnProfileClick)
                        else -> {}
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.search_screen_title),
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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                SearchInputField(
                    value = state.searchQuery,
                    onValueChange = { onEvent(SearchContract.Event.OnSearchInput(it)) },
                    clearInputField = { onEvent(SearchContract.Event.ClearInputField) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (state.searchQuery.isBlank())
                        stringResource(R.string.search_screen_recommended)
                    else
                        stringResource(R.string.search_screen_results),
                    fontSize = 18.sp,
                    fontFamily = MaterialTheme.Typography.gantari.fontFamily,
                    color = MaterialTheme.Colors.textWhite
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (!state.isLoading) {
                    if (state.results.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 64.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.search_screen_no_results_found),
                                fontSize = 18.sp,
                                fontFamily = MaterialTheme.Typography.gantari.fontFamily,
                                color = MaterialTheme.Colors.textWhite
                            )
                        }
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            itemsIndexed(state.results) { index, user ->
                                SearchUserItem(
                                    userId = user.userId,
                                    username = user.username,
                                    profilePictureUrl = user.profilePictureUrl,
                                    equippedTitle = user.equippedTitle,
                                    onClick = { onEvent(SearchContract.Event.OnUserClick(it)) }
                                )
                                if (index < state.results.lastIndex) {
                                    HorizontalDivider(
                                        color = MaterialTheme.Colors.dividerGray.copy(alpha = 0.5f),
                                        thickness = 0.5.dp
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        ThriveOnCircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@Composable
fun SearchInputField(
    value: String,
    onValueChange: (String) -> Unit,
    clearInputField: () -> Unit,
    modifier: Modifier = Modifier
) {
    WrapOutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        textStyle = MaterialTheme.typography.titleLarge.copy(
            fontSize = 18.sp,
            fontFamily = MaterialTheme.Typography.gantari.fontFamily
        ),
        placeholder = {
            Text(
                text = stringResource(R.string.search_screen_label),
                fontSize = 18.sp,
                fontFamily = MaterialTheme.Typography.gantari.fontFamily,
            )
        },
        label = {
            Text(
                text = stringResource(R.string.search_screen_label),
                fontSize = 14.sp,
                fontFamily = MaterialTheme.Typography.gantari.fontFamily,
            )
        },
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.search),
                contentDescription = stringResource(R.string.search_screen_label),
                tint = MaterialTheme.Colors.textOrange
            )
        },
        trailingIcon = {
            IconButton(
                onClick = {
                    clearInputField()
                },
                enabled = value.isNotBlank()
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_cancel_24),
                    contentDescription = stringResource(R.string.search_screen_label),
                    tint = if (value.isNotBlank()) MaterialTheme.Colors.textOrange else MaterialTheme.Colors.textOrange.copy(
                        alpha = 0.5f
                    )
                )
            }
        }
    )
}

@Composable
fun SearchUserItem(
    userId: String,
    username: String,
    profilePictureUrl: String,
    equippedTitle: String,
    onClick: (String) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(userId) }
            .padding(vertical = 8.dp)
    ) {
        AsyncImage(
            model = profilePictureUrl,
            contentDescription = stringResource(R.string.search_screen_profile_picture),
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = username,
                fontSize = 16.sp,
                fontFamily = MaterialTheme.Typography.gantari.fontFamily,
                color = MaterialTheme.Colors.textOrange
            )
            Text(
                text = if (equippedTitle.isNotBlank()) equippedTitle else "",
                fontSize = 14.sp,
                fontFamily = MaterialTheme.Typography.gantari.fontFamily,
                color = MaterialTheme.Colors.textWhite
            )
        }
    }
}
