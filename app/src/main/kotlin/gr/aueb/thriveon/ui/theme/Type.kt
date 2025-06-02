package gr.aueb.thriveon.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import gr.aueb.thriveon.R

val Gantari = FontFamily(Font(R.font.gantari, FontWeight.Normal))
val PragatiNarrow = FontFamily(Font(R.font.pragati_narrow, FontWeight.Normal))
val Phudu = FontFamily(Font(R.font.phudu, FontWeight.Normal))
val IstokWeb = FontFamily(Font(R.font.istok_web, FontWeight.Normal))

@JvmInline
value class AppFontFamily(val fontFamily: FontFamily)

@Immutable
data class ThriveOnTypography(
    val gantari: AppFontFamily,
    val pragatiNarrow: AppFontFamily,
    val phudu: AppFontFamily,
    val istokWeb: AppFontFamily
)

internal fun customTypography() = ThriveOnTypography(
    gantari = AppFontFamily(Gantari),
    pragatiNarrow = AppFontFamily(PragatiNarrow),
    phudu = AppFontFamily(Phudu),
    istokWeb = AppFontFamily(IstokWeb)
)

internal val LocalCustomTypography = staticCompositionLocalOf { customTypography() }

val MaterialTheme.Typography: ThriveOnTypography
    @Composable
    @ReadOnlyComposable
    get() = LocalCustomTypography.current
