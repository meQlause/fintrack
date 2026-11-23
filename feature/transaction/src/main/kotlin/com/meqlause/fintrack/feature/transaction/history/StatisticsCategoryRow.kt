package com.meqlause.fintrack.feature.transaction.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.domain.statistics.CategoryChange
import com.meqlause.fintrack.core.domain.statistics.PeriodStatistics
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import com.meqlause.fintrack.feature.transaction.R

// FR-060, FR-062, S-08, DOC-08: one ranked category: dot, name, change, share and amount on a divided line
@Composable
internal fun StatisticsCategoryRow(
    statistics: PeriodStatistics,
    change: CategoryChange,
    name: String,
    color: Color,
    onClick: (() -> Unit)?,
) {
    val colors = FintrackTheme.colors
    HorizontalDivider(thickness = FintrackTheme.sizes.hairline, color = colors.surfaces.outline)
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(enabled = onClick != null) { onClick?.invoke() }
                .padding(
                    horizontal = FintrackTheme.spacing.xxSmall,
                    vertical =
                        FintrackTheme.spacing.small + FintrackTheme.spacing.xxSmall,
                ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.medium),
    ) {
        Box(modifier = Modifier.size(FintrackTheme.sizes.smallDot).clip(FintrackTheme.shapes.full).background(color))
        Text(
            text = name,
            modifier = Modifier.weight(1f),
            style = FintrackTheme.typography.tabWide.copy(fontWeight = FontWeight.Medium),
            color = colors.texts.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        ChangeText(statistics = statistics, change = change)
        Text(
            text = "${statistics.shareOf(change)}%",
            modifier = Modifier.width(FintrackTheme.sizes.statPercentWidth),
            style = FintrackTheme.typography.caption.copy(fontWeight = FontWeight.Medium),
            color = colors.texts.onSurfaceVariant,
            textAlign = TextAlign.End,
        )
        Text(
            text = MoneyFormatter.format(change.amount),
            modifier = Modifier.width(FintrackTheme.sizes.statAmountWidth),
            style = FintrackTheme.typography.numberCompact.copy(fontWeight = FontWeight.SemiBold),
            color = colors.texts.onSurface,
            textAlign = TextAlign.End,
            maxLines = 1,
        )
    }
}

// FR-062: the change against the last period; "baru" when it had nothing before, empty without a last period
@Composable
private fun ChangeText(
    statistics: PeriodStatistics,
    change: CategoryChange,
) {
    val colors = FintrackTheme.colors
    val percent = change.percentChange
    val text =
        when {
            statistics.previousTotal.isZero -> ""
            percent == null -> stringResource(R.string.history_stats_new_change)
            else -> percentText(percent)
        }
    val tint = percent?.let { changeColor(it, statistics.type, colors) } ?: colors.texts.onSurfaceVariant
    Text(text = text, style = FintrackTheme.typography.micro, color = tint)
}
