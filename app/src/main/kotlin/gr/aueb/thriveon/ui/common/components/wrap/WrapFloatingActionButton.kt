package gr.aueb.thriveon.ui.common.components.wrap

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.FloatingActionButtonElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import gr.aueb.thriveon.ui.theme.Colors
import gr.aueb.thriveon.ui.theme.ThriveOnTheme

/**
 * A composable function that creates a floating action button with customizable properties.
 *
 * @param[onClick] The function that will be executed when the floating action button is clicked.
 * @param[modifier] Modifies the component, e.g., size, shape, or positioning.
 * @param[shape] Defines the shape of the floating action button. Defaults to `FloatingActionButtonDefaults.shape`.
 * @param[containerColor] The background color of the floating action button. Defaults to `FloatingActionButtonDefaults.containerColor`.
 * @param[contentColor] The color used for the content inside the floating action button. Defaults to `contentColorFor(containerColor)`.
 * @param[elevation] `FloatingActionButtonElevation` used to resolve the elevation for the button in different states. This controls the size of the shadow below the button.
 * @param[interactionSource] An optional hoisted `MutableInteractionSource` for observing and emitting interactions for this button. If null, interactions will still happen internally.
 * @param[content] The content of this floating action button, typically an `Icon`.
 */
@Composable
fun WrapFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = FloatingActionButtonDefaults.shape,
    containerColor: Color = FloatingActionButtonDefaults.containerColor,
    contentColor: Color = contentColorFor(containerColor),
    elevation: FloatingActionButtonElevation = FloatingActionButtonDefaults.elevation(),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable () -> Unit
){
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        shape = shape,
        containerColor = containerColor,
        contentColor = contentColor,
        elevation = elevation,
        interactionSource = interactionSource,
        content = content
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewWrapFloatingActionButton() {
    ThriveOnTheme {
        WrapFloatingActionButton(
            onClick = {},
            containerColor = MaterialTheme.Colors.backgroundMaroonLight,
            contentColor = MaterialTheme.Colors.textOrange
        ) {
            WrapIcon(
                painter = rememberVectorPainter(Icons.Default.Add),
                tint = MaterialTheme.Colors.buttonOrange,
                contentDescription = "Icon"
            )
        }
    }
}
