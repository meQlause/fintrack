package com.meqlause.fintrack.feature.transaction.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.domain.statistics.PeriodStatistics
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.feature.transaction.R

private const val TOP_CATEGORY_COUNT = 3

// FR-060, FR-064, S-08: ranked categories, the top three first; a tap opens the list filtered to that category
@Composable
internal fun StatisticsCategoryList(
    statistics: PeriodStatistics,
    state: HistoryUiState,
    onEvent: (HistoryEvent) -> Unit,
) {
    val palette = statisticsPalette(FintrackTheme.colors)
    val names = state.categories.associate { it.categoryId to it.profile.name }
    val drillType =
        if (statistics.type == TransactionType.INCOME) HistoryTypeFilter.INCOME else HistoryTypeFilter.EXPENSE
    val isExpanded = state.isCategoriesExpanded
    val shown = if (isExpanded) statistics.categories else statistics.categories.take(TOP_CATEGORY_COUNT)
    Column(modifier = Modifier.fillMaxWidth().padding(top = FintrackTheme.spacing.xLarge)) {
        Text(
            text = stringResource(R.string.history_stats_categories_title),
            style = FintrackTheme.typography.labelLarge,
            color = FintrackTheme.colors.texts.onSurface,
        )
        shown.forEachIndexed { index, change ->
            val open: (() -> Unit)? =
                change.categoryId?.let { id -> { onEvent(HistoryEvent.CategoryDrilled(id, drillType)) } }
            StatisticsCategoryRow(
                statistics = statistics,
                change = change,
                name = change.categoryId?.let { names[it] } ?: stringResource(R.string.history_stats_uncategorized),
                color = palette[index % palette.size],
                onClick = open,
            )
        }
        if (statistics.categories.size > TOP_CATEGORY_COUNT) {
            StatisticsExpandToggle(
                isExpanded = isExpanded,
                count = statistics.categories.size,
                onClick = { onEvent(HistoryEvent.CategoriesToggled) },
            )
        }
    }
}

@Composable
private fun StatisticsExpandToggle(
    isExpanded: Boolean,
    count: Int,
    onClick: () -> Unit,
) {
    val label =
        if (isExpanded) {
            stringResource(R.string.history_stats_collapse)
        } else {
            stringResource(R.string.history_stats_show_all, count)
        }
    Text(
        text = label,
        style = FintrackTheme.typography.labelLarge,
        color = FintrackTheme.colors.brands.primary,
        modifier = Modifier.padding(top = FintrackTheme.spacing.small).clickable(onClick = onClick),
    )
}
