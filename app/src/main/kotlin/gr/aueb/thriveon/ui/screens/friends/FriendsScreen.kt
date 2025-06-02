package gr.aueb.thriveon.ui.screens.friends

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import gr.aueb.thriveon.R
import gr.aueb.thriveon.ui.common.components.ThriveOnCircularProgressIndicator
import gr.aueb.thriveon.ui.common.components.spacers.VSpacer
import gr.aueb.thriveon.ui.common.components.wrap.WrapScaffold
import gr.aueb.thriveon.ui.common.components.wrap.WrapTopAppBar
import gr.aueb.thriveon.ui.screens.friends.model.FriendsContract
import gr.aueb.thriveon.ui.theme.Colors
import gr.aueb.thriveon.ui.theme.Typography
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsScreen(
    userId: String,
    state: FriendsContract.State,
    onEvent: (FriendsContract.Event) -> Unit,
    effect: Flow<FriendsContract.Effect>,
    onNavigateBack: () -> Unit,
    onNavigateToUserProfile: (String) -> Unit,
) {
    LaunchedEffect(Unit) {
        onEvent(FriendsContract.Event.Init(userId))
        effect.collect { effect ->
            when (effect) {
                FriendsContract.Effect.NavigateBack -> onNavigateBack()
                is FriendsContract.Effect.NavigateToUserProfile -> onNavigateToUserProfile(effect.userId)
            }
        }
    }

    WrapScaffold(
        containerColor = MaterialTheme.Colors.backgroundMaroonDark,
        topBar = {
            WrapTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.friends_screen_title),
                        fontSize = 30.sp,
                        fontFamily = MaterialTheme.Typography.gantari.fontFamily,
                        color = MaterialTheme.Colors.textOrange
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        onEvent(FriendsContract.Event.OnBackClick)
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(R.string.friends_screen_back),
                            tint = MaterialTheme.Colors.textWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.Colors.backgroundMaroonDark
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
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

                state.friends.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.friends_screen_empty),
                            fontFamily = MaterialTheme.Typography.gantari.fontFamily,
                            fontSize = 16.sp,
                            color = MaterialTheme.Colors.textWhite
                        )
                    }
                }

                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        itemsIndexed(state.friends, key = { _, user -> user.userId }) { index, user ->
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onEvent(FriendsContract.Event.OnUserProfileClick(user.userId))
                                        }
                                        .padding(vertical = 8.dp)
                                ) {
                                    AsyncImage(
                                        model = user.profilePictureUrl,
                                        contentDescription = stringResource(R.string.friends_screen_profile_image_description),
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = user.username,
                                            fontFamily = MaterialTheme.Typography.gantari.fontFamily,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontSize = 18.sp,
                                            color = MaterialTheme.Colors.textOrange
                                        )
                                        if (user.equippedTitle.isNotBlank()) {
                                            Text(
                                                text = user.equippedTitle,
                                                fontFamily = MaterialTheme.Typography.gantari.fontFamily,
                                                style = MaterialTheme.typography.bodySmall,
                                                fontSize = 16.sp,
                                                color = MaterialTheme.Colors.textWhite
                                            )
                                        }
                                    }
                                }

                                if (index < state.friends.lastIndex) {
                                    HorizontalDivider(
                                        color = MaterialTheme.Colors.dividerGray.copy(alpha = 0.5f),
                                        thickness = 0.5.dp
                                    )
                                    VSpacer.Tiny()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
