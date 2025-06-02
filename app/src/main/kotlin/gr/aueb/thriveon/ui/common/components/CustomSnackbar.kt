package gr.aueb.thriveon.ui.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import gr.aueb.thriveon.ui.theme.Colors
import gr.aueb.thriveon.ui.theme.Typography

@Composable
fun CustomSnackbarHost(snackbarHostState: SnackbarHostState) {
    SnackbarHost(hostState = snackbarHostState) { data ->
        CustomSnackbar(data)
    }
}

@Composable
fun CustomSnackbar(data: SnackbarData) {
    Box(
        modifier = Modifier
            .graphicsLayer(
                scaleX = 0.9f,
                scaleY = 0.9f
            )
            .offset(
                y = (-50).dp
            )
            .background(
                color = MaterialTheme.Colors.backgroundMaroonLight,
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 0.5.dp,
                color = MaterialTheme.Colors.buttonOrange,
                shape = RoundedCornerShape(12.dp)
            )
            .alpha(0.8f)
    ) {
        Text(
            text = data.visuals.message,
            fontFamily = MaterialTheme.Typography.gantari.fontFamily,
            color = MaterialTheme.Colors.buttonOrange,
            modifier = Modifier.padding(12.dp).align(Alignment.Center)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CustomSnackbarPreview() {
    MaterialTheme {
        CustomSnackbar(
            data = object : SnackbarData {
                override val visuals: SnackbarVisuals = object : SnackbarVisuals {
                    override val message: String = "This is a custom snackbar message!"
                    override val actionLabel: String? = null
                    override val withDismissAction: Boolean = false
                    override val duration: SnackbarDuration = SnackbarDuration.Short
                }

                override fun dismiss() {}
                override fun performAction() {}
            }
        )
    }
}
