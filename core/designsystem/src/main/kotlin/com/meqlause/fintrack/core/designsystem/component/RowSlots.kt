package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.runtime.Composable

// DOC-08: the leading mark and the trailing content around the text column of a screen row
data class RowSlots(
    val leading: @Composable () -> Unit,
    val trailing: @Composable () -> Unit = {},
)
