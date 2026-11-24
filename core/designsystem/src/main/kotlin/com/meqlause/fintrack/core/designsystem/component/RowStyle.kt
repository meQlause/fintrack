package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.foundation.layout.PaddingValues
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes

// DOC-08, NFR-010: the divider, the padding, the large text reflow and the optional taps of a screen row
data class RowStyle(
    val showDivider: Boolean = false,
    val stackAtLargeText: Boolean = false,
    val onClick: (() -> Unit)? = null,
    val onLongClick: (() -> Unit)? = null,
    val contentPadding: PaddingValues =
        PaddingValues(
            horizontal = FintrackScreenSizes.rowPaddingHorizontal,
            vertical = FintrackScreenSizes.rowPaddingVertical,
        ),
)
