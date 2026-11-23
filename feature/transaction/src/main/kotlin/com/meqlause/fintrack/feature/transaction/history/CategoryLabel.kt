package com.meqlause.fintrack.feature.transaction.history

import androidx.compose.ui.graphics.Color

// FR-060, S-08: the name of a ranked category and the colour of its dot
internal data class CategoryLabel(
    val name: String,
    val color: Color,
)
