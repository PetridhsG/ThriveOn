package gr.aueb.thriveon.ui.screens.titles

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gr.aueb.thriveon.R
import gr.aueb.thriveon.ui.common.components.wrap.WrapScaffold
import gr.aueb.thriveon.ui.common.components.wrap.WrapTopAppBar
import gr.aueb.thriveon.ui.screens.titles.model.TitlesContract
import gr.aueb.thriveon.ui.theme.Colors
import gr.aueb.thriveon.ui.theme.Typography
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TitlesScreen(
    userId: String,
    state: TitlesContract.State,
    onEvent: (TitlesContract.Event) -> Unit,
    effect: Flow<TitlesContract.Effect>,
    onNavigateBack: () -> Unit,
    onNavigateToProgress: (String) -> Unit,
) {
    LaunchedEffect(Unit) {
        onEvent(TitlesContract.Event.Init(userId))
        effect.collect { effect ->
            when (effect) {
                TitlesContract.Effect.NavigateBack -> onNavigateBack()
                is TitlesContract.Effect.NavigateToProgress -> onNavigateToProgress(effect.userId)
            }
        }
    }

    WrapScaffold(
        containerColor = MaterialTheme.Colors.backgroundMaroonDark,
        topBar = {
            WrapTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.titles_screen_title),
                        fontFamily = MaterialTheme.Typography.gantari.fontFamily,
                        fontSize = 30.sp,
                        color = MaterialTheme.Colors.textOrange
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onEvent(TitlesContract.Event.OnBackClick) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = null,
                            tint = MaterialTheme.Colors.textWhite
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onEvent(TitlesContract.Event.OnProgressClick) }) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_auto_graph_24),
                            contentDescription = null,
                            tint = MaterialTheme.Colors.buttonOrange
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
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.Colors.textOrange)
                    }
                }

                state.milestones.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.titles_screen_empty),
                            fontFamily = MaterialTheme.Typography.gantari.fontFamily,
                            fontSize = 15.sp,
                            color = MaterialTheme.Colors.textWhite
                        )
                    }
                }

                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        itemsIndexed(state.milestones) { index, milestone ->
                            Column {
                                Text(
                                    text = stringResource(
                                        R.string.titles_screen_completed_tasks,
                                        milestone.requiredCount,
                                        milestone.category
                                    ),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    fontFamily = MaterialTheme.Typography.gantari.fontFamily,
                                    color = MaterialTheme.Colors.textWhite,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = stringResource(
                                        R.string.titles_screen_milestone_title,
                                        milestone.milestoneTitle,
                                        milestone.badge
                                    ),
                                    fontSize = 16.sp,
                                    fontFamily = MaterialTheme.Typography.gantari.fontFamily,
                                    color = MaterialTheme.Colors.textOrange,
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(top = 4.dp)
                                )

                                if (index < state.milestones.lastIndex) {
                                    HorizontalDivider(
                                        color = MaterialTheme.Colors.dividerGray.copy(alpha = 0.5f),
                                        thickness = 0.5.dp,
                                        modifier = Modifier.padding(top = 12.dp)
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
