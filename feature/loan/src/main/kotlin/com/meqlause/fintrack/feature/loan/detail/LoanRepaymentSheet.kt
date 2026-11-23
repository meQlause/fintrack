package com.meqlause.fintrack.feature.loan.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.FintrackButton
import com.meqlause.fintrack.core.designsystem.component.FintrackChip
import com.meqlause.fintrack.core.designsystem.component.NumericKeypad
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.domain.loan.RepaymentError
import com.meqlause.fintrack.core.model.Wallet
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import com.meqlause.fintrack.feature.loan.R

// FR-152, S-17: the repayment sheet; a keypad amount, the wallet the money moves through, and Simpan
@Composable
internal fun LoanRepaymentSheet(
    state: LoanDetailUiState,
    onEvent: (LoanDetailEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
    ) {
        RepaymentHeader(amount = MoneyFormatter.format(state.repayment.amount))
        WalletChips(
            wallets = state.wallets,
            selected = state.repayment.walletId,
            onPick = { walletId -> onEvent(LoanDetailEvent.WalletPicked(walletId)) },
        )
        RepaymentErrors(state.repayment.errors)
        NumericKeypad(
            onDigit = { digit -> onEvent(LoanDetailEvent.DigitTyped(digit)) },
            onBackspace = { onEvent(LoanDetailEvent.BackspacePressed) },
        )
        FintrackButton(
            text = stringResource(R.string.loan_repay_save),
            onClick = { onEvent(LoanDetailEvent.SaveRepayment) },
            enabled = state.repayment.amount.rupiah > 0L && state.repayment.walletId != null,
        )
    }
}

// FR-152: the sheet title over the amount typed so far, both on the left
@Composable
private fun RepaymentHeader(amount: String) {
    Column(verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small)) {
        Text(
            text = stringResource(R.string.loan_repay_title),
            style = FintrackTheme.typography.titleMedium,
            color = FintrackTheme.colors.texts.onSurface,
        )
        Text(
            text = amount,
            style = FintrackTheme.typography.headlineSmall,
            color = FintrackTheme.colors.texts.onSurface,
        )
    }
}

// FR-152: each rejected repayment reason as a red line, in the order of the error enum
@Composable
private fun RepaymentErrors(errors: Set<RepaymentError>) {
    errors.sortedBy { it.ordinal }.forEach { error ->
        Text(
            text = stringResource(error.messageRes()),
            style = FintrackTheme.typography.labelMedium,
            color = FintrackTheme.colors.semantics.error,
        )
    }
}

// FR-152: the usable wallets as chips in one scrolling row; the chosen one is selected
@Composable
private fun WalletChips(
    wallets: List<Wallet>,
    selected: WalletId?,
    onPick: (WalletId) -> Unit,
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small)) {
        items(items = wallets, key = { it.walletId.value }) { wallet ->
            FintrackChip(
                label = wallet.profile.name,
                selected = wallet.walletId == selected,
                onClick = { onPick(wallet.walletId) },
            )
        }
    }
}
