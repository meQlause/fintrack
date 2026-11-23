package com.meqlause.fintrack.feature.transaction.history

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.feature.transaction.R

// FR-036, S-05: a horizontal drag past the threshold moves to the previous or next month
@Composable
internal fun Modifier.historyMonthSwipe(
    onPrevious: () -> Unit,
    onNext: () -> Unit,
): Modifier {
    val previousLabel = stringResource(R.string.history_month_previous)
    val nextLabel = stringResource(R.string.history_month_next)
    val swipeThreshold = FintrackTheme.sizes.monthSwipeThreshold
    return pointerInput(Unit) {
        var travel = 0f
        detectHorizontalDragGestures(
            onDragStart = { travel = 0f },
            onHorizontalDrag = { _, amount -> travel += amount },
            onDragEnd = {
                val threshold = swipeThreshold.toPx()
                when {
                    travel <= -threshold -> onNext()
                    travel >= threshold -> onPrevious()
                }
            },
        )
    }.semantics {
        // NFR-010: the month swipe has screen-reader alternatives
        customActions = monthActions(previousLabel to onPrevious, nextLabel to onNext)
    }
}

// NFR-010: one accessibility action per direction, each with its spoken label
private fun monthActions(
    previous: Pair<String, () -> Unit>,
    next: Pair<String, () -> Unit>,
): List<CustomAccessibilityAction> =
    listOf(previous, next).map { (label, action) ->
        CustomAccessibilityAction(label) {
            action()
            true
        }
    }
