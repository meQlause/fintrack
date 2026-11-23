package com.meqlause.fintrack.core.designsystem.icon

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import com.meqlause.fintrack.core.designsystem.theme.CategoryPalette
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// DOC-08: the colour a category key shows on the current surface, for charts and swatches outside IconCircle
@Composable
@ReadOnlyComposable
fun categoryColorOf(colorKey: String): Color = CategoryPalette.colorFor(colorKey, FintrackTheme.colors.surfaces.background)
