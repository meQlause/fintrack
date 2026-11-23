package com.meqlause.fintrack.feature.transaction.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import com.meqlause.fintrack.feature.transaction.R

// FR-036, S-05: income, expense and net of the shown transactions; net takes the colour of its sign
@Composable
internal fun HistoryTotalsStrip(totals: HistoryTotals) {
    val colors = FintrackTheme.colors
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = FintrackTheme.spacing.medium)
                .clip(FintrackTheme.shapes.medium)
                .background(colors.surfaces.surface),
    ) {
        TotalCell(
            label = stringResource(R.string.history_total_income),
            value = totals.income,
            color = colors.semantics.income,
            modifier = Modifier.weight(1f),
        )
        TotalCell(
            label = stringResource(R.string.history_total_expense),
            value = totals.expense,
            color = colors.semantics.expense,
            modifier = Modifier.weight(1f),
        )
        TotalCell(
            label = stringResource(R.string.history_total_net),
            value = totals.netTotal,
            color = colors.texts.onSurface,
            modifier = Modifier.weight(1f),
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
    Column(modifier = modifier.padding(FintrackTheme.spacing.medium)) {
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
            overflow = TextOverflow.Ellipsis,
        )
    }
}
