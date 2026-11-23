package com.meqlause.fintrack.feature.budget.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.FintrackButton
import com.meqlause.fintrack.core.designsystem.component.NumericKeypad
import com.meqlause.fintrack.core.designsystem.component.PickerDrawer
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import com.meqlause.fintrack.feature.budget.BudgetCallbacks
import com.meqlause.fintrack.feature.budget.BudgetEditor
import com.meqlause.fintrack.feature.budget.R

// FR-070, FR-074: types the monthly limit of the chosen target on the keypad (BR-001 bounds)
@Composable
internal fun BudgetAmountDrawer(
    editor: BudgetEditor.Amount,
    callbacks: BudgetCallbacks,
) {
    val title = editor.target.categoryName ?: stringResource(R.string.budget_overall)
    val amount = Money(editor.digits.toLongOrNull() ?: 0L)
    PickerDrawer(title = title, onClose = callbacks.onDismissEditor) {
        Text(
            text = MoneyFormatter.format(amount),
            style = FintrackTheme.typography.titleMedium,
            color = FintrackTheme.colors.texts.onSurface,
        )
        NumericKeypad(onDigit = callbacks.onDigit, onBackspace = callbacks.onBackspace)
        FintrackButton(
            text = stringResource(R.string.budget_save),
            onClick = callbacks.onSave,
            enabled = amount.isTransactionAmount(),
        )
    }
}
