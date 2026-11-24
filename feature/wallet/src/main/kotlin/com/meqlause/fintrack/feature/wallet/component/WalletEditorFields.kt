package com.meqlause.fintrack.feature.wallet.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.FintrackSwitch
import com.meqlause.fintrack.core.designsystem.component.SheetCardRow
import com.meqlause.fintrack.core.designsystem.component.SheetFieldLabel
import com.meqlause.fintrack.core.designsystem.component.SheetNumberField
import com.meqlause.fintrack.core.designsystem.component.SheetValueRow
import com.meqlause.fintrack.core.designsystem.component.SheetValueText
import com.meqlause.fintrack.core.designsystem.icon.IconLook
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.feature.wallet.R
import com.meqlause.fintrack.feature.wallet.WalletAppearance
import com.meqlause.fintrack.feature.wallet.WalletCallbacks
import com.meqlause.fintrack.feature.wallet.WalletEditor
import com.meqlause.fintrack.feature.wallet.WalletLabels
import com.meqlause.fintrack.feature.wallet.WalletPicker

// FR-010, FR-139, FR-170: type, investment kind, opening balance, credit terms and the total switch
@Composable
internal fun WalletFormFields(
    draft: WalletEditor,
    callbacks: WalletCallbacks,
) {
    Column(modifier = Modifier.padding(top = FintrackTheme.spacing.medium)) {
        val appearance = WalletAppearance.forType(draft.type)
        SheetValueRow(
            text =
                SheetValueText(
                    stringResource(R.string.wallet_field_type),
                    stringResource(WalletLabels.type(draft.type)),
                ),
            onClick = { callbacks.onOpenPicker(WalletPicker.TYPE) },
            look = IconLook(appearance.iconKey, appearance.colorKey),
        )
        if (draft.isInvestment) {
            InvestKindRow(draft = draft, onClick = { callbacks.onOpenPicker(WalletPicker.INVEST_KIND) })
        }
        BalanceField(draft = draft, onChange = callbacks.onInitialBalanceChange)
        if (draft.isCredit) {
            CreditFields(draft = draft, callbacks = callbacks)
        }
        IncludeInTotalRow(draft = draft, onChange = callbacks.onIncludeInTotalChange)
    }
}

// FR-170: the kind of investment, opened in its own picker
@Composable
private fun InvestKindRow(
    draft: WalletEditor,
    onClick: () -> Unit,
) {
    SheetValueRow(
        text =
            SheetValueText(
                stringResource(R.string.wallet_field_invest_kind),
                stringResource(WalletLabels.investKind(draft.investKind)),
            ),
        onClick = onClick,
        modifier = Modifier.padding(top = FintrackTheme.spacing.small),
    )
}

// FR-013: the switch that decides whether the wallet counts in the total
@Composable
private fun IncludeInTotalRow(
    draft: WalletEditor,
    onChange: (Boolean) -> Unit,
) {
    SheetCardRow(modifier = Modifier.padding(top = FintrackTheme.spacing.medium)) {
        Text(
            text = stringResource(R.string.wallet_include_total),
            style = FintrackScreenText.style(size = 14f, weight = 600),
            color = FintrackTheme.colors.texts.onSurface,
            modifier = Modifier.weight(1f),
        )
        FintrackSwitch(checked = draft.includeInTotal, onCheckedChange = onChange)
    }
}

// FR-139: limit, statement day and due day of a card or paylater wallet
@Composable
private fun CreditFields(
    draft: WalletEditor,
    callbacks: WalletCallbacks,
) {
    SheetFieldLabel(text = stringResource(R.string.wallet_field_limit))
    SheetNumberField(
        value = draft.creditLimit,
        onValueChange = callbacks.onCreditLimitChange,
        placeholder = stringResource(R.string.wallet_balance_hint),
    )
    if (draft.limitError) {
        ErrorText(text = stringResource(R.string.wallet_limit_invalid))
    }
    SheetFieldLabel(text = stringResource(R.string.wallet_cycle_title))
    DayRow(label = R.string.wallet_field_statement_day, dayOfMonth = draft.statementDay) {
        callbacks.onOpenPicker(WalletPicker.STATEMENT_DAY)
    }
    if (draft.walletId == null) {
        NotIssuedNote()
    }
    DayRow(
        label = R.string.wallet_field_due_day,
        dayOfMonth = draft.dueDay,
        modifier = Modifier.padding(top = FintrackTheme.spacing.small),
    ) {
        callbacks.onOpenPicker(WalletPicker.DUE_DAY)
    }
}

// FR-139: a new card has no statement yet
@Composable
private fun NotIssuedNote() {
    Text(
        text = stringResource(R.string.wallet_not_issued),
        style = FintrackTheme.typography.bodyMedium,
        color = FintrackTheme.colors.texts.onSurfaceVariant,
        modifier = Modifier.padding(top = FintrackTheme.spacing.xSmall),
    )
}

@Composable
private fun DayRow(
    label: Int,
    dayOfMonth: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    SheetValueRow(
        text = SheetValueText(stringResource(label), stringResource(R.string.wallet_day_value, dayOfMonth)),
        onClick = onClick,
        modifier = modifier,
    )
}
