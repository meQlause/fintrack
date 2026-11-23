package com.meqlause.fintrack.feature.transaction.history

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.EmptyState
import com.meqlause.fintrack.core.designsystem.component.EmptyStateCopy
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import com.meqlause.fintrack.core.ui.transaction.TransactionRow
import com.meqlause.fintrack.core.ui.transaction.TransactionRowCallbacks
import com.meqlause.fintrack.feature.transaction.R
import com.meqlause.fintrack.core.designsystem.R as DesignR

// FR-036, FR-037, S-05: the list of the picked month; a horizontal swipe changes the month
@Composable
internal fun ColumnScope.HistoryMonthContent(
    state: HistoryUiState,
    onEvent: (HistoryEvent) -> Unit,
    onOpenTransaction: (Long) -> Unit,
) {
    val motion = FintrackTheme.motion.medium
    AnimatedContent(
        targetState = state.month,
        modifier =
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .historyMonthSwipe(
                    onPrevious = { onEvent(HistoryEvent.PreviousMonth) },
                    onNext = { onEvent(HistoryEvent.NextMonth) },
                ),
        transitionSpec = { focusPull(motion.durationMillis, motion.easing) },
        label = "history-month-focus",
    ) { _ ->
        HistoryDayList(state = state, onOpenTransaction = onOpenTransaction)
    }
}

// S-05: the new month comes into focus by fading and growing from slightly smaller, with no sideways motion
private fun focusPull(
    durationMillis: Int,
    easing: Easing,
): ContentTransform {
    val enter =
        fadeIn(tween<Float>(durationMillis, easing = easing)) +
            scaleIn(tween<Float>(durationMillis, easing = easing), initialScale = FOCUS_SCALE)
    val exit =
        fadeOut(tween<Float>(durationMillis, easing = easing)) +
            scaleOut(tween<Float>(durationMillis, easing = easing), targetScale = 1f / FOCUS_SCALE)
    return enter togetherWith exit
}

private const val FOCUS_SCALE = 0.96f

// NFR-005: lazy items of one kind share a content type so the list reuses their compositions while scrolling
private const val DAY_HEADER_TYPE = "day-header"
private const val TRANSACTION_ROW_TYPE = "transaction-row"

@Composable
private fun HistoryDayList(
    state: HistoryUiState,
    onOpenTransaction: (Long) -> Unit,
) {
    if (!state.isLoaded) return
    if (state.groups.isEmpty()) {
        HistoryEmptyState(isFiltered = state.filter.isActive)
        return
    }
    LazyColumn(contentPadding = PaddingValues(bottom = FintrackTheme.sizes.navBarHeight)) {
        state.groups.forEach { group ->
            item(key = "day-${group.label}", contentType = DAY_HEADER_TYPE) { DayHeader(group = group) }
            items(group.rows, key = { it.transactionId }, contentType = { TRANSACTION_ROW_TYPE }) { row ->
                TransactionRow(
                    params = row.toParams(),
                    callbacks = TransactionRowCallbacks(onClick = { onOpenTransaction(row.transactionId) }),
                )
            }
        }
    }
}

@Composable
private fun DayHeader(group: HistoryDayGroup) {
    Row(
        modifier =
            Modifier.fillMaxWidth().padding(
                top = FintrackTheme.spacing.large,
                bottom = FintrackTheme.spacing.small,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = group.label,
            style = FintrackTheme.typography.labelLarge,
            color = FintrackTheme.colors.texts.onSurface,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = MoneyFormatter.format(group.net),
            style = FintrackTheme.typography.labelMedium,
            color = FintrackTheme.colors.texts.onSurfaceVariant,
        )
    }
}

@Composable
private fun HistoryEmptyState(isFiltered: Boolean) {
    val copy =
        if (isFiltered) {
            EmptyStateCopy(title = stringResource(R.string.history_search_empty_title), body = "")
        } else {
            EmptyStateCopy(title = stringResource(R.string.history_empty_title), body = "")
        }
    EmptyState(copy = copy, iconRes = DesignR.drawable.ic_history)
}
