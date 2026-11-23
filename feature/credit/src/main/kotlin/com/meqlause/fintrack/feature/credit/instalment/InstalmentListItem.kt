package com.meqlause.fintrack.feature.credit.instalment

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.ProgressBar
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import com.meqlause.fintrack.feature.credit.R

// FR-142, S-16: one plan card with its progress "k/n", what remains and the next statement that bills
@Composable
internal fun InstalmentListItem(
    instalmentRow: InstalmentRow,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FintrackTheme.colors
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(FintrackTheme.shapes.medium)
                .background(colors.surfaces.surface)
                .clickable(onClick = onClick)
                .padding(FintrackTheme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
    ) {
        Text(text = instalmentRow.title, style = FintrackTheme.typography.titleMedium, color = colors.texts.onSurface)
        Text(
            text = "${instalmentRow.cardName} · ${stringResource(
                R.string.instalment_monthly,
                MoneyFormatter.format(instalmentRow.monthly),
            )}",
            style = FintrackTheme.typography.bodyMedium,
            color = colors.texts.onSurfaceVariant,
        )
        ProgressBar(fraction = instalmentRow.billed.toFloat() / instalmentRow.tenor, color = colors.brands.primary)
        Row(
            horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.instalment_progress, instalmentRow.billed, instalmentRow.tenor),
                style = FintrackTheme.typography.labelLarge,
                color = colors.texts.onSurface,
            )
            Text(
                text = remainingLabel(instalmentRow),
                style = FintrackTheme.typography.bodyMedium,
                color = colors.texts.onSurfaceVariant,
            )
        }
        instalmentRow.nextMonth?.let { month ->
            Text(
                text = stringResource(R.string.instalment_next_bill, month.label()),
                style = FintrackTheme.typography.labelMedium,
                color = colors.texts.onSurfaceVariant,
            )
        }
    }
}

// FR-142: remaining principal, or the word "Lunas" once every portion is billed
@Composable
private fun remainingLabel(instalmentRow: InstalmentRow): String =
    if (instalmentRow.isFinished) {
        stringResource(R.string.instalment_finished)
    } else {
        stringResource(R.string.instalment_remaining, MoneyFormatter.format(instalmentRow.remaining))
    }
