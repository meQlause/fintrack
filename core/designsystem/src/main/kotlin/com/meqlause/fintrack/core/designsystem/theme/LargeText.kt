package com.meqlause.fintrack.core.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalDensity

/** NFR-010: tells whether the font scale is large enough that a row must reflow instead of squeezing its text. */
object LargeText {
    private const val SCALE = 1.5f

    /** NFR-010: true from a font scale of 1.5 up. */
    val isOn: Boolean
        @Composable
        @ReadOnlyComposable
        get() = LocalDensity.current.fontScale >= SCALE
}
