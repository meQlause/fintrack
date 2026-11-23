package com.meqlause.fintrack.feature.transaction.edit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.ui.format.MoneyFormatter

// FR-030, FR-033, S-04: the 42 px amount in the type colour (grey while empty) over its validation message
@Composable
internal fun EditTransactionAmountBlock(
    state: EditTransactionUiState,
    onEvent: (EditTransactionEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(top = FintrackTheme.spacing.stack),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = MoneyFormatter.format(state.form.amount),
            style = FintrackScreenText.number(size = 42f, weight = 700),
            color = amountColor(state),
            modifier = Modifier.clickable { onEvent(EditTransactionEvent.FieldFocused(KeypadField.AMOUNT)) },
        )
        Text(
            text =
                state.errors
                    .minByOrNull { it.ordinal }
                    ?.let { stringResource(it.messageRes()) }
                    .orEmpty(),
            style = FintrackScreenText.style(size = 12f, weight = 600),
            color = FintrackTheme.colors.semantics.error,
            modifier =
                Modifier
                    .padding(top = FintrackTheme.spacing.xxSmall)
                    .heightIn(min = FintrackScreenSizes.messageHeight),
        )
    }
}

// S-04: expenses are red, income green and transfers in the transfer colour; an empty amount is muted
@Composable
private fun amountColor(state: EditTransactionUiState): Color {
    val colors = FintrackTheme.colors
    return when {
        state.form.amount.rupiah == 0L -> colors.texts.onSurfaceVariant
        state.form.type == TransactionType.EXPENSE -> colors.semantics.expense
        state.form.type == TransactionType.INCOME -> colors.semantics.income
        else -> colors.semantics.transfer
    }
}
