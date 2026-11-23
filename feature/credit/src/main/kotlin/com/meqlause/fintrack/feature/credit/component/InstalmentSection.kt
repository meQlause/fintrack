package com.meqlause.fintrack.feature.credit.component

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
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.FormRow
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import com.meqlause.fintrack.feature.credit.R
import com.meqlause.fintrack.feature.credit.instalment.InstalmentRow

// FR-142, S-14: the active instalments of this card, each opening S-16; "Semua cicilan" opens the full list
@Composable
internal fun InstalmentSection(
    instalments: List<InstalmentRow>,
    onOpenInstalment: (Long) -> Unit,
    onOpenAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.instalment_section_title),
                style = FintrackTheme.typography.labelLarge,
                color = FintrackTheme.colors.texts.onSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = stringResource(R.string.instalment_section_all),
                style = FintrackTheme.typography.bodyMedium,
                color = FintrackTheme.colors.brands.primary,
                modifier = Modifier.clickable(onClick = onOpenAll).padding(vertical = FintrackTheme.spacing.small),
            )
        }
        if (instalments.isEmpty()) {
            Text(
                text = stringResource(R.string.instalment_section_empty),
                style = FintrackTheme.typography.bodyMedium,
                color = FintrackTheme.colors.texts.onSurfaceVariant,
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small)) {
            instalments.forEach { row ->
                FormRow(
                    label = row.title,
                    value =
                        "${stringResource(R.string.instalment_progress, row.billed, row.tenor)} · " +
                            MoneyFormatter.format(row.monthly),
                    onClick = { onOpenInstalment(row.planId) },
                )
            }
        }
    }
}
