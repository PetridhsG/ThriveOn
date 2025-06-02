package gr.aueb.thriveon.ui.common.components.wrap

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import gr.aueb.thriveon.ui.theme.Colors
import gr.aueb.thriveon.ui.theme.ThriveOnTheme

/**
 * @param[painter] The foreground of the icon - typically `rememberVectorPainter(icon)` where `icon` is an `ImageVector`
 * @param[contentDescription] text used by accessibility services to describe what this icon represents. This should always be provided unless this icon is used for decorative purposes, and does not represent a meaningful action that a user can take. This text should be localized, such as by using `androidx.compose.ui.res.stringResource` or similar
 * @param[modifier] Modifies the component e.g. size, shape, colors
 * @param[tint] tint to be applied to painter. If `Color.Unspecified` is provided, then no tint is applied.
 * */
@Composable
fun WrapIcon(
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    Icon(
        painter = painter,
        tint = tint,
        contentDescription = contentDescription,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewWrapIcon() {
    ThriveOnTheme {
        WrapIcon(
            painter = rememberVectorPainter(Icons.Default.AccountCircle),
            tint = MaterialTheme.Colors.buttonOrange,
            contentDescription = "Icon"
        )
    }
}
