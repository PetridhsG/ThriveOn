package gr.aueb.thriveon.ui.common.components.wrap

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import gr.aueb.thriveon.ui.theme.ThriveOnTheme

/**
 * Reusable Wrapper Composable to be used instead of [TopAppBar].
 *
 * This composable wraps the [TopAppBar] and provides a customizable interface for adding title, navigation icon,
 * actions, and other properties related to the app bar appearance and behavior.
 *
 * @param modifier The [Modifier] to be applied to the composable, allowing customization of layout and styling.
 * @param title A composable function that defines the content of the title of the top app bar.
 * @param navigationIcon A composable function for the navigation icon that appears on the left side of the app bar.
 * This is an optional parameter and defaults to an empty composable.
 * @param actions A composable function that allows defining actions (icons or buttons) to be displayed on the right side of the app bar.
 * This is an optional parameter and defaults to an empty composable.
 * @param expandedHeight Defines the expanded height of the app bar. The default value is [TopAppBarDefaults.TopAppBarExpandedHeight].
 * @param windowInsets Defines the window insets that will be used for the app bar. The default value is [TopAppBarDefaults.windowInsets].
 * @param colors Defines the colors to be applied to the top app bar. The default value is [TopAppBarDefaults.topAppBarColors()].
 * @param scrollBehavior Defines the behavior of the app bar when scrolling. If set to null, the default scroll behavior will be used.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WrapTopAppBar(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    expandedHeight: Dp = TopAppBarDefaults.TopAppBarExpandedHeight,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    TopAppBar(
        title = title,
        modifier = modifier,
        navigationIcon = navigationIcon,
        actions = actions,
        expandedHeight = expandedHeight,
        windowInsets = windowInsets,
        colors = colors,
        scrollBehavior = scrollBehavior
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun WrapTopAppBarPreview() {
    ThriveOnTheme {
        WrapTopAppBar(title = {})
    }
}
