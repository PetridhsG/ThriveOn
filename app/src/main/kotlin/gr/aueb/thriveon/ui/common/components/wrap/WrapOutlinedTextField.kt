package gr.aueb.thriveon.ui.common.components.wrap

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import gr.aueb.thriveon.ui.theme.Colors
import gr.aueb.thriveon.ui.theme.ThriveOnTheme

/**
 * Reusable Wrapper Composable to be used instead of [OutlinedTextField].
 *
 * This composable serves as a wrapper for [OutlinedTextField] with customizable properties for appearance, behavior, and interaction.
 * It allows the creation of an outlined text field with many configurable options such as icons, labels, supporting text, and more.
 *
 * @param modifier The [Modifier] to be applied to the composable, allowing layout and styling customization.
 * @param value The current value of the text field.
 * @param onValueChange A lambda function that defines the action to take when the value of the text field changes.
 * @param enabled A Boolean flag indicating whether the text field is enabled. The default is true.
 * @param readOnly A Boolean flag indicating whether the text field is read-only. The default is false.
 * @param textStyle The style of the text, typically used to customize the font and text appearance. The default is [LocalTextStyle].
 * @param label A composable function that provides the label for the text field. This is optional.
 * @param placeholder A composable function that provides the placeholder text when the text field is empty. This is optional.
 * @param leadingIcon A composable function that provides an icon to be displayed at the start of the text field. This is optional.
 * @param trailingIcon A composable function that provides an icon to be displayed at the end of the text field. This is optional.
 * @param prefix A composable function to display a prefix before the text. This is optional.
 * @param suffix A composable function to display a suffix after the text. This is optional.
 * @param supportingText A composable function that provides additional information or a helper text below the text field. This is optional.
 * @param isError A Boolean flag indicating whether the text field is in an error state. The default is false.
 * @param visualTransformation A transformation applied to the text, such as password masking. The default is [VisualTransformation.None].
 * @param keyboardOptions Configurations for the keyboard, such as the type of input. The default is [KeyboardOptions.Default].
 * @param keyboardActions Actions associated with keyboard interactions, such as handling the "Enter" key. The default is [KeyboardActions.Default].
 * @param singleLine A Boolean flag that indicates whether the text field should be a single-line field. The default is false.
 * @param maxLines The maximum number of lines the text field can expand to. The default is [Int.MAX_VALUE] unless `singleLine` is true.
 * @param minLines The minimum number of lines the text field should occupy. The default is 1.
 * @param interactionSource The interaction source used to track interactions with the text field. This is optional.
 * @param shape The shape of the text field, affecting its corner radius. The default is [OutlinedTextFieldDefaults.shape].
 */
@Composable
fun WrapOutlinedTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit = {},
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource? = null,
    shape: Shape = OutlinedTextFieldDefaults.shape,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.Colors.textOrange,
        unfocusedBorderColor = MaterialTheme.Colors.componentOrangeOutline.copy(alpha = 0.6f),
        disabledBorderColor = MaterialTheme.Colors.componentOrangeOutline.copy(alpha = 0.3f),
        errorBorderColor = Color.Red,
        cursorColor = MaterialTheme.Colors.textOrange,
        focusedLabelColor = MaterialTheme.Colors.textOrange,
        unfocusedLabelColor = MaterialTheme.Colors.textWhite.copy(alpha = 0.7f),
        errorLabelColor = Color.Red,
        focusedLeadingIconColor = MaterialTheme.Colors.textOrange,
        unfocusedLeadingIconColor = MaterialTheme.Colors.textWhite,
        focusedTrailingIconColor = MaterialTheme.Colors.textOrange,
        unfocusedTrailingIconColor = MaterialTheme.Colors.textWhite,
        errorTrailingIconColor = Color.Red,
        focusedTextColor = MaterialTheme.Colors.textWhite,
        unfocusedTextColor = MaterialTheme.Colors.textWhite,
        errorTextColor = MaterialTheme.Colors.textWhite
    )
) {
    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        prefix = prefix,
        suffix = suffix,
        supportingText = supportingText,
        isError = isError,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        interactionSource = interactionSource,
        shape = shape,
        colors = colors
    )
}

@Preview(showBackground = true)
@Composable
private fun WrapOutlinedTextFieldPreview() {
    ThriveOnTheme {
        WrapOutlinedTextField(
            value = "Hello world!",
            onValueChange = {}
        )
    }
}
