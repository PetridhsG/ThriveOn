package gr.aueb.thriveon.ui.screens.home

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import gr.aueb.thriveon.R
import gr.aueb.thriveon.domain.model.FirebaseTask
import gr.aueb.thriveon.domain.model.PrivateTask
import gr.aueb.thriveon.ui.common.components.BottomNavBar
import gr.aueb.thriveon.ui.common.components.BottomNavDestination
import gr.aueb.thriveon.ui.common.components.CustomSnackbarHost
import gr.aueb.thriveon.ui.common.components.ThriveOnCircularProgressIndicator
import gr.aueb.thriveon.ui.common.components.spacers.VSpacer
import gr.aueb.thriveon.ui.common.components.wrap.WrapButton
import gr.aueb.thriveon.ui.common.components.wrap.WrapIconButton
import gr.aueb.thriveon.ui.common.components.wrap.WrapOutlinedTextField
import gr.aueb.thriveon.ui.common.components.wrap.WrapScaffold
import gr.aueb.thriveon.ui.common.components.wrap.WrapTextButton
import gr.aueb.thriveon.ui.common.components.wrap.rememberModalBottomSheetState
import gr.aueb.thriveon.ui.screens.home.model.HomeContract
import gr.aueb.thriveon.ui.theme.Colors
import gr.aueb.thriveon.ui.theme.Typography
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.io.File
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: HomeContract.State,
    onEvent: (HomeContract.Event) -> Unit,
    effect: Flow<HomeContract.Effect>,
    onNavigateToFeed: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToProfile: () -> Unit,
    shouldNavigateBack: Boolean = false,
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val addPrivateTaskBottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { sheetValue -> sheetValue != SheetValue.Hidden }
    )
    val dailyTaskBottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { sheetValue -> sheetValue != SheetValue.Hidden }
    )
    val completeTaskSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { sheetValue -> sheetValue != SheetValue.Hidden }
    )
    val snackbarHostState = remember { SnackbarHostState() }
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && photoUri != null) {
            onEvent(HomeContract.Event.CompleteTaskEvent.PhotoCaptured(photoUri!!))
        }
    }

    if (shouldNavigateBack) BackHandler(enabled = true) {}

    LaunchedEffect(Unit) {
        onEvent(HomeContract.Event.Init)
        effect.collect { effect ->
            when (effect) {
                HomeContract.Effect.Navigation.NavigateToFeed -> {
                    onNavigateToFeed()
                }

                HomeContract.Effect.Navigation.NavigateToSearch -> {
                    onNavigateToSearch()
                }

                HomeContract.Effect.Navigation.NavigateToNotifications -> {
                    onNavigateToNotifications()
                }

                HomeContract.Effect.Navigation.NavigateToProfile -> {
                    onNavigateToProfile()
                }

                HomeContract.Effect.SnackBarEffect.DismissSnackbar -> {
                    snackbarHostState.currentSnackbarData?.dismiss()
                }

                is HomeContract.Effect.SnackBarEffect.ShowSnackbar -> {
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

                HomeContract.Effect.AddPrivateTaskBottomSheetEffect.Dismiss -> {
                    addPrivateTaskBottomSheetState.hide()
                }

                HomeContract.Effect.DailyTaskBottomSheetEffect.Dismiss -> {
                    dailyTaskBottomSheetState.hide()
                }

                HomeContract.Effect.CompleteTaskBottomSheetEffect.Dismiss -> {
                    completeTaskSheetState.hide()
                }

                is HomeContract.Effect.LaunchCamera -> {
                    val photoFile =
                        File(context.cacheDir, "images/${UUID.randomUUID()}.jpg").apply {
                            parentFile?.mkdirs()
                            createNewFile()
                        }
                    val uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        photoFile
                    )
                    photoUri = uri

                    cameraLauncher.launch(uri)
                }
            }
        }
    }

    WrapScaffold(
        containerColor = MaterialTheme.Colors.backgroundMaroonDark,
        snackbarHost = { CustomSnackbarHost(snackbarHostState) },
        bottomBar = {
            BottomNavBar(
                selected = BottomNavDestination.Home,
                onNavigate = {
                    when (it) {
                        BottomNavDestination.Feed -> onEvent(HomeContract.Event.NavigationEvent.OnFeedClick)
                        BottomNavDestination.Search -> onEvent(HomeContract.Event.NavigationEvent.OnSearchClick)
                        BottomNavDestination.Notifications -> onEvent(HomeContract.Event.NavigationEvent.OnNotificationsClick)
                        BottomNavDestination.Profile -> onEvent(HomeContract.Event.NavigationEvent.OnProfileClick)
                        else -> {}
                    }
                }
            )
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.thrive_on_logo),
                        contentDescription = null,
                        modifier = Modifier.width(180.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        SectionDivider(title = stringResource(R.string.home_screen_daily_tasks))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            ResetCountdownText()
                        }
                    }

                    items(3) { index ->
                        VSpacer.Small()
                        val taskEntry = state.dailyTasks[index]
                        val taskDetails = taskEntry?.let { state.loadedTaskMap[it.taskId] }

                        when {
                            taskEntry == null -> {
                                WrapButton(
                                    onClick = {
                                        onEvent(HomeContract.Event.OnDailyTaskClick(index))
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = stringResource(R.string.home_screen_add_task),
                                        color = MaterialTheme.Colors.textBlack,
                                        fontFamily = MaterialTheme.Typography.pragatiNarrow.fontFamily,
                                        fontSize = 20.sp
                                    )
                                }
                            }

                            taskDetails != null -> {
                                DailyTaskCard(
                                    task = taskDetails,
                                    isCompleted = taskEntry.isCompleted,
                                    onCompleteClick = {
                                        onEvent(
                                            HomeContract.Event.CompleteTaskEvent.Start(
                                                task = taskDetails,
                                                slotIndex = index
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    }

                    item {
                        VSpacer.Small()
                        SectionDivider(title = stringResource(R.string.home_screen_private_tasks))
                    }

                    items(state.privateTasks, key = { it.id }) { task ->
                        PrivateTaskCard(
                            task = task,
                            onCompleteClick = {
                                onEvent(HomeContract.Event.PrivateTaskEvent.OnCompleteTaskClick(task.id))
                            }
                        )
                    }

                    item {
                        WrapButton(
                            onClick = {
                                onEvent(HomeContract.Event.PrivateTaskEvent.OnAddPrivateTaskClick)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = stringResource(R.string.home_screen_create_private_task),
                                fontFamily = MaterialTheme.Typography.pragatiNarrow.fontFamily,
                                fontSize = 20.sp
                            )
                        }
                    }

                    item {
                        VSpacer.Tiny()
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

        if (state.isAddPrivateTaskBottomSheetVisible) {
            AddPrivateTaskBottomSheet(
                onEvent = onEvent,
                state = state,
                sheetState = addPrivateTaskBottomSheetState
            )
        }

        if (state.isDailyTaskBottomSheetVisible) {
            DailyTaskBottomSheet(
                onEvent = onEvent,
                state = state,
                sheetState = dailyTaskBottomSheetState
            )
        }

        if (state.isCompleteTaskBottomSheetVisible) {
            CompleteTaskBottomSheet(
                onEvent = onEvent,
                state = state,
                sheetState = completeTaskSheetState
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPrivateTaskBottomSheet(
    onEvent: (HomeContract.Event) -> Unit,
    state: HomeContract.State,
    sheetState: SheetState,
) {
    ModalBottomSheet(
        onDismissRequest = { onEvent(HomeContract.Event.AddPrivateTaskBottomSheetEvent.Dismiss) },
        dragHandle = null,
        sheetState = sheetState,
        containerColor = MaterialTheme.Colors.backgroundMaroonLight
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.home_screen_new_private_task),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 22.sp,
                        fontFamily = MaterialTheme.Typography.pragatiNarrow.fontFamily
                    ),
                    color = MaterialTheme.Colors.textWhite
                )
            }

            WrapOutlinedTextField(
                value = state.newTaskTitle,
                onValueChange = {
                    onEvent(HomeContract.Event.AddPrivateTaskBottomSheetEvent.TitleChange(it))
                },
                textStyle = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 15.sp,
                    fontFamily = MaterialTheme.Typography.gantari.fontFamily
                ),
                label = { Text(stringResource(R.string.home_screen_task_title)) },
                modifier = Modifier.fillMaxWidth()
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DatePickerRow(
                    selectedDate = state.newTaskDueDate,
                    onDateSelected = {
                        onEvent(HomeContract.Event.AddPrivateTaskBottomSheetEvent.DateChange(it))
                    },
                    onDismissRequest = { }
                )
            }

            VSpacer.Tiny()

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                WrapTextButton(
                    onClick = {
                        onEvent(HomeContract.Event.AddPrivateTaskBottomSheetEvent.Dismiss)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stringResource(R.string.home_screen_cancel),
                        color = MaterialTheme.Colors.textWhite,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 18.sp,
                            fontFamily = MaterialTheme.Typography.pragatiNarrow.fontFamily
                        )
                    )
                }

                WrapButton(
                    onClick = {
                        onEvent(HomeContract.Event.AddPrivateTaskBottomSheetEvent.Confirm)
                    },
                    modifier = Modifier.weight(1f),
                    enabled = state.isCreatePrivateTaskButtonEnabled
                ) {
                    Text(
                        text = stringResource(R.string.home_screen_create),
                        color = if (state.isCreatePrivateTaskButtonEnabled)
                            MaterialTheme.Colors.textBlack
                        else
                            MaterialTheme.Colors.backgroundMaroonDark,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 18.sp,
                            fontFamily = MaterialTheme.Typography.pragatiNarrow.fontFamily
                        )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyTaskBottomSheet(
    onEvent: (HomeContract.Event) -> Unit,
    state: HomeContract.State,
    sheetState: SheetState,
) {
    ModalBottomSheet(
        onDismissRequest = { onEvent(HomeContract.Event.DailyTaskBottomSheetEvent.Cancel) },
        dragHandle = null,
        sheetState = sheetState,
        containerColor = MaterialTheme.Colors.backgroundMaroonLight
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.home_screen_choose_task),
                        fontFamily = MaterialTheme.Typography.pragatiNarrow.fontFamily,
                        fontSize = 22.sp,
                        color = MaterialTheme.Colors.textWhite,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = stringResource(R.string.home_screen_reroll_left, state.rerollCount),
                        fontFamily = MaterialTheme.Typography.pragatiNarrow.fontFamily,
                        fontSize = 22.sp,
                        color = MaterialTheme.Colors.textOrange,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                IconButton(
                    onClick = {
                        onEvent(HomeContract.Event.DailyTaskBottomSheetEvent.Reroll)
                    },
                    enabled = !state.areSuggestionsLoading && state.rerollCount > 0
                ) {
                    Icon(
                        painter = painterResource(R.drawable.restart),
                        contentDescription = stringResource(R.string.home_screen_reroll),
                        tint = if (state.rerollCount > 0 && !state.areSuggestionsLoading)
                            MaterialTheme.Colors.textOrange
                        else
                            MaterialTheme.Colors.dividerGray,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            if (state.areSuggestionsLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.Colors.textOrange)
                }
            } else {
                state.suggestedTasks.forEachIndexed { index, task ->
                    OutlinedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onEvent(
                                    HomeContract.Event.DailyTaskBottomSheetEvent.Select(index)
                                )
                            },
                        colors = CardDefaults.outlinedCardColors(
                            containerColor = MaterialTheme.Colors.backgroundMaroonDark
                        ),
                        border = BorderStroke(
                            width = 2.dp,
                            color = if (state.selectedSuggestionIndex == index)
                                MaterialTheme.Colors.textOrange
                            else
                                MaterialTheme.Colors.backgroundMaroonDark
                        )
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                text = task.title,
                                fontSize = 16.sp,
                                fontFamily = MaterialTheme.Typography.gantari.fontFamily,
                                color = MaterialTheme.Colors.textWhite,
                            )
                            Text(
                                text = task.categoryTitle,
                                fontSize = 12.sp,
                                fontFamily = MaterialTheme.Typography.gantari.fontFamily,
                                color = MaterialTheme.Colors.textOrange
                            )
                        }
                    }
                }
            }

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                WrapTextButton(
                    onClick = {
                        onEvent(HomeContract.Event.DailyTaskBottomSheetEvent.Cancel)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stringResource(R.string.home_screen_cancel),
                        fontSize = 18.sp,
                        fontFamily = MaterialTheme.Typography.pragatiNarrow.fontFamily,
                        color = MaterialTheme.Colors.textOrange
                    )
                }
                WrapButton(
                    onClick = {
                        onEvent(HomeContract.Event.DailyTaskBottomSheetEvent.Confirm)
                    },
                    enabled = state.selectedSuggestionIndex != null && !state.areSuggestionsLoading,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stringResource(R.string.home_screen_confirm),
                        fontSize = 18.sp,
                        fontFamily = MaterialTheme.Typography.pragatiNarrow.fontFamily,
                        color = if (state.selectedSuggestionIndex != null)
                            MaterialTheme.Colors.textBlack
                        else
                            MaterialTheme.Colors.backgroundMaroonDark
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompleteTaskBottomSheet(
    onEvent: (HomeContract.Event) -> Unit,
    state: HomeContract.State,
    sheetState: SheetState,
) {
    val task = state.taskToComplete ?: return

    ModalBottomSheet(
        onDismissRequest = { onEvent(HomeContract.Event.CompleteTaskEvent.Cancel) },
        dragHandle = null,
        sheetState = sheetState,
        containerColor = MaterialTheme.Colors.backgroundMaroonLight
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (!state.isPreviewVisible) {
                Text(
                    text = stringResource(R.string.home_screen_task_details),
                    fontFamily = MaterialTheme.Typography.pragatiNarrow.fontFamily,
                    fontSize = 22.sp,
                    color = MaterialTheme.Colors.textWhite,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.Colors.backgroundMaroonDark
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = task.title,
                            fontFamily = MaterialTheme.Typography.gantari.fontFamily,
                            fontSize = 16.sp,
                            color = MaterialTheme.Colors.textWhite
                        )
                        Text(
                            text = task.categoryTitle,
                            fontFamily = MaterialTheme.Typography.gantari.fontFamily,
                            fontSize = 12.sp,
                            color = MaterialTheme.Colors.textOrange
                        )
                    }
                }

                Text(
                    text = "Rate Your Experience",
                    color = MaterialTheme.Colors.textWhite,
                    fontSize = 22.sp,
                    fontFamily = MaterialTheme.Typography.pragatiNarrow.fontFamily,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(0.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.wrapContentWidth()
                    ) {
                        (1..5).forEach { star ->
                            IconButton(
                                onClick = {
                                    onEvent(HomeContract.Event.CompleteTaskEvent.SetRating(star))
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = if (state.selectedRating >= star)
                                        MaterialTheme.Colors.textOrange
                                    else
                                        MaterialTheme.Colors.backgroundMaroonDark,
                                    modifier = Modifier.size(25.dp)
                                )
                            }
                        }
                    }
                }

                Text(
                    text = stringResource(R.string.home_screen_complete),
                    fontFamily = MaterialTheme.Typography.pragatiNarrow.fontFamily,
                    fontSize = 22.sp,
                    color = MaterialTheme.Colors.textWhite,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    WrapButton(
                        onClick = {
                            onEvent(HomeContract.Event.CompleteTaskEvent.ChooseWithoutPhoto)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.Colors.buttonOrange
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = stringResource(R.string.home_screen_complete_without_photo),
                            fontFamily = MaterialTheme.Typography.pragatiNarrow.fontFamily,
                            fontSize = 18.sp,
                            color = MaterialTheme.Colors.textBlack
                        )
                    }

                    WrapButton(
                        onClick = {
                            onEvent(HomeContract.Event.CompleteTaskEvent.ChooseWithPhoto)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.Colors.buttonOrange
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = stringResource(R.string.home_screen_complete_with_photo),
                            fontFamily = MaterialTheme.Typography.pragatiNarrow.fontFamily,
                            fontSize = 18.sp,
                            color = MaterialTheme.Colors.textBlack
                        )
                    }
                }
            } else {
                Text(
                    text = stringResource(R.string.home_screen_preview_post),
                    fontFamily = MaterialTheme.Typography.gantari.fontFamily,
                    fontSize = 24.sp,
                    color = MaterialTheme.Colors.textWhite,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        state.profilePictureUrl?.let {
                            AsyncImage(
                                model = it,
                                contentDescription = stringResource(R.string.home_screen_profile_picture),
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        Column {
                            Text(
                                text = state.username,
                                fontFamily = MaterialTheme.Typography.gantari.fontFamily,
                                fontSize = 14.sp,
                                lineHeight = 14.sp,
                                color = MaterialTheme.Colors.textWhite
                            )
                            Text(
                                text = "${task.title} | ${task.categoryTitle}",
                                fontFamily = MaterialTheme.Typography.gantari.fontFamily,
                                fontSize = 14.sp,
                                lineHeight = 14.sp,
                                color = MaterialTheme.Colors.textOrange
                            )
                        }
                    }
                }

                val context = LocalContext.current

                when {
                    state.isUsingCustomPhoto && state.photoUri != null -> {
                        val painter = rememberAsyncImagePainter(
                            model = ImageRequest.Builder(context)
                                .data(state.photoUri)
                                .crossfade(true)
                                .build()
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp)
                                .clip(MaterialTheme.shapes.medium),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painter,
                                contentDescription = stringResource(R.string.home_screen_captured_photo),
                                modifier = Modifier.matchParentSize(),
                                contentScale = ContentScale.Crop
                            )

                            if (painter.state is AsyncImagePainter.State.Loading) {
                                CircularProgressIndicator(color = MaterialTheme.Colors.textOrange)
                            }
                        }
                    }

                    task.defaultPicture.isNotBlank() -> {
                        val painter = rememberAsyncImagePainter(
                            model = ImageRequest.Builder(context)
                                .data(task.defaultPicture)
                                .crossfade(true)
                                .build()
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp)
                                .clip(MaterialTheme.shapes.medium),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painter,
                                contentDescription = stringResource(R.string.home_screen_default_task_image),
                                modifier = Modifier.matchParentSize(),
                                contentScale = ContentScale.Crop
                            )

                            if (painter.state is AsyncImagePainter.State.Loading) {
                                ThriveOnCircularProgressIndicator()
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    WrapTextButton(
                        onClick = {
                            onEvent(HomeContract.Event.CompleteTaskEvent.Cancel)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = stringResource(R.string.home_screen_cancel),
                            fontFamily = MaterialTheme.Typography.pragatiNarrow.fontFamily,
                            fontSize = 18.sp,
                            color = MaterialTheme.Colors.textWhite
                        )
                    }

                    WrapButton(
                        onClick = {
                            onEvent(HomeContract.Event.CompleteTaskEvent.ConfirmCompletion)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.Colors.buttonOrange
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = stringResource(R.string.home_screen_confirm),
                            fontFamily = MaterialTheme.Typography.pragatiNarrow.fontFamily,
                            fontSize = 18.sp,
                            color = MaterialTheme.Colors.textWhite
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PrivateTaskCard(
    modifier: Modifier = Modifier,
    task: PrivateTask,
    onCompleteClick: (Long) -> Unit,
) {
    val formattedDate = task.dueOn?.let {
        runCatching {
            val parsed = LocalDate.parse(it)
            parsed.format(DateTimeFormatter.ofPattern("MM/dd"))
        }.getOrElse { null }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.Colors.backgroundMaroonLight)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    top = 5.dp,
                    bottom = 5.dp,
                    end = 4.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = task.taskTitle,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 14.sp,
                        fontFamily = MaterialTheme.Typography.gantari.fontFamily
                    ),
                    color = MaterialTheme.Colors.textWhite,
                    maxLines = 2
                )
                formattedDate?.let {
                    Text(
                        text = stringResource(R.string.home_screen_due, it),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = MaterialTheme.Typography.gantari.fontFamily
                        ),
                        color = MaterialTheme.Colors.textOrange.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            WrapIconButton(
                onClick = { onCompleteClick(task.id) },
                modifier = Modifier.size(60.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.checkmark),
                    contentDescription = stringResource(R.string.home_screen_complete),
                    tint = MaterialTheme.Colors.completeGreen,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}

@Composable
fun DailyTaskCard(
    task: FirebaseTask,
    isCompleted: Boolean,
    onCompleteClick: () -> Unit,
) {
    val backgroundColor = if (isCompleted) {
        MaterialTheme.Colors.taskCardGreen
    } else {
        MaterialTheme.Colors.taskCardOrange
    }

    val iconTint = if (isCompleted) {
        MaterialTheme.Colors.completeGreen
    } else {
        MaterialTheme.Colors.uncompletedGray
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = MaterialTheme.Typography.gantari.fontFamily,
                        fontSize = 14.sp
                    ),
                    color = MaterialTheme.Colors.textWhite,
                    maxLines = 2
                )

                Text(
                    text = task.categoryTitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = MaterialTheme.Typography.gantari.fontFamily,
                        fontSize = 13.sp
                    ),
                    color = MaterialTheme.Colors.textBlack
                )
            }

            WrapIconButton(
                onClick = onCompleteClick,
                enabled = !isCompleted,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.checkmark),
                    contentDescription = stringResource(R.string.home_screen_complete),
                    tint = iconTint,
                    modifier = Modifier.size(56.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerRow(
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate?) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val today = LocalDate.now()
    val initialDate = selectedDate ?: today
    val initialMillis = initialDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialMillis,
        yearRange = today.year..today.year,
        initialDisplayMode = DisplayMode.Picker,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val date = Instant.ofEpochMilli(utcTimeMillis)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                return !date.isBefore(today)
            }
        }
    )

    var showDialog by remember { mutableStateOf(false) }

    val label = selectedDate?.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
        ?: stringResource(R.string.home_screen_select_due_date)

    WrapButton(
        onClick = { showDialog = true },
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp)
            .clip(RoundedCornerShape(16.dp))
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 18.sp,
                fontFamily = MaterialTheme.Typography.pragatiNarrow.fontFamily
            )
        )
    }

    if (showDialog) {
        DatePickerDialog(
            onDismissRequest = {
                showDialog = false
                onDismissRequest()
            },
            colors = DatePickerDefaults.colors(
                containerColor = MaterialTheme.Colors.backgroundMaroonLight,
            ),
            confirmButton = {},
            dismissButton = {}
        ) {
            Surface(
                color = MaterialTheme.Colors.backgroundMaroonLight,
                tonalElevation = 100.dp,
                shape = RoundedCornerShape(28.dp)
            ) {
                Column(
                    modifier = Modifier
                        .background(MaterialTheme.Colors.backgroundMaroonLight)
                        .padding(bottom = 12.dp)
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
                            val selected = datePickerState.selectedDateMillis?.let {
                                Instant.ofEpochMilli(it)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                            }
                            Text(
                                text = selected?.toString() ?: "",
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
                            showDialog = false
                            onDismissRequest()
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
                                val selected = Instant.ofEpochMilli(millis)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                                onDateSelected(selected)
                            }
                            showDialog = false
                            onDismissRequest()
                        }) {
                            Text(
                                text = stringResource(R.string.confirm_date),
                                fontSize = 16.sp,
                                color = MaterialTheme.Colors.textOrange,
                                fontFamily = MaterialTheme.Typography.pragatiNarrow.fontFamily,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionDivider(title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        HorizontalDivider(
            modifier = Modifier
                .weight(1f)
                .height(1.dp),
            color = MaterialTheme.Colors.dividerGray
        )
        Text(
            text = title,
            modifier = Modifier.padding(horizontal = 12.dp),
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = MaterialTheme.Typography.gantari.fontFamily
            ),
            color = MaterialTheme.Colors.textWhite
        )
        HorizontalDivider(
            modifier = Modifier
                .weight(1f)
                .height(1.dp),
            color = MaterialTheme.Colors.dividerGray
        )
    }
}

@Composable
fun ResetCountdownText() {
    var timeLeft by remember { mutableStateOf(getTimeUntilMidnight()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            timeLeft = getTimeUntilMidnight()
        }
    }

    Text(
        text = buildAnnotatedString {
            append("Tasks reset in: ")
            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.Colors.textOrange,
                    fontFamily = MaterialTheme.Typography.gantari.fontFamily
                )
            ) {
                append(timeLeft)
            }
        },
        style = MaterialTheme.typography.bodyMedium.copy(
            fontFamily = MaterialTheme.Typography.gantari.fontFamily
        ),
        color = MaterialTheme.Colors.textWhite
    )
}

@SuppressLint("DefaultLocale")
fun getTimeUntilMidnight(): String {
    val now = LocalDateTime.now()
    val midnight = now.toLocalDate().plusDays(1).atStartOfDay()
    val duration = Duration.between(now, midnight)

    val hours = duration.toHours()
    val minutes = (duration.toMinutes() % 60)
    val seconds = (duration.seconds % 60)

    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}
