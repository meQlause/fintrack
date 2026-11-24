package com.meqlause.fintrack.feature.transaction.history

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.EmptyAction
import com.meqlause.fintrack.core.designsystem.component.EmptyIllustrated
import com.meqlause.fintrack.core.designsystem.component.FintrackCard
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.feature.transaction.R

// FR-060..064, S-08: the statistics of the picked month, scrolled as one column below the month strip
@Composable
internal fun ColumnScope.StatisticsContent(
    state: HistoryUiState,
    onEvent: (HistoryEvent) -> Unit,
    onAdd: () -> Unit,
) {
    Column(modifier = Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState())) {
        StatisticsTypeSwitch(state = state, onEvent = onEvent)
        val statistics = state.statistics
        if (statistics == null || statistics.categories.isEmpty()) {
            StatisticsEmpty(onAdd = onAdd)
        } else {
            FintrackCard(
                modifier = Modifier.fillMaxWidth().padding(top = FintrackTheme.spacing.stack),
                contentPadding =
                    PaddingValues(
                        start = FintrackTheme.spacing.large,
                        top = FintrackTheme.spacing.section,
                        end = FintrackTheme.spacing.large,
                    ),
            ) {
                StatisticsSummary(
                    statistics = statistics,
                    month = state.month,
                    appearances = state.categories.associate { it.categoryId to it.profile.appearance },
                )
                StatisticsCategoryList(statistics = statistics, state = state, onEvent = onEvent)
            }
            StatisticsDailyBars(statistics = statistics, todayIndex = state.todayIndex)
            Spacer(modifier = Modifier.height(FintrackScreenSizes.screenBottom))
        }
    }
}

// FR-060: Pengeluaran or Pemasukan, the type the statistics total
@Composable
private fun StatisticsTypeSwitch(
    state: HistoryUiState,
    onEvent: (HistoryEvent) -> Unit,
) {
    val colors = FintrackTheme.colors
    HistorySegmented(
        options =
            listOf(
                HistorySegmentOption(
                    TransactionType.EXPENSE,
                    stringResource(R.string.history_type_expense),
                    activeColor = colors.semantics.expense,
                ),
                HistorySegmentOption(
                    TransactionType.INCOME,
                    stringResource(R.string.history_type_income),
                    activeColor = colors.semantics.income,
                ),
            ),
        choice = SegmentChoice(state.statisticsType) { onEvent(HistoryEvent.StatisticsTypePicked(it)) },
        modifier = Modifier.padding(top = FintrackTheme.spacing.small),
        trackHeight = FintrackTheme.sizes.statSegmentHeight,
    )
}

@Composable
private fun StatisticsEmpty(onAdd: () -> Unit) {
    EmptyIllustrated(
        title = stringResource(R.string.history_empty_title),
        action = EmptyAction(label = stringResource(R.string.history_empty_action), onClick = onAdd),
    )
}
