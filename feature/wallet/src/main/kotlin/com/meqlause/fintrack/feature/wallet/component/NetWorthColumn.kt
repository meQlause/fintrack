package com.meqlause.fintrack.feature.wallet.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.meqlause.fintrack.core.designsystem.icon.categoryColorOf
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.ui.format.MoneyFormatter

// BR-053: one side of the card, Aset or Utang, with its total (17 px bold) and a dot row per part
@Composable
internal fun NetWorthColumn(
    title: String,
    total: String,
    rows: List<NetWorthPart>,
    isDebt: Boolean,
    modifier: Modifier = Modifier,
) {
    val semantics = FintrackTheme.colors.semantics
    Column(modifier = modifier) {
        Text(
            text = title,
            style = FintrackScreenText.label(),
            color = FintrackTheme.colors.texts.onSurfaceVariant,
        )
        Text(
            text = total,
            style = FintrackScreenText.total(),
            color = if (isDebt) semantics.expense else semantics.income,
            modifier = Modifier.padding(top = FintrackTheme.spacing.xxSmall),
        )
        rows.forEach { part -> NetWorthLegendRow(part = part) }
    }
}

// BR-053: coloured dot, muted label and the compact amount without the Rp prefix
@Composable
private fun NetWorthLegendRow(part: NetWorthPart) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = FintrackScreenSizes.legendRowTop),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FintrackScreenSizes.legendGap),
    ) {
        Row(
            modifier =
                Modifier
                    .size(FintrackScreenSizes.legendDot)
                    .clip(CircleShape)
                    .background(categoryColorOf(part.colorKey)),
        ) {}
        Text(
            text = stringResource(part.labelRes),
            style = FintrackScreenText.legend(weight = 500),
            color = FintrackTheme.colors.texts.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = MoneyFormatter.formatCompact(Money(part.amount)),
            style = FintrackScreenText.legendNumber(),
            color = FintrackTheme.colors.texts.onSurface,
            maxLines = 1,
        )
    }
}
