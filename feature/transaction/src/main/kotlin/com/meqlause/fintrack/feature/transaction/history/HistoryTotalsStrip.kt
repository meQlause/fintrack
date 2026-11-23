package com.meqlause.fintrack.feature.transaction.history

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import com.meqlause.fintrack.feature.transaction.R
import com.meqlause.fintrack.core.designsystem.R as DesignR

// FR-036, S-05, DOC-08: Masuk, Keluar and Selisih in one card of three cells with compact amounts
@Composable
internal fun HistoryTotalsStrip(totals: HistoryTotals) {
    val colors = FintrackTheme.colors
    val shape = FintrackTheme.shapes.card
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = FintrackTheme.spacing.stack)
                .height(IntrinsicSize.Min)
                .clip(shape)
                .background(colors.surfaces.surface)
                .border(FintrackTheme.sizes.hairline, colors.surfaces.outline, shape),
    ) {
        val income =
            TotalLabel(stringResource(R.string.history_total_income), DesignR.drawable.ic_down, colors.semantics.income)
        TotalCell(label = income, value = MoneyFormatter.formatCompact(totals.income), modifier = Modifier.weight(1f))
        Divider()
        val expense =
            TotalLabel(stringResource(R.string.history_total_expense), DesignR.drawable.ic_up, colors.semantics.expense)
        TotalCell(label = expense, value = MoneyFormatter.formatCompact(totals.expense), modifier = Modifier.weight(1f))
        Divider()
        TotalCell(
            label = TotalLabel(stringResource(R.string.history_total_net)),
            value = MoneyFormatter.formatCompactSigned(totals.netTotal),
            modifier = Modifier.weight(1f),
            valueColor = if (totals.netTotal.rupiah < 0L) colors.semantics.expense else colors.semantics.income,
        )
    }
}

@Composable
private fun Divider() {
    Box(
        modifier =
            Modifier
                .width(FintrackTheme.sizes.hairline)
                .fillMaxHeight()
                .background(FintrackTheme.colors.surfaces.outline),
    )
}

@Composable
private fun TotalCell(
    label: TotalLabel,
    value: String,
    modifier: Modifier,
    valueColor: Color = FintrackTheme.colors.texts.onSurface,
) {
    Column(
        modifier =
            modifier.padding(horizontal = FintrackTheme.sizes.rowInset, vertical = FintrackTheme.spacing.medium),
    ) {
        TotalCaption(label = label)
        Text(
            text = value,
            modifier = Modifier.padding(top = FintrackTheme.spacing.xSmall),
            style = FintrackTheme.typography.numberSmall,
            color = valueColor,
            maxLines = 1,
            softWrap = false,
        )
    }
}

// S-05: the arrow, when the cell has one, and the caption
@Composable
private fun TotalCaption(label: TotalLabel) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.xSmall),
    ) {
        if (label.arrow != null) {
            Icon(
                painter = painterResource(label.arrow),
                contentDescription = null,
                tint = label.arrowColor,
                modifier = Modifier.size(FintrackTheme.sizes.tinyGlyph),
            )
        }
        Text(
            text = label.text,
            style = FintrackTheme.typography.caption,
            color = FintrackTheme.colors.texts.onSurfaceVariant,
        )
    }
}

// S-05: the caption of one totals cell with its optional arrow and the arrow's colour
private data class TotalLabel(
    val text: String,
    @param:DrawableRes val arrow: Int? = null,
    val arrowColor: Color = Color.Unspecified,
)
