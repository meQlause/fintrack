package com.meqlause.fintrack.feature.transaction.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import com.meqlause.fintrack.feature.transaction.R

// FR-036, S-05: income and expense side by side, the net of the shown transactions below; amounts never truncate
@Composable
internal fun HistoryTotalsStrip(totals: HistoryTotals) {
    val colors = FintrackTheme.colors
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = FintrackTheme.spacing.medium)
                .clip(FintrackTheme.shapes.medium)
                .background(colors.surfaces.surface),
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            TotalCell(
                label = stringResource(R.string.history_total_income),
                value = totals.income,
                color = colors.semantics.income,
                modifier = Modifier.weight(1f),
            )
            Box(modifier = Modifier.width(FintrackTheme.sizes.hairline).fillMaxHeight().background(colors.surfaces.outline))
            TotalCell(
                label = stringResource(R.string.history_total_expense),
                value = totals.expense,
                color = colors.semantics.expense,
                modifier = Modifier.weight(1f),
            )
        }
        Box(modifier = Modifier.fillMaxWidth().height(FintrackTheme.sizes.hairline).background(colors.surfaces.outline))
        TotalCell(
            label = stringResource(R.string.history_total_net),
            value = totals.netTotal,
            color = colors.texts.onSurface,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun TotalCell(
    label: String,
    value: Money,
    color: Color,
    modifier: Modifier,
) {
    Column(modifier = modifier.padding(FintrackTheme.spacing.small)) {
        Text(
            text = label,
            style = FintrackTheme.typography.labelMedium,
            color = FintrackTheme.colors.texts.onSurfaceVariant,
        )
        Text(
            text = MoneyFormatter.format(value),
            style = FintrackTheme.typography.numberMedium,
            color = color,
            maxLines = 1,
            softWrap = false,
        )
    }
}
