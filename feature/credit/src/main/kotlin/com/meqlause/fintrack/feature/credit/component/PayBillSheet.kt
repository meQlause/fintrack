package com.meqlause.fintrack.feature.credit.component

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.meqlause.fintrack.core.designsystem.component.FintrackBottomSheet
import com.meqlause.fintrack.core.designsystem.component.FintrackButton
import com.meqlause.fintrack.core.designsystem.component.FintrackChip
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.feature.credit.R
import com.meqlause.fintrack.feature.credit.detail.CreditDetailCallbacks
import com.meqlause.fintrack.feature.credit.detail.PayDraft
import com.meqlause.fintrack.feature.credit.detail.PaySource

// FR-132, FR-133, S-15: the bill payment sheet; saving stores a TRANSFER from a non-credit wallet (BR-039)
@Composable
internal fun PayBillSheet(
    draft: PayDraft,
    sources: List<PaySource>,
    callbacks: CreditDetailCallbacks,
    modifier: Modifier = Modifier,
) {
    FintrackBottomSheet(modifier = modifier) {
        Text(
            text = stringResource(R.string.credit_pay_title),
            style = FintrackTheme.typography.titleMedium,
            color = FintrackTheme.colors.texts.onSurface,
        )
        SourceChips(draft = draft, sources = sources, onPick = callbacks.onPickSource)
        OutlinedTextField(
            value = draft.typedDigits,
            onValueChange = callbacks.onAmountChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(R.string.credit_pay_amount)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
        )
        PayAmountChips(callbacks = callbacks)
        if (draft.failed) PayFailedNotice()
        PayButtons(canPay = sources.isNotEmpty(), callbacks = callbacks)
    }
}

// FR-133: shown when the payment could not be stored
@Composable
private fun PayFailedNotice() {
    Text(
        text = stringResource(R.string.credit_pay_failed),
        style = FintrackTheme.typography.bodyMedium,
        color = FintrackTheme.colors.semantics.error,
    )
}

// FR-132: save the payment, or cancel the sheet
@Composable
private fun PayButtons(
    canPay: Boolean,
    callbacks: CreditDetailCallbacks,
) {
    Column(verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small)) {
        FintrackButton(text = stringResource(R.string.credit_pay_save), onClick = callbacks.onPay, enabled = canPay)
        FintrackButton(text = stringResource(R.string.credit_pay_cancel), onClick = callbacks.onDismissPay)
    }
}

// FR-133: fill the whole remaining bill, or clear the entry to type a part of it
@Composable
private fun PayAmountChips(callbacks: CreditDetailCallbacks) {
    Row(horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small)) {
        FintrackChip(
            label = stringResource(R.string.credit_pay_full),
            selected = false,
            onClick = callbacks.onFillRemaining,
        )
        FintrackChip(
            label = stringResource(R.string.credit_pay_partial),
            selected = false,
            onClick = callbacks.onClearEntry,
        )
    }
}

// BR-039: only non-credit wallets are offered; the first one is selected until the user picks another
@Composable
private fun SourceChips(
    draft: PayDraft,
    sources: List<PaySource>,
    onPick: (WalletId) -> Unit,
) {
    if (sources.isEmpty()) {
        Text(
            text = stringResource(R.string.credit_pay_no_source),
            style = FintrackTheme.typography.bodyMedium,
            color = FintrackTheme.colors.texts.onSurfaceVariant,
        )
        return
    }
    val selected = draft.sourceId ?: sources.first().walletId
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
    ) {
        sources.forEach { source ->
            FintrackChip(
                label = source.name,
                selected = source.walletId == selected,
                onClick = { onPick(source.walletId) },
            )
        }
    }
}
