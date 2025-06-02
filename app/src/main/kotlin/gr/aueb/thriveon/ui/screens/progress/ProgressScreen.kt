package gr.aueb.thriveon.ui.screens.progress

import android.annotation.SuppressLint
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gr.aueb.thriveon.R
import gr.aueb.thriveon.ui.common.components.ThriveOnCircularProgressIndicator
import gr.aueb.thriveon.ui.common.components.spacers.VSpacer
import gr.aueb.thriveon.ui.common.components.wrap.WrapScaffold
import gr.aueb.thriveon.ui.common.components.wrap.WrapTopAppBar
import gr.aueb.thriveon.ui.screens.progress.model.ProgressContract
import gr.aueb.thriveon.ui.theme.Colors
import gr.aueb.thriveon.ui.theme.Typography
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    userId: String,
    state: ProgressContract.State,
    onEvent: (ProgressContract.Event) -> Unit,
    effect: Flow<ProgressContract.Effect>,
    onNavigateBack: () -> Unit,
) {
    LaunchedEffect(Unit) {
        onEvent(ProgressContract.Event.Init(userId))
        effect.collect { effect ->
            when (effect) {
                ProgressContract.Effect.NavigateBack -> onNavigateBack()
            }
        }
    }

    WrapScaffold(
        containerColor = MaterialTheme.Colors.backgroundMaroonDark,
        topBar = {
            WrapTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.progress_screen_title),
                        fontFamily = MaterialTheme.Typography.gantari.fontFamily,
                        fontSize = 30.sp,
                        color = MaterialTheme.Colors.textOrange
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onEvent(ProgressContract.Event.OnBackClick) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = null,
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
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    ThriveOnCircularProgressIndicator()
                }
            } else {
                ProgressCarousel(state = state)
            }
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun ProgressCarousel(state: ProgressContract.State) {
    if (state.categoryProgress.isEmpty()) {
        Text(
            text = "No progress available.",
            color = MaterialTheme.Colors.textWhite,
            modifier = Modifier.padding(12.dp)
        )
        return
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    val total = state.categoryProgress.size
    val isAtStart = currentIndex == 0
    val isAtEnd = currentIndex == total - 1

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Crossfade(targetState = state.categoryProgress[currentIndex]) { categoryItem ->

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                VSpacer.Medium()

                Text(
                    text = categoryItem.category,
                    fontFamily = MaterialTheme.Typography.gantari.fontFamily,
                    fontSize = 22.sp,
                    color = MaterialTheme.Colors.textWhite,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${categoryItem.progress} / 60",
                    fontFamily = MaterialTheme.Typography.gantari.fontFamily,
                    fontSize = 16.sp,
                    color = MaterialTheme.Colors.textWhite
                )

                Spacer(modifier = Modifier.height(12.dp))

                StaircaseProgressGraph(
                    progress = categoryItem.progress,
                    milestones = listOf(0, 5, 15, 25, 40, 60),
                    modifier = Modifier
                        .padding(8.dp)
                        .height(200.dp)
                        .fillMaxWidth()
                )

                categoryItem.currentMilestone?.let { milestone ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        categoryItem.badge?.let { badge ->
                            Text(
                                text = badge,
                                fontSize = 20.sp,
                                modifier = Modifier.padding(end = 6.dp)
                            )
                        }

                        Text(
                            text = milestone.milestoneTitle,
                            color = MaterialTheme.Colors.textOrange,
                            fontFamily = MaterialTheme.Typography.gantari.fontFamily,
                            fontSize = 18.sp
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    IconButton(
                        onClick = { currentIndex-- },
                        enabled = !isAtStart
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = null,
                            tint = if (!isAtStart) MaterialTheme.Colors.textOrange else MaterialTheme.Colors.backgroundMaroonLight
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = "${currentIndex + 1} / $total",
                        color = MaterialTheme.Colors.textWhite,
                        fontFamily = MaterialTheme.Typography.gantari.fontFamily,
                        fontSize = 18.sp
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    IconButton(
                        onClick = { currentIndex++ },
                        enabled = !isAtEnd
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowForward,
                            contentDescription = null,
                            tint = if (!isAtEnd) MaterialTheme.Colors.textOrange else MaterialTheme.Colors.backgroundMaroonLight
                        )
                    }
                }

                if (categoryItem.completedTasks.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.progress_screen_completed_tasks),
                            color = MaterialTheme.Colors.textOrange,
                            fontSize = 18.sp,
                            fontFamily = MaterialTheme.Typography.gantari.fontFamily,
                        )
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 250.dp)
                            .padding(horizontal = 8.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        categoryItem.completedTasks
                            .sortedByDescending { it.date }
                            .forEach { task ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.Colors.backgroundMaroonLight
                                    ),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = task.taskTitle,
                                            color = MaterialTheme.Colors.textWhite,
                                            fontSize = 15.sp,
                                            fontFamily = MaterialTheme.Typography.gantari.fontFamily,
                                            maxLines = 2
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = stringResource(R.string.progress_screen_completed_date, task.date),
                                            color = MaterialTheme.Colors.textOrange,
                                            fontSize = 13.sp,
                                            fontFamily = MaterialTheme.Typography.gantari.fontFamily
                                        )
                                    }
                                }
                            }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 250.dp)
                            .padding(horizontal = 8.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.progress_screen_no_completed_tasks),
                                color = MaterialTheme.Colors.textOrange,
                                fontSize = 18.sp,
                                fontFamily = MaterialTheme.Typography.gantari.fontFamily,
                            )
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun StaircaseProgressGraph(
    progress: Int,
    milestones: List<Int>,
    modifier: Modifier = Modifier
) {
    val circleSize = 16.dp
    val strokeWidth = 8.dp

    BoxWithConstraints(modifier = modifier) {
        val density = LocalDensity.current
        val maxWidthPx = with(density) { constraints.maxWidth.toFloat() }
        val maxHeightPx = with(density) { constraints.maxHeight.toFloat() }

        val points = milestones.map { value ->
            val x = (value / 60f) * maxWidthPx
            val y = maxHeightPx - (value / 60f) * maxHeightPx
            Offset(x, y)
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            val fullPath = Path().apply {
                moveTo(points.first().x, points.first().y)
                for (i in 1 until points.size) {
                    lineTo(points[i].x, points[i].y)
                }
            }

            val progressFraction = progress.coerceIn(0, 60) / 60f
            val totalLength = points.zipWithNext().sumOf { (a, b) ->
                a.distanceTo(b).toDouble()
            }
            val targetLength = progressFraction * totalLength.toFloat()
            val progressPath = Path()

            var drawnLength = 0f
            progressPath.moveTo(points[0].x, points[0].y)

            for (i in 1 until points.size) {
                val start = points[i - 1]
                val end = points[i]
                val segmentLength = start.distanceTo(end)

                if ((drawnLength + segmentLength).toFloat() <= targetLength.toFloat()) {
                    progressPath.lineTo(end.x, end.y)
                    drawnLength += segmentLength
                } else {
                    val remaining = targetLength - drawnLength
                    val fraction = remaining / segmentLength
                    val x = start.x + fraction * (end.x - start.x)
                    val y = start.y + fraction * (end.y - start.y)
                    progressPath.lineTo(x, y)
                    break
                }
            }

            drawPath(
                path = fullPath,
                color = Color(0xFF3A1A1A),
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )

            drawPath(
                path = progressPath,
                color = Color(0xFFF19C16),
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )

            for (i in 1 until points.size) {
                val point = points[i]
                val filled = progress >= milestones[i]

                if (filled) {
                    drawCircle(
                        color = Color(0xFFF19C16),
                        radius = circleSize.toPx() / 2,
                        center = point
                    )
                } else {
                    drawCircle(
                        color = Color.White,
                        radius = circleSize.toPx() / 2,
                        center = point,
                        style = Stroke(width = with(density) { 2.dp.toPx() })
                    )
                }
            }
        }
    }
}

private fun Offset.distanceTo(other: Offset): Float {
    return kotlin.math.hypot((x - other.x), (y - other.y))
}
