package gr.aueb.thriveon.ui.common.components.wrap

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetDefaults
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SheetValue.Hidden
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import gr.aueb.thriveon.ui.common.components.spacers.VSpacer
import gr.aueb.thriveon.ui.theme.ThriveOnTheme
import gr.aueb.thriveon.ui.theme.Colors

/**
 * Reusable Wrapper Composable to be used instead of [ModalBottomSheet].
 *
 * This composable wraps [ModalBottomSheet] and provides additional customization options for the appearance,
 * behavior, and interaction of the bottom sheet.
 *
 * @param modifier The [Modifier] to be applied to the composable, allowing for layout and styling customization.
 * @param onDismissRequest A lambda function that is invoked when the bottom sheet is dismissed.
 * @param sheetState The state of the bottom sheet, typically managed using [rememberModalBottomSheetState].
 * The default is [rememberModalBottomSheetState()].
 * @param sheetMaxWidth The maximum width of the sheet. The default value is [BottomSheetDefaults.SheetMaxWidth].
 * @param shape The shape of the sheet, which affects its corner radius. The default value is [BottomSheetDefaults.ExpandedShape].
 * @param containerColor The background color of the bottom sheet container. The default value is [BottomSheetDefaults.ContainerColor].
 * @param contentColor The content color (such as text and icon colors) that is applied to the sheet. The default value is [contentColorFor(containerColor)].
 * @param tonalElevation The tonal elevation of the bottom sheet, which defines its shadow. The default is 0.dp.
 * @param scrimColor The color of the scrim (the overlay behind the sheet). The default is [BottomSheetDefaults.ScrimColor].
 * @param dragHandle A composable function for the drag handle at the top of the bottom sheet. The default is [BottomSheetDefaults.DragHandle()].
 * @param contentWindowInsets A composable that defines the window insets, which controls the layout of the sheet relative to other UI elements.
 * The default is [BottomSheetDefaults.windowInsets].
 * @param properties The properties for the modal bottom sheet, including settings such as whether it should dismiss when tapping outside.
 * The default is [ModalBottomSheetDefaults.properties].
 * @param content A composable function that defines the content to be displayed inside the bottom sheet.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WrapModalBottomSheet(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(),
    sheetMaxWidth: Dp = BottomSheetDefaults.SheetMaxWidth,
    shape: Shape = BottomSheetDefaults.ExpandedShape,
    containerColor: Color = MaterialTheme.Colors.backgroundMaroonLight,
    contentColor: Color = contentColorFor(containerColor),
    tonalElevation: Dp = 0.dp,
    scrimColor: Color = BottomSheetDefaults.ScrimColor,
    dragHandle: @Composable (() -> Unit)? = { BottomSheetDefaults.DragHandle() },
    contentWindowInsets: @Composable () -> WindowInsets = { BottomSheetDefaults.windowInsets },
    properties: ModalBottomSheetProperties = ModalBottomSheetDefaults.properties,
    content: @Composable ColumnScope.() -> Unit
) {
    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        sheetMaxWidth = sheetMaxWidth,
        shape = shape,
        containerColor = containerColor,
        contentColor = contentColor,
        tonalElevation = tonalElevation,
        scrimColor = scrimColor,
        dragHandle = dragHandle,
        contentWindowInsets = contentWindowInsets,
        properties = properties,
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberModalBottomSheetState(
    skipPartiallyExpanded: Boolean = false,
    confirmValueChange: (SheetValue) -> Boolean = { true },
    initialValue: SheetValue = Hidden,
    skipHiddenState: Boolean = false,
): SheetState {
    val density = LocalDensity.current
    return rememberSaveable(
        skipPartiallyExpanded,
        confirmValueChange,
        skipHiddenState,
        saver =
            SheetState.Saver(
                skipPartiallyExpanded = skipPartiallyExpanded,
                confirmValueChange = confirmValueChange,
                density = density,
                skipHiddenState = skipHiddenState,
            )
    ) {
        SheetState(
            skipPartiallyExpanded,
            density,
            initialValue,
            confirmValueChange,
            skipHiddenState,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun WrapModalBottomSheetPreview() {
    ThriveOnTheme {
        val sheetState = rememberModalBottomSheetState(
            initialValue = SheetValue.Expanded
        )

        WrapModalBottomSheet(
            sheetState = sheetState,
            scrimColor = MaterialTheme.Colors.backgroundMaroonDark,
            onDismissRequest = {}
        ) {
            val colors = MaterialTheme.Colors
            val typography = MaterialTheme.typography

            Text(
                text = "Bottom Sheet Title",
                style = typography.titleLarge,
                color = colors.textWhite
            )
            Text(
                text = "This is some body text inside the bottom sheet.",
                style = typography.bodyMedium,
                color = colors.textLightBlue
            )

            VSpacer.Custom(200f)
        }
    }
}
