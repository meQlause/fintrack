package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.runtime.Immutable

// DOC-09: one bottom-bar destination: its accessible label and its stroke icon resource
@Immutable
data class FintrackBarItem(
    val label: String,
    val iconRes: Int,
)
