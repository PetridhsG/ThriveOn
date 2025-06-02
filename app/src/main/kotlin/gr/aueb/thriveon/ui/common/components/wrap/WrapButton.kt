package gr.aueb.thriveon.ui.common.components.wrap

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gr.aueb.thriveon.ui.theme.Colors
import gr.aueb.thriveon.ui.theme.ThriveOnTheme
import gr.aueb.thriveon.ui.theme.Typography

const val THROTTLE_TIME = 1000L
/**
 * @param[onClick] The function that will be executed when clicked
 * @param[modifier] Modifies the component e.g. size, shape, colors
 * @param[enabled] Controls the enabled state of this wrap button. When false, this component will not respond to user input, and it will appear visually disabled and disabled to accessibility services.
 * @param[elevation] `ButtonElevation` used to resolve the elevation for this wrap button in different states. This controls the size of the shadow below the button.
 * @param[border] The border to draw around the container of this wrap button
 * @param[contentPadding] The spacing values to apply internally between the container and the content
 * @param[interactionSource] an optional hoisted `MutableInteractionSource` for observing and emitting Interactions for this button. You can use this to change the button's appearance or preview the button in different states. Note that if null is provided, interactions will still happen internally.
 * @param[content] The content of this wrap text button
 * */
@Composable
fun WrapButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.shape,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.Colors.buttonOrange,
        contentColor = MaterialTheme.Colors.textBlack
    ),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource? = null,
    throttleDelayMillis: Long = THROTTLE_TIME,
    content: @Composable RowScope.() -> Unit
) {
    val lastClickTime = remember { mutableLongStateOf(0L) }
    Button(
        onClick = {
            val now = System.currentTimeMillis()
            if (now - lastClickTime.longValue >= throttleDelayMillis) {
                lastClickTime.longValue = now
                onClick()
            }
        },
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        contentPadding = contentPadding,
        interactionSource = interactionSource ?: remember { MutableInteractionSource() },
        content = content
    )
}

/**
 * @param[onClick] The function that will be executed when clicked
 * @param[modifier] Modifies the component e.g. size, shape, colors
 * @param[enabled] Controls the enabled state of this icon button. When false, this component will not respond to user input, and it will appear visually disabled and disabled to accessibility services.
 * @param[colors] `IconButtonColors` that will be used to resolve the colors used for this wrap icon button in different states.
 * @param[interactionSource] an optional hoisted `MutableInteractionSource` for observing and emitting Interactions for this icon button. You can use this to change the icon button's appearance or preview the icon button in different states. Note that if null is provided, interactions will still happen internally.
 * @param[content] The content of this wrap icon button - typically an `Icon`
 * */
@Composable
fun WrapIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(
        contentColor = MaterialTheme.Colors.textOrange
    ),
    interactionSource: MutableInteractionSource? = null,
    throttleDelayMillis: Long = THROTTLE_TIME,
    content: @Composable () -> Unit
) {
    val lastClickTime = remember { mutableLongStateOf(0L) }

    IconButton(
        onClick = {
            val now = System.currentTimeMillis()
            if (now - lastClickTime.longValue >= throttleDelayMillis) {
                lastClickTime.longValue = now
                onClick()
            }
        },
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource ?: remember { MutableInteractionSource() },
        content = content
    )
}

@Composable
fun WrapOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.outlinedShape,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.Colors.buttonOrange,
        contentColor = MaterialTheme.Colors.textBlack
    ),
    elevation: ButtonElevation? = null,
    border: BorderStroke? = BorderStroke(1.dp, MaterialTheme.Colors.textBlack),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource? = null,
    throttleDelayMillis: Long = THROTTLE_TIME,
    content: @Composable (RowScope.() -> Unit)
) {
    WrapButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        throttleDelayMillis = throttleDelayMillis,
        content = content
    )
}

/**
 * @param[onClick] The function that will be executed when clicked
 * @param[modifier] Modifies the component e.g. size, shape, colors
 * @param[enabled] Controls the enabled state of this text button. When false, this component will not respond to user input, and it will appear visually disabled and disabled to accessibility services.
 * @param[colors] `ButtonColors` that will be used to resolve the colors used for this wrap text button in different states.
 * @param[elevation] `ButtonElevation` used to resolve the elevation for this wrap text button in different states. This controls the size of the shadow below the button.
 * @param[border] The border to draw around the container of this wrap text button
 * @param[contentPadding] The spacing values to apply internally between the container and the content
 * @param[interactionSource] an optional hoisted `MutableInteractionSource` for observing and emitting Interactions for this button. You can use this to change the button's appearance or preview the button in different states. Note that if null is provided, interactions will still happen internally.
 * @param[content] The content of this wrap text button
 * */
@Composable
fun WrapTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.textShape,
    colors: ButtonColors = ButtonDefaults.textButtonColors(
        contentColor = MaterialTheme.Colors.textBlack
    ),
    elevation: ButtonElevation? = null,
    border: BorderStroke? = BorderStroke(1.dp, MaterialTheme.Colors.textOrange),
    contentPadding: PaddingValues = ButtonDefaults.TextButtonContentPadding,
    interactionSource: MutableInteractionSource? = null,
    throttleDelayMillis: Long = THROTTLE_TIME,
    content: @Composable (RowScope.() -> Unit)
) {
    val lastClickTime = remember { mutableLongStateOf(0L) }

    TextButton(
        onClick = {
            val now = System.currentTimeMillis()
            if (now - lastClickTime.longValue >= throttleDelayMillis) {
                lastClickTime.longValue = now
                onClick()
            }
        },
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        contentPadding = contentPadding,
        interactionSource = interactionSource ?: remember { MutableInteractionSource() },
        content = content
    )
}


@Preview(showBackground = true)
@Composable
fun PreviewButtons() {
    ThriveOnTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.Colors.backgroundMaroonLight)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            WrapButton(onClick = {}) {
                Text(
                    text = "Button",
                    fontFamily = MaterialTheme.Typography.pragatiNarrow.fontFamily,
                    fontSize = 24.sp
                )
            }

            WrapIconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "Settings Icon",
                )
            }

            WrapOutlinedButton(onClick = {}) {
                Text(
                    text = "Outlined Button",
                    fontFamily = MaterialTheme.Typography.pragatiNarrow.fontFamily,
                    color = MaterialTheme.Colors.textBlack
                )
            }

            WrapTextButton(onClick = {}) {
                Text(
                    text = "Text Button",
                    fontFamily = MaterialTheme.Typography.pragatiNarrow.fontFamily,
                    fontSize = 24.sp
                )
            }
        }
    }
}
