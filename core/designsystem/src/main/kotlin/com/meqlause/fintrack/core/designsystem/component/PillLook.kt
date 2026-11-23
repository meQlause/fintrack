package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes

// DOC-08: the optional dot, the label colour, the selected state and the height of a pill
data class PillLook(
    val dotColor: Color? = null,
    val contentColor: Color = Color.Unspecified,
    val selected: Boolean = false,
    val height: Dp = FintrackScreenSizes.pillTall,
)
