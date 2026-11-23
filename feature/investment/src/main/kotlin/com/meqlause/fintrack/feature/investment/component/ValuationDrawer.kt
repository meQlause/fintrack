package com.meqlause.fintrack.feature.investment.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.FintrackButton
import com.meqlause.fintrack.core.designsystem.component.NumericKeypad
import com.meqlause.fintrack.core.designsystem.component.PickerDrawer
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import com.meqlause.fintrack.feature.investment.AmountSheetCallbacks
import com.meqlause.fintrack.feature.investment.KeypadDigits
import com.meqlause.fintrack.feature.investment.R

// FR-172: the keypad sheet that records the current value of an investment, prefilled with the last value
@Composable
internal fun ValuationDrawer(
    digits: String,
    callbacks: AmountSheetCallbacks,
) {
    val typography = FintrackTheme.typography
    PickerDrawer(title = stringResource(R.string.investment_value_title), onClose = callbacks.onDismiss) {
        Column {
            Text(
                text = stringResource(R.string.investment_value_hint),
                style = typography.labelMedium,
                color = FintrackTheme.colors.texts.onSurfaceVariant,
            )
            Text(
                text = MoneyFormatter.format(KeypadDigits.toMoney(digits)),
                style = typography.numberLarge,
                color = FintrackTheme.colors.texts.onSurface,
            )
            NumericKeypad(onDigit = callbacks.onDigit, onBackspace = callbacks.onBackspace)
            FintrackButton(
                text = stringResource(R.string.investment_save),
                onClick = callbacks.onSave,
                modifier = Modifier.padding(top = FintrackTheme.spacing.medium),
            )
        }
    }
}
