package com.meqlause.fintrack.core.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalDensity

private const val LARGE_TEXT_SCALE = 1.5f

/** NFR-010: true when the font scale is large enough that a row must reflow instead of squeezing its text. */
@Composable
@ReadOnlyComposable
fun isLargeText(): Boolean = LocalDensity.current.fontScale >= LARGE_TEXT_SCALE
