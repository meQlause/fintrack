package com.meqlause.fintrack.feature.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.FintrackChip
import com.meqlause.fintrack.core.designsystem.component.NumericKeypad
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.domain.validation.WalletNameError
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.WalletType
import com.meqlause.fintrack.core.ui.format.MoneyFormatter

/** S-01, FR-002: page 2, the required first wallet: name, type and initial balance on the keypad. */
@Composable
internal fun FirstWalletPage(
    form: FirstWalletForm,
    onAction: (OnboardingAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.medium),
    ) {
        PageIntro(
            title = stringResource(R.string.onboarding_wallet_title),
            body = stringResource(R.string.onboarding_wallet_body),
        )
        NameField(
            name = form.name,
            error = form.nameError,
            onNameChange = { onAction(OnboardingAction.NameChanged(it)) },
        )
        TypeChoices(selected = form.type, onSelect = { onAction(OnboardingAction.TypeSelected(it)) })
        BalanceDisplay(balance = form.balance)
        NumericKeypad(
            onDigit = { onAction(OnboardingAction.DigitTyped(it)) },
            onBackspace = { onAction(OnboardingAction.BackspaceTyped) },
        )
    }
}

@Composable
private fun NameField(
    name: String,
    error: WalletNameError?,
    onNameChange: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.xSmall)) {
        FieldLabel(text = stringResource(R.string.onboarding_wallet_name_label))
        TextField(
            value = name,
            onValueChange = onNameChange,
            singleLine = true,
            textStyle = FintrackTheme.typography.bodyLarge,
            shape = FintrackTheme.shapes.medium,
            colors = nameFieldColors(),
            modifier = Modifier.fillMaxWidth(),
        )
        error?.let { ErrorText(reason = it) }
    }
}

// S-01: the name field uses the surface tint and no underline, like the other form fields of the app
@Composable
private fun nameFieldColors(): TextFieldColors {
    val colors = FintrackTheme.colors
    return TextFieldDefaults.colors(
        focusedContainerColor = colors.surfaces.surfaceVariant,
        unfocusedContainerColor = colors.surfaces.surfaceVariant,
        focusedTextColor = colors.texts.onSurface,
        unfocusedTextColor = colors.texts.onSurface,
        cursorColor = colors.brands.primary,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
    )
}

@Composable
private fun ErrorText(reason: WalletNameError) {
    val message =
        when (reason) {
            WalletNameError.BLANK -> stringResource(R.string.onboarding_wallet_name_error_blank)
            WalletNameError.TOO_LONG -> stringResource(R.string.onboarding_wallet_name_error_long)
        }
    Text(text = message, style = FintrackTheme.typography.labelMedium, color = FintrackTheme.colors.semantics.error)
}

@Composable
private fun TypeChoices(
    selected: WalletType,
    onSelect: (WalletType) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.xSmall)) {
        FieldLabel(text = stringResource(R.string.onboarding_wallet_type_label))
        Row(horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small)) {
            WALLET_TYPE_LABELS.forEach { (type, labelRes) ->
                FintrackChip(
                    label = stringResource(labelRes),
                    selected = type == selected,
                    onClick = { onSelect(type) },
                )
            }
        }
    }
}

@Composable
private fun BalanceDisplay(balance: Money) {
    Column(verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.xSmall)) {
        FieldLabel(text = stringResource(R.string.onboarding_balance_label))
        Text(
            text = MoneyFormatter.format(balance),
            style = FintrackTheme.typography.numberLarge,
            color = FintrackTheme.colors.texts.onSurface,
        )
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(text = text, style = FintrackTheme.typography.labelMedium, color = FintrackTheme.colors.texts.onSurfaceVariant)
}

private val WALLET_TYPE_LABELS =
    listOf(
        WalletType.CASH to R.string.onboarding_wallet_type_cash,
        WalletType.BANK to R.string.onboarding_wallet_type_bank,
        WalletType.EWALLET to R.string.onboarding_wallet_type_ewallet,
    )
