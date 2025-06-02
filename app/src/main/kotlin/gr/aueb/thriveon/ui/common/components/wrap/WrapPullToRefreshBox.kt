package gr.aueb.thriveon.ui.common.components.wrap

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import gr.aueb.thriveon.ui.theme.ThriveOnTheme

/**
 * Reusable Wrapper Composable to be used instead of [PullToRefreshBox].
 *
 * This composable provides a wrapper around [PullToRefreshBox], allowing for customizable content, refresh behavior,
 * and refresh indicator appearance.
 *
 * @param modifier The [Modifier] to be applied to the composable, allowing customization of layout and styling.
 * @param isRefreshing A boolean flag indicating whether the pull-to-refresh action is currently in progress.
 * @param onRefresh A lambda function that defines the action to take when a refresh is triggered.
 * @param state The state of the pull-to-refresh box, which is typically managed using [rememberPullToRefreshState].
 * The default value is [rememberPullToRefreshState()].
 * @param contentAlignment Defines the alignment of the content within the box. The default value is [Alignment.TopStart].
 * @param indicator A composable function that renders the refresh indicator. By default, it displays the [Indicator] at the top center.
 * @param content A composable function that defines the main content of the pull-to-refresh box.
 * This is where the content to be refreshed is provided.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WrapPullToRefreshBox(
    modifier: Modifier = Modifier,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    state: PullToRefreshState = rememberPullToRefreshState(),
    contentAlignment: Alignment = Alignment.TopStart,
    indicator: @Composable BoxScope.() -> Unit = {
        Indicator(
            modifier = Modifier.align(Alignment.TopCenter),
            isRefreshing = isRefreshing,
            state = state
        )
    },
    content: @Composable BoxScope.() -> Unit
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier,
        state = state,
        contentAlignment = contentAlignment,
        indicator = indicator,
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun WrapPullToRefreshBoxPreview() {
    ThriveOnTheme {
        WrapPullToRefreshBox(
            isRefreshing = false,
            onRefresh = {}
        ) {}
    }
}
