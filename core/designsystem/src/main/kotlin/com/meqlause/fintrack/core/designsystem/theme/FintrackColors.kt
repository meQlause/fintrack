package com.meqlause.fintrack.core.designsystem.theme

import androidx.compose.runtime.Immutable

// DOC-08: the 16 colour tokens, grouped by role to stay within the 7-parameter constructor limit (05 section 2)
@Immutable
data class FintrackColors(
    val surfaces: SurfaceColors,
    val texts: TextColors,
    val brands: BrandColors,
    val semantics: SemanticColors,
)
