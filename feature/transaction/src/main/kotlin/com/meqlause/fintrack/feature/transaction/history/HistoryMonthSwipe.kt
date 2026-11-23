package com.meqlause.fintrack.feature.transaction.history

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val SwipeThreshold: Dp = 64.dp

// FR-036, S-05: a horizontal drag past the threshold moves to the previous or next month; the list never slides sideways
internal fun Modifier.historyMonthSwipe(
    onPrevious: () -> Unit,
    onNext: () -> Unit,
): Modifier =
    pointerInput(Unit) {
        var travel = 0f
        detectHorizontalDragGestures(
            onDragStart = { travel = 0f },
            onHorizontalDrag = { _, amount -> travel += amount },
            onDragEnd = {
                val threshold = SwipeThreshold.toPx()
                when {
                    travel <= -threshold -> onNext()
                    travel >= threshold -> onPrevious()
                }
            },
        )
    }
