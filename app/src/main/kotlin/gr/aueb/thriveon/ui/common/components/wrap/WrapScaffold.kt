package gr.aueb.thriveon.ui.common.components.wrap

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gr.aueb.thriveon.ui.theme.Colors
import gr.aueb.thriveon.ui.theme.ThriveOnTheme
import gr.aueb.thriveon.ui.theme.Typography

/** A modified version of Scaffold
 * @param modifier Modifies the component e.g. size, shape, colors
 * @param topBar Sets a top bar for the component
 * @param bottomBar Sets a bottom bar for the component
 * @param floatingActionButton Sets a floating action button for the component
 * @param content The content that is displayed
 * */
@Composable
fun WrapScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    containerColor: Color = MaterialTheme.Colors.backgroundMaroonDark,
    contentColor: Color = contentColorFor(containerColor),
    contentWindowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets,
    content: @Composable (PaddingValues) -> Unit
){
    Scaffold(
        modifier = modifier,
        topBar = topBar,
        bottomBar = bottomBar,
        snackbarHost = snackbarHost,
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
        containerColor = containerColor,
        contentColor = contentColor,
        contentWindowInsets = contentWindowInsets,
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun PreviewWrapScaffold() {
    ThriveOnTheme {
        WrapScaffold(
            content = { padding ->
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    val colors = MaterialTheme.Colors
                    val fonts = MaterialTheme.Typography

                    Text(
                        text = "Istok Web",
                        fontFamily = fonts.istokWeb.fontFamily,
                        fontSize = 24.sp,
                        color = colors.textWhite
                    )

                }
            }
        )
    }
}
