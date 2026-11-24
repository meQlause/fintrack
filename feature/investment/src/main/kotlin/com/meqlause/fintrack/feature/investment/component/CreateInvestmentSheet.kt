package com.meqlause.fintrack.feature.investment.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.FintrackButton
import com.meqlause.fintrack.core.designsystem.component.NumericKeypad
import com.meqlause.fintrack.core.designsystem.component.PickerDrawer
import com.meqlause.fintrack.core.designsystem.component.SheetCardRow
import com.meqlause.fintrack.core.designsystem.component.SheetFieldLabel
import com.meqlause.fintrack.core.designsystem.component.SheetTextField
import com.meqlause.fintrack.core.designsystem.component.SheetValueRow
import com.meqlause.fintrack.core.designsystem.component.SheetValueText
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import com.meqlause.fintrack.feature.investment.InvestmentCallbacks
import com.meqlause.fintrack.feature.investment.InvestmentDraft
import com.meqlause.fintrack.feature.investment.InvestmentLabels
import com.meqlause.fintrack.feature.investment.KeypadDigits
import com.meqlause.fintrack.feature.investment.R

// FR-170, DOC-08: the new investment sheet, laid out like the new wallet sheet; PickerDrawer already scrolls
@Composable
internal fun CreateInvestmentSheet(
    draft: InvestmentDraft,
    callbacks: InvestmentCallbacks,
) {
    PickerDrawer(title = stringResource(R.string.investment_create_title), onClose = callbacks.sheet.onDismiss) {
        SheetFieldLabel(text = stringResource(R.string.investment_name_label))
        SheetTextField(
            value = draft.name,
            onValueChange = callbacks.onNameChange,
            placeholder = stringResource(R.string.investment_name_hint),
        )
        if (draft.nameRejected) {
            RejectedName()
        }
        KindRow(draft = draft, onClick = { callbacks.onOpenKindPicker(true) })
        SheetFieldLabel(text = stringResource(R.string.investment_invested_label))
        AmountRow(draft = draft)
        NumericKeypad(
            onDigit = callbacks.sheet.onDigit,
            onBackspace = callbacks.sheet.onBackspace,
            modifier = Modifier.padding(top = FintrackTheme.spacing.medium),
        )
        FintrackButton(
            text = stringResource(R.string.investment_save),
            onClick = callbacks.sheet.onSave,
            modifier = Modifier.padding(top = FintrackTheme.spacing.large),
            enabled = draft.canSave,
        )
    }
}

// FR-170: the kind of the investment, as the type row of the new wallet sheet
@Composable
private fun KindRow(
    draft: InvestmentDraft,
    onClick: () -> Unit,
) {
    val value =
        draft.kind?.let { stringResource(InvestmentLabels.kindRes(it)) }
            ?: stringResource(R.string.investment_kind_pick)
    SheetValueRow(
        text = SheetValueText(label = stringResource(R.string.investment_kind_label), value = value),
        onClick = onClick,
        modifier = Modifier.padding(top = FintrackTheme.spacing.medium),
    )
}

// FR-170: the amount put in with the keypad, shown in the card row of a sheet input
@Composable
private fun AmountRow(draft: InvestmentDraft) {
    SheetCardRow {
        Text(
            text = MoneyFormatter.format(KeypadDigits.toMoney(draft.invested)),
            style = FintrackScreenText.number(size = AMOUNT_SIZE, weight = AMOUNT_WEIGHT),
            color = FintrackTheme.colors.texts.onSurface,
        )
    }
}

// FR-170: the warning under the name field when the name was rejected
@Composable
private fun RejectedName() {
    Text(
        text = stringResource(R.string.investment_name_rejected),
        style = FintrackTheme.typography.bodyMedium,
        color = FintrackTheme.colors.semantics.error,
        modifier = Modifier.padding(top = FintrackTheme.spacing.xSmall),
    )
}

private const val AMOUNT_SIZE = 20f
private const val AMOUNT_WEIGHT = 700
