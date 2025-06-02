package gr.aueb.thriveon.ui.common.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import gr.aueb.thriveon.ui.theme.Colors

@Composable
fun ThriveOnCircularProgressIndicator() {
    CircularProgressIndicator(
        color = MaterialTheme.Colors.textOrange,
        strokeWidth = 6.dp,
        modifier = Modifier.size(64.dp)
    )
}
