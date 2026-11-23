package com.meqlause.fintrack.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

// DOC-08: the 16 colour tokens, grouped by role to stay within the 7-parameter constructor limit (05 section 2)
@Immutable
data class FintrackColors(
    val surfaces: SurfaceColors,
    val texts: TextColors,
    val brands: BrandColors,
    val semantics: SemanticColors,
) {
    // DOC-08: the colour a category key shows on the current surface, for charts and swatches outside IconCircle
    fun categoryColor(colorKey: String): Color = CategoryPalette.colorFor(colorKey, surfaces.background)
}
