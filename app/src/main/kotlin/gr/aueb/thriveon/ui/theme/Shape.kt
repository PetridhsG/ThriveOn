package gr.aueb.thriveon.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

internal val customShapes = Shapes(
    extraSmall = RoundedCornerShape(25.dp),     // Snackbar, Text field Shape
    small = RoundedCornerShape(4.dp),           // Chip shape
    medium = RoundedCornerShape(16.dp),         // Card, Small FAB shape
    large = RoundedCornerShape(16.dp),          // FAB, Extended FAB, Navigation Drawer shape
    extraLarge = RoundedCornerShape(32.dp),     // Bottom Sheet, Modal Bottom Sheet, Dialog, Large FAB shape
)
