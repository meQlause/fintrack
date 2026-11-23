package com.meqlause.fintrack.feature.transaction.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.domain.statistics.PeriodStatistics
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.ui.format.DateFormatter
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import com.meqlause.fintrack.feature.transaction.R
import java.time.YearMonth

// FR-060, S-08: the donut of the category shares with Total, the amount and the change against last month inside
@Composable
internal fun StatisticsSummary(
    statistics: PeriodStatistics,
    month: YearMonth,
    appearances: Map<CategoryId, Appearance>,
) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        StatisticsDonut(statistics = statistics, appearances = appearances)
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(
                text = stringResource(R.string.history_stats_total),
                style = FintrackTheme.typography.caption,
                color = FintrackTheme.colors.texts.onSurfaceVariant,
            )
            Text(
                text = MoneyFormatter.format(statistics.total),
                modifier = Modifier.padding(top = FintrackTheme.spacing.xxSmall),
                style = FintrackTheme.typography.numberTotal,
                color = FintrackTheme.colors.texts.onSurface,
            )
            StatisticsTotalChange(statistics = statistics, month = month)
        }
    }
}

// FR-062: the total against the previous month, e.g. "▲ 60% · Okt"; nothing when it had no money of this kind
@Composable
private fun StatisticsTotalChange(
    statistics: PeriodStatistics,
    month: YearMonth,
) {
    val percent = statistics.totalPercentChange ?: return
    val previous = DateFormatter.monthShort(month.minusMonths(1L).atDay(1))
    Text(
        text = "${percentText(percent)} · $previous",
        modifier = Modifier.padding(top = FintrackTheme.spacing.xSmall),
        style = FintrackTheme.typography.caption,
        color = changeColor(percent, statistics.type, FintrackTheme.colors),
    )
}
