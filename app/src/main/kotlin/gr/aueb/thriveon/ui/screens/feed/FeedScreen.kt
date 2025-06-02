package gr.aueb.thriveon.ui.screens.feed

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import gr.aueb.thriveon.R
import gr.aueb.thriveon.domain.model.Post
import gr.aueb.thriveon.ui.common.components.BottomNavBar
import gr.aueb.thriveon.ui.common.components.BottomNavDestination
import gr.aueb.thriveon.ui.common.components.ThriveOnCircularProgressIndicator
import gr.aueb.thriveon.ui.common.components.spacers.VSpacer
import gr.aueb.thriveon.ui.common.components.wrap.WrapScaffold
import gr.aueb.thriveon.ui.screens.feed.model.FeedContract
import gr.aueb.thriveon.ui.theme.Colors
import gr.aueb.thriveon.ui.theme.Typography
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Composable
fun FeedScreen(
    state: FeedContract.State,
    onEvent: (FeedContract.Event) -> Unit,
    effect: Flow<FeedContract.Effect>,
    onNavigateToHome: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToUserProfile: (String) -> Unit,
) {
    LaunchedEffect(Unit) {
        onEvent(FeedContract.Event.Init)
        effect.collect { effect ->
            when (effect) {
                FeedContract.Effect.NavigationEffect.NavigateToHome -> onNavigateToHome()
                FeedContract.Effect.NavigationEffect.NavigateToSearch -> onNavigateToSearch()
                FeedContract.Effect.NavigationEffect.NavigateToNotifications -> onNavigateToNotifications()
                FeedContract.Effect.NavigationEffect.NavigateToProfile -> onNavigateToProfile()
                is FeedContract.Effect.NavigationEffect.NavigateToUserProfile -> onNavigateToUserProfile(
                    effect.userId
                )
            }
        }
    }

    WrapScaffold(
        containerColor = MaterialTheme.Colors.backgroundMaroonDark,
        bottomBar = {
            BottomNavBar(
                selected = BottomNavDestination.Feed,
                onNavigate = {
                    when (it) {
                        BottomNavDestination.Home -> onEvent(FeedContract.Event.NavigationEvent.OnHomeClick)
                        BottomNavDestination.Search -> onEvent(FeedContract.Event.NavigationEvent.OnSearchClick)
                        BottomNavDestination.Notifications -> onEvent(FeedContract.Event.NavigationEvent.OnNotificationsClick)
                        BottomNavDestination.Profile -> onEvent(FeedContract.Event.NavigationEvent.OnProfileClick)
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
        ){
            Column(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.thrive_on_logo),
                        contentDescription = null,
                        modifier = Modifier.width(180.dp),
                        contentScale = ContentScale.Fit
                    )
                }
                HorizontalDivider(color = MaterialTheme.Colors.dividerGray)
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp,top=8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when {
                    state.isLoading -> {
                        item {
                            Box(
                                modifier = Modifier.fillParentMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                ThriveOnCircularProgressIndicator()
                            }
                        }
                    }

                    state.posts.isEmpty() -> {
                        item {
                            Box(
                                modifier = Modifier.fillParentMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(R.string.feed_screen_no_posts),
                                    fontSize = 18.sp,
                                    fontFamily = MaterialTheme.Typography.gantari.fontFamily,
                                    color = MaterialTheme.Colors.textWhite
                                )
                            }
                        }
                    }

                    else -> {
                        itemsIndexed(state.posts, key = { _, post -> post.postId }) { index, post ->
                            if (index == 0) {
                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            FeedPostItem(
                                post = post,
                                onProfileClick = {
                                    onEvent(FeedContract.Event.OnUserProfileClick(post.userId))
                                },
                                onUserNameClick = {
                                    onEvent(FeedContract.Event.OnUserNameClick(post.userId))
                                },
                                onReact = { reaction ->
                                    onEvent(FeedContract.Event.OnReact(post.postId, reaction))
                                },
                                onEvent = onEvent,
                                state = state
                            )
                        }
                    }
                }

                item {
                    VSpacer.Tiny()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedPostItem(
    state: FeedContract.State,
    onEvent: (FeedContract.Event) -> Unit,
    post: Post,
    onUserNameClick: () -> Unit,
    onProfileClick: () -> Unit,
    onReact: (String) -> Unit
) {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    val userReaction = currentUserId?.let { post.userReacted[it] }
    val reactionsGrouped = remember(post.userReacted) {
        post.userReacted.entries.groupBy({ it.value }, { it.key })
    }
    val allReactedUserIds = post.userReacted.keys.toList()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSheet by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf("All") }

    val reactionTypes = listOf("All", "like", "heart", "wow", "fire", "party")
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.Colors.backgroundMaroonLight)
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            AsyncImage(
                model = post.userProfilePictureUrl,
                contentDescription = stringResource(R.string.feed_screen_profile_picture),
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .clickable { onProfileClick() }
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = post.username,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    lineHeight = 14.sp,
                    fontFamily = MaterialTheme.Typography.gantari.fontFamily,
                    color = MaterialTheme.Colors.textWhite,
                    modifier = Modifier
                        .clickable { onUserNameClick() }
                )
                Text(
                    text = post.taskTitle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 12.sp,
                    lineHeight = 14.sp,
                    fontFamily = MaterialTheme.Typography.gantari.fontFamily,
                    color = MaterialTheme.Colors.textOrange
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = post.taskCategory,
                        fontFamily = MaterialTheme.Typography.gantari.fontFamily,
                        fontSize = 12.sp,
                        lineHeight = 14.sp,
                        color = MaterialTheme.Colors.textOrange
                    )
                    Text(
                        text = timeAgo(post.timestamp),
                        fontFamily = MaterialTheme.Typography.gantari.fontFamily,
                        fontSize = 12.sp,
                        lineHeight = 14.sp,
                        color = MaterialTheme.Colors.textOrange
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .clip(RoundedCornerShape(12.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = { showSheet = true }
                        )
                    }
            ) {
                val painter = rememberAsyncImagePainter(model = post.imageUrl)
                val imageState = painter.state

                Image(
                    painter = painter,
                    contentDescription = stringResource(R.string.feed_screen_post_image),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )

                if (imageState is AsyncImagePainter.State.Loading) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(MaterialTheme.Colors.backgroundDark.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.Colors.textOrange)
                    }
                }

                if (imageState !is AsyncImagePainter.State.Loading) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
                            .background(MaterialTheme.Colors.backgroundDark.copy(alpha = 0.9f))
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val coroutineScope = rememberCoroutineScope()
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
                                            scale.animateTo(1f, tween(250))
                                            onReact(emoji)
                                        }
                                    }
                                    .scale(scale.value),
                                fontFamily = MaterialTheme.Typography.gantari.fontFamily
                            )
                        }
                    }
                }
            }

            if (showSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showSheet = false },
                    sheetState = sheetState,
                    containerColor = MaterialTheme.Colors.backgroundMaroonLight,
                    dragHandle = null
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 300.dp, max = 400.dp)
                    ) {
                        TabRow(
                            selectedTabIndex = reactionTypes.indexOf(selectedTab),
                            containerColor = MaterialTheme.Colors.backgroundDark,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                            indicator = { tabPositions ->
                                TabRowDefaults.SecondaryIndicator(
                                    modifier = Modifier
                                        .tabIndicatorOffset(tabPositions[reactionTypes.indexOf(selectedTab)])
                                        .height(3.dp),
                                    color = MaterialTheme.Colors.buttonOrange
                                )
                            }
                        ) {
                            reactionTypes.forEach { reaction ->
                                val count = if (reaction == "All") post.userReacted.size
                                else post.reacts[reaction] ?: 0

                                Tab(
                                    selected = selectedTab == reaction,
                                    onClick = { selectedTab = reaction },
                                    text = {
                                        Box(
                                            modifier = Modifier
                                                .height(40.dp)
                                                .width(48.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = if (reaction == "All") "All" else emojiSymbol(reaction),
                                                color = if (selectedTab == reaction)
                                                    MaterialTheme.Colors.textOrange
                                                else MaterialTheme.Colors.textWhite,
                                                fontSize = 16.sp,
                                                modifier = Modifier.align(Alignment.Center)
                                            )

                                            Text(
                                                text = count.toString(),
                                                color = MaterialTheme.Colors.textWhite.copy(alpha = 0.7f),
                                                fontSize = 12.sp,
                                                modifier = Modifier
                                                    .align(Alignment.BottomEnd)
                                                    .offset(x = 6.dp)
                                            )
                                        }
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        val userIds = when (selectedTab) {
                            "All" -> allReactedUserIds
                            else -> reactionsGrouped[selectedTab] ?: emptyList()
                        }

                        LaunchedEffect(selectedTab) {
                            onEvent(FeedContract.Event.LoadReactedUsers(userIds))
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp)
                        ) {
                            when {
                                state.isReactedUsersLoading -> {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        ThriveOnCircularProgressIndicator()
                                    }
                                }

                                state.reactedUsers.isEmpty() -> {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = stringResource(R.string.feed_screen_no_reactions),
                                            color = MaterialTheme.Colors.textWhite,
                                            fontFamily = MaterialTheme.Typography.gantari.fontFamily,
                                            fontSize = 16.sp
                                        )
                                    }
                                }

                                else -> {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .verticalScroll(rememberScrollState()),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        val userIdToReaction = reactionsGrouped
                                            .flatMap { (reaction, userIds) -> userIds.map { it to reaction } }
                                            .toMap()

                                        state.reactedUsers.forEachIndexed { index, user ->
                                            val reaction = userIdToReaction[user.userId]

                                            Column {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clickable {
                                                            onEvent(FeedContract.Event.OnUserProfileClick(user.userId))
                                                            showSheet = false
                                                        }
                                                        .padding(6.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        AsyncImage(
                                                            model = user.profilePictureUrl,
                                                            contentDescription = null,
                                                            modifier = Modifier
                                                                .size(36.dp)
                                                                .clip(CircleShape)
                                                        )
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Column {
                                                            Text(
                                                                text = user.username,
                                                                color = MaterialTheme.Colors.textWhite,
                                                                fontFamily = MaterialTheme.Typography.gantari.fontFamily,
                                                                fontSize = 15.sp
                                                            )
                                                            Text(
                                                                text = user.equippedTitle,
                                                                color = MaterialTheme.Colors.textOrange,
                                                                fontFamily = MaterialTheme.Typography.gantari.fontFamily,
                                                                fontSize = 13.sp
                                                            )
                                                        }
                                                    }

                                                    reaction?.let {
                                                        Text(
                                                            text = emojiSymbol(it),
                                                            fontSize = 20.sp
                                                        )
                                                    }
                                                }

                                                if (index != state.reactedUsers.lastIndex) {
                                                    HorizontalDivider(
                                                        color = MaterialTheme.Colors.dividerGray.copy(alpha = 0.5f),
                                                        thickness = 0.5.dp
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun timeAgo(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    val minutes = diff / 60000

    return when {
        minutes < 1 -> stringResource(R.string.time_just_now)
        minutes < 60 -> stringResource(R.string.time_minutes_ago, minutes)
        minutes < 1440 -> stringResource(R.string.time_hours_ago, minutes / 60)
        else -> stringResource(R.string.time_days_ago, minutes / 1440)
    }
}

@Composable
fun emojiSymbol(name: String): String {
    return when (name) {
        "like" -> stringResource(R.string.emoji_like)
        "heart" -> stringResource(R.string.emoji_heart)
        "wow" -> stringResource(R.string.emoji_wow)
        "fire" -> stringResource(R.string.emoji_fire)
        "party" -> stringResource(R.string.emoji_party)
        else -> stringResource(R.string.emoji_unknown)
    }
}
