package gr.aueb.thriveon.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun ThriveOnTheme(
    content: @Composable () -> Unit
) {

    CompositionLocalProvider(
        LocalCustomColors provides customColors(),
        LocalCustomTypography provides customTypography()
    ) {
        MaterialTheme(
            shapes = customShapes,
            content = content
        )
    }
}
