package com.meqlause.fintrack.feature.transaction.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.domain.statistics.PeriodStatistics
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.feature.transaction.R
import com.meqlause.fintrack.core.designsystem.R as DesignR

private const val TOP_CATEGORY_COUNT = 3

// FR-060, FR-064, S-08: ranked categories, the top three first; a tap opens the list filtered to that category
@Composable
internal fun StatisticsCategoryList(
    statistics: PeriodStatistics,
    state: HistoryUiState,
    onEvent: (HistoryEvent) -> Unit,
) {
    val appearances = state.categories.associate { it.categoryId to it.profile.appearance }
    val names = state.categories.associate { it.categoryId to it.profile.name }
    val drillType =
        if (statistics.type == TransactionType.INCOME) HistoryTypeFilter.INCOME else HistoryTypeFilter.EXPENSE
    val isExpanded = state.isCategoriesExpanded
    val shown = if (isExpanded) statistics.categories else statistics.categories.take(TOP_CATEGORY_COUNT)
    Column(modifier = Modifier.fillMaxWidth().padding(top = FintrackTheme.spacing.medium)) {
        shown.forEachIndexed { index, change ->
            val open: (() -> Unit)? =
                change.categoryId?.let { id -> { onEvent(HistoryEvent.CategoryDrilled(id, drillType)) } }
            StatisticsCategoryRow(
                statistics = statistics,
                change = change,
                name = change.categoryId?.let { names[it] } ?: stringResource(R.string.history_stats_uncategorized),
                color = statisticsColorOf(change, appearances),
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
    HorizontalDivider(thickness = FintrackTheme.sizes.hairline, color = FintrackTheme.colors.surfaces.outline)
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(FintrackTheme.sizes.statMoreHeight)
                .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement =
            Arrangement.spacedBy(
                FintrackTheme.spacing.xSmall + FintrackTheme.spacing.xxSmall,
                Alignment.CenterHorizontally,
            ),
    ) {
        Text(text = label, style = FintrackTheme.typography.tab, color = FintrackTheme.colors.texts.onSurfaceVariant)
        Icon(
            painter = painterResource(if (isExpanded) DesignR.drawable.ic_chev_u else DesignR.drawable.ic_chev_d),
            contentDescription = null,
            tint = FintrackTheme.colors.texts.onSurfaceVariant,
            modifier = Modifier.size(FintrackTheme.sizes.shortcutGlyph),
        )
    }
}
