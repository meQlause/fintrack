package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.runtime.Immutable

// DOC-09: events the bottom bar reports upward; the bar keeps no selection state of its own
@Immutable
data class FintrackBottomBarCallbacks(
    val onSelect: (Int) -> Unit,
    val onAddClick: () -> Unit,
)
