package com.meqlause.fintrack.feature.transaction.edit

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
import com.meqlause.fintrack.core.designsystem.component.FintrackSwitch
import com.meqlause.fintrack.core.designsystem.component.FormRow
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import com.meqlause.fintrack.feature.transaction.R

// FR-140, S-04: the Cicilan switch with its tenor and admin fee rows, offered for an expense on a credit wallet
@Composable
internal fun EditInstalmentRows(
    state: EditTransactionUiState,
    onEvent: (EditTransactionEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val instalment = state.instalment
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.instalment_switch_label),
                style = FintrackTheme.typography.bodyMedium,
                color = FintrackTheme.colors.texts.onSurface,
                modifier = Modifier.weight(1f),
            )
            FintrackSwitch(
                checked = instalment.enabled,
                onCheckedChange = { enabled -> onEvent(EditTransactionEvent.InstalmentToggled(enabled)) },
            )
        }
        if (instalment.enabled) {
            FormRow(
                label = stringResource(R.string.instalment_tenor_label),
                value = tenorLabel(state),
                onClick = { onEvent(EditTransactionEvent.SheetOpened(EditSheet.TENOR)) },
            )
            FormRow(
                label = stringResource(R.string.instalment_fee_label),
                value = MoneyFormatter.format(instalment.fee),
                onClick = { onEvent(EditTransactionEvent.FieldFocused(KeypadField.INSTALMENT_FEE)) },
            )
            previewLabel(state)?.let { preview ->
                Text(
                    text = preview,
                    style = FintrackTheme.typography.labelMedium,
                    color = FintrackTheme.colors.texts.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = FintrackTheme.spacing.large),
                )
            }
        }
    }
}

// FR-140: the typed tenor, or the words "Lainnya" while it is outside 2..36
@Composable
private fun tenorLabel(state: EditTransactionUiState): String =
    state.instalment.tenor?.let { stringResource(R.string.instalment_tenor_value, it) }
        ?: stringResource(R.string.instalment_tenor_custom)

// FR-140: "Rp500.000 × 6 bulan", the monthly portion of the amount typed so far; BR-035 rounds down
@Composable
private fun previewLabel(state: EditTransactionUiState): String? {
    val tenor = state.instalment.tenor ?: return null
    val monthly = Money(state.form.amount.rupiah / tenor)
    return stringResource(R.string.instalment_preview, MoneyFormatter.format(monthly), tenor)
}
