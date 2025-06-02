package gr.aueb.thriveon.ui.common.components.spacers

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import gr.aueb.thriveon.ui.common.constants.ConstantsSpacing.SPACING_LARGE
import gr.aueb.thriveon.ui.common.constants.ConstantsSpacing.SPACING_MEDIUM
import gr.aueb.thriveon.ui.common.constants.ConstantsSpacing.SPACING_SMALL
import gr.aueb.thriveon.ui.common.constants.ConstantsSpacing.SPACING_TINY

/**
 * This object contains composable functions that add horizontal spacing.
 *
 * Below are the predefined spacings:
 * * [Tiny]     - adds [SPACING_TINY]   of vertical spacing
 * * [Small]    - adds [SPACING_SMALL]  of vertical spacing
 * * [Medium]   - adds [SPACING_MEDIUM] of vertical spacing
 * * [Large]    - adds [SPACING_LARGE]  of vertical spacing
 *
 * If one of the above spacings doesn't fit your needs, you can use [Custom] that accepts a [Float]
 * as the vertical spacing to be added.
 */
object VSpacer {
    /**
     * Composable function that adds [SPACING_TINY] of horizontal spacing.
     */
    @Composable
    fun Tiny() = Spacer(modifier = Modifier.height(height = SPACING_TINY.dp))

    /**
     * Composable function that adds [SPACING_SMALL] of horizontal spacing.
     */
    @Composable
    fun Small() = Spacer(modifier = Modifier.height(height = SPACING_SMALL.dp))

    /**
     * Composable function that adds [SPACING_MEDIUM] of horizontal spacing.
     */
    @Composable
    fun Medium() = Spacer(modifier = Modifier.height(height = SPACING_MEDIUM.dp))

    /**
     * Composable function that adds [SPACING_LARGE] of horizontal spacing.
     */
    @Composable
    fun Large() = Spacer(modifier = Modifier.height(height = SPACING_LARGE.dp))

    /**
     * Composable function that adds [height] of horizontal spacing.
     */
    @Composable
    fun Custom(height: Float) = Spacer(modifier = Modifier.height(height = height.dp))
}
