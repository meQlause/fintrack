package com.meqlause.fintrack.feature.transaction.edit

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.FormRow
import com.meqlause.fintrack.core.designsystem.component.PickerDrawer
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import com.meqlause.fintrack.feature.transaction.R

private val TENOR_OPTIONS = listOf(3, 6, 12)

// FR-140, S-04: the tenor drawer; 3, 6 and 12 months, or "Lainnya" to type any tenor from 2 to 36 on the keypad
@Composable
internal fun EditInstalmentTenorDrawer(
    state: EditTransactionUiState,
    onEvent: (EditTransactionEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    PickerDrawer(
        title = stringResource(R.string.instalment_tenor_drawer_title),
        onClose = { onEvent(EditTransactionEvent.SheetDismissed) },
        modifier = modifier,
    ) {
        TENOR_OPTIONS.forEach { months ->
            FormRow(
                label = pluralStringResource(R.plurals.instalment_tenor_value, months, months),
                value = monthlyLabel(state, months),
                onClick = { onEvent(EditTransactionEvent.InstalmentTenorPicked(months)) },
            )
        }
        FormRow(
            label = stringResource(R.string.instalment_tenor_custom),
            value = "",
            onClick = { onEvent(EditTransactionEvent.InstalmentTenorPicked(null)) },
        )
    }
}

// FR-140: the monthly portion beside each tenor, once an amount is typed
@Composable
private fun monthlyLabel(
    state: EditTransactionUiState,
    months: Int,
): String {
    val amount = state.form.amount
    if (amount.isZero) return stringResource(R.string.instalment_amount_first)
    val perMonth = MoneyFormatter.format(Money(amount.rupiah / months))
    return stringResource(R.string.instalment_per_month, perMonth)
}
