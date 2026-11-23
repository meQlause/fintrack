package com.meqlause.fintrack.feature.transaction.history

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.domain.statistics.PeriodStatistics
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import com.meqlause.fintrack.feature.transaction.R

// FR-060, S-08: the donut of the category shares with the total in its centre and the change against the last period
@Composable
internal fun StatisticsSummary(
    statistics: PeriodStatistics,
    appearances: Map<CategoryId, Appearance>,
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = FintrackTheme.spacing.large),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(contentAlignment = Alignment.Center) {
            StatisticsDonut(statistics = statistics, appearances = appearances)
            StatisticsTotal(statistics = statistics)
        }
        StatisticsTotalChange(statistics = statistics)
    }
}

@Composable
private fun StatisticsTotal(statistics: PeriodStatistics) {
    val typeRes =
        if (statistics.type == TransactionType.INCOME) {
            R.string.transaction_type_income
        } else {
            R.string.transaction_type_expense
        }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = MoneyFormatter.format(statistics.total),
            style = FintrackTheme.typography.numberMedium,
            color = FintrackTheme.colors.texts.onSurface,
        )
        Text(
            text = stringResource(typeRes),
            style = FintrackTheme.typography.labelMedium,
            color = FintrackTheme.colors.texts.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

// FR-062: the total against the previous period; nothing is shown when the previous period had no money of this kind
@Composable
private fun StatisticsTotalChange(statistics: PeriodStatistics) {
    val percent = statistics.totalPercentChange ?: return
    val colors = FintrackTheme.colors
    Text(
        text = "${percentText(percent)} ${stringResource(R.string.history_stats_versus_previous)}",
        style = FintrackTheme.typography.labelMedium,
        color = changeColor(percent, statistics.type, colors),
        modifier = Modifier.padding(top = FintrackTheme.spacing.small),
    )
}
