package com.meqlause.fintrack.feature.investment.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.FintrackButton
import com.meqlause.fintrack.core.designsystem.component.FormRow
import com.meqlause.fintrack.core.designsystem.component.NumericKeypad
import com.meqlause.fintrack.core.designsystem.component.PickerDrawer
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import com.meqlause.fintrack.feature.investment.InvestmentCallbacks
import com.meqlause.fintrack.feature.investment.InvestmentDraft
import com.meqlause.fintrack.feature.investment.InvestmentLabels
import com.meqlause.fintrack.feature.investment.KeypadDigits
import com.meqlause.fintrack.feature.investment.R

// FR-170: the sheet that creates an investment account with its name, kind and amount already put in
@Composable
internal fun CreateInvestmentSheet(
    draft: InvestmentDraft,
    callbacks: InvestmentCallbacks,
) {
    PickerDrawer(title = stringResource(R.string.investment_create_title), onClose = callbacks.sheet.onDismiss) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            NameAndKindFields(draft = draft, callbacks = callbacks)
            InvestedAmount(draft = draft)
            NumericKeypad(onDigit = callbacks.sheet.onDigit, onBackspace = callbacks.sheet.onBackspace)
            FintrackButton(
                text = stringResource(R.string.investment_save),
                onClick = callbacks.sheet.onSave,
                modifier = Modifier.padding(top = FintrackTheme.spacing.medium),
                enabled = draft.canSave,
            )
        }
    }
}

// FR-170: the name field and the kind row of the new investment
@Composable
private fun NameAndKindFields(
    draft: InvestmentDraft,
    callbacks: InvestmentCallbacks,
) {
    OutlinedTextField(
        value = draft.name,
        onValueChange = callbacks.onNameChange,
        modifier = Modifier.fillMaxWidth().padding(vertical = FintrackTheme.spacing.small),
        label = { Text(text = stringResource(R.string.investment_name_label)) },
        singleLine = true,
    )
    FormRow(
        label = stringResource(R.string.investment_kind_label),
        value =
            draft.kind?.let { stringResource(InvestmentLabels.kindRes(it)) }
                ?: stringResource(R.string.investment_kind_pick),
        onClick = { callbacks.onOpenKindPicker(true) },
    )
}

// FR-170: the amount put in, and the warning when the name was rejected
@Composable
private fun InvestedAmount(draft: InvestmentDraft) {
    val colors = FintrackTheme.colors
    val typography = FintrackTheme.typography
    Text(
        text = stringResource(R.string.investment_invested_label),
        modifier = Modifier.padding(top = FintrackTheme.spacing.medium),
        style = typography.labelMedium,
        color = colors.texts.onSurfaceVariant,
    )
    Text(
        text = MoneyFormatter.format(KeypadDigits.toMoney(draft.invested)),
        style = typography.numberLarge,
        color = colors.texts.onSurface,
    )
    if (draft.nameRejected) {
        Text(
            text = stringResource(R.string.investment_name_rejected),
            style = typography.bodyMedium,
            color = colors.semantics.error,
        )
    }
}
