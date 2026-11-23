package com.meqlause.fintrack.feature.transaction.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.domain.statistics.CategoryChange
import com.meqlause.fintrack.core.domain.statistics.PeriodStatistics
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import com.meqlause.fintrack.feature.transaction.R

private const val WHOLE_PERCENT = 100f

// FR-060, FR-062, S-08: one ranked category with its colour, name, share bar, amount and change against last period
@Composable
internal fun StatisticsCategoryRow(
    statistics: PeriodStatistics,
    change: CategoryChange,
    name: String,
    color: Color,
    onClick: (() -> Unit)?,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = FintrackTheme.spacing.small)
                .clickable(enabled = onClick != null) { onClick?.invoke() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.size(FintrackTheme.spacing.small).clip(FintrackTheme.shapes.full).background(color))
        Column(modifier = Modifier.weight(1f).padding(start = FintrackTheme.spacing.small)) {
            Text(
                text = name,
                style = FintrackTheme.typography.bodyLarge,
                color = FintrackTheme.colors.texts.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            ShareBar(fraction = statistics.shareOf(change) / WHOLE_PERCENT, color = color)
        }
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.padding(start = FintrackTheme.spacing.small),
        ) {
            Text(
                text = MoneyFormatter.format(change.amount),
                style = FintrackTheme.typography.labelLarge,
                color = FintrackTheme.colors.texts.onSurface,
            )
            ShareLine(statistics = statistics, change = change)
        }
    }
}

// S-08: the category's share of the period total as a thin bar in its colour
@Composable
private fun ShareBar(
    fraction: Float,
    color: Color,
) {
    Box(
        modifier =
            Modifier
                .padding(top = FintrackTheme.spacing.xxSmall)
                .fillMaxWidth()
                .height(FintrackTheme.spacing.xSmall)
                .clip(FintrackTheme.shapes.full)
                .background(FintrackTheme.colors.surfaces.outline),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth(fraction.coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .background(color),
        )
    }
}

// FR-062: the share in percent, then the change against the last period; "baru" when it had nothing before
@Composable
private fun ShareLine(
    statistics: PeriodStatistics,
    change: CategoryChange,
) {
    val colors = FintrackTheme.colors
    val percent = change.percentChange
    val changeText =
        when {
            statistics.previousTotal.isZero -> ""
            percent == null -> stringResource(R.string.history_stats_new_change)
            else -> percentText(percent)
        }
    val tint = percent?.let { changeColor(it, statistics.type, colors) } ?: colors.texts.onSurfaceVariant
    Row {
        Text(
            text = "${statistics.shareOf(change)}%",
            style = FintrackTheme.typography.labelMedium,
            color = colors.texts.onSurfaceVariant,
        )
        if (changeText.isNotEmpty()) {
            Text(
                text = changeText,
                style = FintrackTheme.typography.labelMedium,
                color = tint,
                modifier = Modifier.padding(start = FintrackTheme.spacing.xSmall),
            )
        }
    }
}
