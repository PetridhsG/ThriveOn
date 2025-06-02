package gr.aueb.thriveon.ui.common.components.wrap

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import gr.aueb.thriveon.ui.theme.Colors
import gr.aueb.thriveon.ui.theme.ThriveOnTheme
import gr.aueb.thriveon.ui.theme.Typography

/**
 * Reusable Wrapper Composable to be used instead of AlertDialog
 *
 * This composable serves as a wrapper around [AlertDialog] providing custom parameters and
 * default values that can be overridden to customize the dialog's appearance and behavior.
 *
 * @param onDismissRequest A lambda function that defines the action to take when the dialog is dismissed.
 * @param confirmButton A composable function for the confirm button content, which is displayed at the bottom of the dialog.
 * @param modifier The [Modifier] to be applied to the composable, allowing customization of layout and styling.
 * @param dismissButton A composable function for the dismiss button content, which is displayed at the bottom of the dialog. This button is optional.
 * @param icon A composable function to display an icon in the dialog's header. This is optional.
 * @param title A composable function to display the title of the dialog, which appears at the top of the dialog. This is optional.
 * @param text A composable function to display the body text of the dialog. This is optional.
 * @param shape Defines the shape of the dialog's container. The default value is [AlertDialogDefaults.shape].
 * @param containerColor Defines the background color of the dialog's container. The default value is [AlertDialogDefaults.containerColor].
 * @param iconContentColor Defines the color of the icon content. The default value is [AlertDialogDefaults.iconContentColor].
 * @param titleContentColor Defines the color of the title content. The default value is [AlertDialogDefaults.titleContentColor].
 * @param textContentColor Defines the color of the text content. The default value is [AlertDialogDefaults.textContentColor].
 * @param tonalElevation Defines the elevation of the dialog, affecting the shadow and depth. The default value is [AlertDialogDefaults.TonalElevation].
 * @param properties Defines additional properties for the dialog, such as whether it is cancellable, which is passed through [DialogProperties].
 */
@Composable
fun WrapAlertDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    dismissButton: @Composable (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    title: @Composable (() -> Unit)? = null,
    text: @Composable (() -> Unit)? = null,
    shape: Shape = AlertDialogDefaults.shape,
    containerColor: Color = AlertDialogDefaults.containerColor,
    iconContentColor: Color = AlertDialogDefaults.iconContentColor,
    titleContentColor: Color = AlertDialogDefaults.titleContentColor,
    textContentColor: Color = AlertDialogDefaults.textContentColor,
    tonalElevation: Dp = AlertDialogDefaults.TonalElevation,
    properties: DialogProperties = DialogProperties()
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = confirmButton,
        modifier = modifier,
        dismissButton = dismissButton,
        icon = icon,
        title = title,
        text = text,
        shape = shape,
        containerColor = containerColor,
        iconContentColor = iconContentColor,
        titleContentColor = titleContentColor,
        textContentColor = textContentColor,
        tonalElevation = tonalElevation,
        properties = properties
    )
}

@Preview(showBackground = true)
@Composable
private fun WrapBasicAlertDialogPreview() {
    ThriveOnTheme {
       Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.Colors.backgroundMaroonLight
        ) {
            WrapAlertDialog(
                onDismissRequest = {},
                containerColor = MaterialTheme.Colors.backgroundMaroonLight,
                confirmButton = {
                    WrapButton(onClick = {}) {
                        Text(
                            text = "Confirm",
                            fontFamily = MaterialTheme.Typography.pragatiNarrow.fontFamily,
                            fontSize = 16.sp
                        )
                    }
                },
                dismissButton = {
                    WrapTextButton(onClick = {}) {
                        Text(
                            text = "Dismiss",
                            fontFamily = MaterialTheme.Typography.pragatiNarrow.fontFamily,
                            fontSize = 16.sp
                        )
                    }
                },
                title = {
                    Text(
                        text = "Dialog Title",
                        color = MaterialTheme.Colors.textWhite,
                        fontFamily = MaterialTheme.Typography.pragatiNarrow.fontFamily,
                        fontSize = 20.sp
                    )
                },
                text = {
                    Text(
                        text = "This is the dialog content.",
                        color = MaterialTheme.Colors.textWhite,
                        fontSize = 16.sp
                    )
                }
            )
        }
    }
}

