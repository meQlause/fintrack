package com.meqlause.fintrack.feature.transaction.edit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.ui.format.MoneyFormatter

// FR-030, FR-033, S-04: the amount hero in the type colour, its validation message and the date line
@Composable
internal fun EditTransactionAmountBlock(
    state: EditTransactionUiState,
    onEvent: (EditTransactionEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = MoneyFormatter.format(state.form.amount),
            style = FintrackTheme.typography.numberHero,
            color = amountColor(state.form.type),
            modifier = Modifier.clickable { onEvent(EditTransactionEvent.FieldFocused(KeypadField.AMOUNT)) },
        )
        state.errors.minByOrNull { it.ordinal }?.let { error ->
            Text(
                text = stringResource(error.messageRes()),
                style = FintrackTheme.typography.labelMedium,
                color = FintrackTheme.colors.semantics.error,
            )
        }
        Text(
            text = state.record.dateLabel,
            style = FintrackTheme.typography.bodyMedium,
            color = FintrackTheme.colors.texts.onSurfaceVariant,
        )
    }
}

// S-04: expenses are red, income green and transfers in the transfer colour
@Composable
private fun amountColor(type: TransactionType): Color {
    val semantics = FintrackTheme.colors.semantics
    return when (type) {
        TransactionType.EXPENSE -> semantics.expense
        TransactionType.INCOME -> semantics.income
        else -> semantics.transfer
    }
}
