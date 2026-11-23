package com.meqlause.fintrack.feature.transaction.history

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.meqlause.fintrack.feature.transaction.R

private val SwipeThreshold: Dp = 64.dp

// FR-036, S-05: a horizontal drag past the threshold moves to the previous or next month; the list never slides sideways
@Composable
internal fun Modifier.historyMonthSwipe(
    onPrevious: () -> Unit,
    onNext: () -> Unit,
): Modifier {
    val previousLabel = stringResource(R.string.history_month_previous)
    val nextLabel = stringResource(R.string.history_month_next)
    return pointerInput(Unit) {
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
    }.semantics {
        // NFR-010: the month swipe has screen-reader alternatives
        customActions =
            listOf(
                CustomAccessibilityAction(previousLabel) {
                    onPrevious()
                    true
                },
                CustomAccessibilityAction(nextLabel) {
                    onNext()
                    true
                },
            )
    }
}
