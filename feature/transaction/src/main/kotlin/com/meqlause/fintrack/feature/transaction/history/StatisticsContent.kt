package com.meqlause.fintrack.feature.transaction.history

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.EmptyState
import com.meqlause.fintrack.core.designsystem.component.EmptyStateCopy
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.domain.statistics.PeriodStatistics
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.feature.transaction.R
import com.meqlause.fintrack.core.designsystem.R as DesignR

// FR-060..064, S-08: the statistics of the picked month, scrolled as one column below the month strip
@Composable
internal fun ColumnScope.StatisticsContent(
    state: HistoryUiState,
    onEvent: (HistoryEvent) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
    ) {
        HistorySegmented(
            options =
                listOf(
                    HistorySegmentOption(TransactionType.EXPENSE, stringResource(R.string.transaction_type_expense)),
                    HistorySegmentOption(TransactionType.INCOME, stringResource(R.string.transaction_type_income)),
                ),
            selected = state.statisticsType,
            onPick = { onEvent(HistoryEvent.StatisticsTypePicked(it)) },
            modifier = Modifier.padding(top = FintrackTheme.spacing.medium),
        )
        val statistics = state.statistics
        if (statistics == null || statistics.categories.isEmpty()) {
            StatisticsEmpty()
        } else {
            StatisticsSummary(
                statistics = statistics,
                appearances = state.categories.associate { it.categoryId to it.profile.appearance },
            )
            StatisticsCategoryList(statistics = statistics, state = state, onEvent = onEvent)
            StatisticsDailyBars(statistics = statistics)
        }
    }
}

@Composable
private fun StatisticsEmpty() {
    val copy = EmptyStateCopy(title = stringResource(R.string.history_empty_title), body = "")
    EmptyState(copy = copy, iconRes = DesignR.drawable.ic_history)
}
