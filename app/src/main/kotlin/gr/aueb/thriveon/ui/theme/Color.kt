package gr.aueb.thriveon.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class ThriveOnColors(
    val backgroundMaroonDark: Color,        // Maroon background dark (for the app background,cards background on light background etc.)
    val backgroundMaroonLight: Color,       // Maroon background light (cards background on dark background etc.)
    val backgroundGray: Color,              // Gray background for some components
    val backgroundDark: Color,              // Dark background for emoji bar
    val textWhite: Color,                   // Text color
    val textBlack: Color,                   // Text color
    val textOrange: Color,                  // Text color
    val textLightBlue: Color,               // Text color
    val buttonOrange: Color,                // Orange background color for the buttons
    val componentOrangeOutline: Color,      // Outline color for some components
    val taskCardOrange: Color,              // Task orange background
    val taskCardGreen: Color,               // Task green background
    val completeGreen: Color,               // Complete green
    val uncompletedGray: Color,             // Gray for uncompleted tasks
    val dividerGray: Color,                 // Gray for horizontal dividers
    val navigationIconGray: Color,          // Gray for navigation icons
    val signOutRed: Color                   // Red for Signing Out
)

internal fun customColors() = ThriveOnColors(
    backgroundMaroonDark = Color(0xFF2C0A0A),
    backgroundMaroonLight = Color(0xFF5A1A1A),
    backgroundGray = Color(0xFF2E2E2E),
    backgroundDark = Color(0xCC000000),
    textWhite = Color(0xFFFFFFFF),
    textBlack = Color(0xFF1A1A1A),
    textOrange = Color(0xFFFFA726),
    textLightBlue = Color(0xFF00CFFF),
    buttonOrange = Color(0xFFFF9800),
    componentOrangeOutline = Color(0xFFFFB74D),
    taskCardOrange = Color(0xFFDD8A16),
    taskCardGreen = Color(0xFF2C712C),
    completeGreen = Color(0xFF3DFD3D),
    uncompletedGray = Color(0xFF5A4C3A),
    dividerGray = Color(0xFF6E6E6E),
    navigationIconGray = Color(0xFFFAFAFA),
    signOutRed = Color(0xFFFF3B30)
)

internal val LocalCustomColors = staticCompositionLocalOf { customColors() }

val MaterialTheme.Colors: ThriveOnColors
    @Composable
    @ReadOnlyComposable
    get() = LocalCustomColors.current
