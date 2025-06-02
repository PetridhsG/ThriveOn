package gr.aueb.thriveon.ui.common.components.wrap

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import gr.aueb.thriveon.ui.theme.ThriveOnTheme

/**
 * Wrapper composable function of [ExposedDropdownMenuBox] providing custom styling. It contains a
 * [WrapOutlinedTextField] that displays the [value], and the [content] which is the dropdown menu
 * content. By default, the text-field is read-only. If you want to make this dropdown searchable,
 * set [readonly] to `false`, and pass a lambda to [onValueChange].
 *
 * @param value The text value to display in the text-field.
 * @param label The text value to display as the label of the text-field.
 * @param expanded Whether the dropdown menu is expanded.
 * @param modifier The modifier to be applied.
 * @param placeholder The optional text to display as the placeholder when [value] is blank.
 * @param readonly Whether the text-field is read-only. If you want to create a searchable dropdown
 * menu then set the value to `false`. It has the default value of `true`.
 * @param onClick Callback invoked when the user clicks on the dropdown menu.
 * @param onDismissRequest Callback invoked when the user clicks outside of the dropdown menu.
 * @param onValueChange Callback invoked when the [value] changes.
 * @param content The content to display inside the dropdown menu.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WrapExposedDropdownMenuBox(
    value: String,
    modifier: Modifier = Modifier,
    expanded: Boolean,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    readonly: Boolean = true,
    onClick: (Boolean) -> Unit,
    onDismissRequest: () -> Unit,
    onValueChange: (String) -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onClick,
        modifier = modifier
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = label,
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryEditable, enabled = true)
                .then(modifier),
            readOnly = readonly,
            placeholder = placeholder,
            trailingIcon = {
                val rotation by animateFloatAsState(
                    targetValue = if (expanded) 180f else 0f,
                    label = "dropdown arrow animation"
                )

                Icon(
                    painter = rememberVectorPainter(Icons.Filled.ArrowDropDown),
                    contentDescription = null,
                    modifier = Modifier.graphicsLayer {
                        rotationZ = rotation
                    }
                )
            }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismissRequest,
            content = content,
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WrapExposedDropdownMenuBoxPreview() {
    ThriveOnTheme {
        val options = listOf("Option 1", "Option 2", "Option 3")
        val (expanded, setExpanded) = remember { mutableStateOf(false) }
        val (selectedOption, setSelectedOption) = remember { mutableStateOf(options[0]) }

        WrapExposedDropdownMenuBox(
            value = selectedOption,
            expanded = expanded,
            onClick = { setExpanded(!expanded) },
            onDismissRequest = { setExpanded(false) },
            label = { Text("Select an option") },
            onValueChange = { setSelectedOption(it) }
        ) {
            options.forEach { option ->
                Text(
                    text = option,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            setSelectedOption(option)
                            setExpanded(false)
                        }
                        .padding(16.dp)
                )
            }
        }
    }
}
