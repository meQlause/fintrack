package com.meqlause.fintrack.feature.loan.detail

import com.meqlause.fintrack.core.domain.loan.LoanDueOption
import com.meqlause.fintrack.core.model.WalletId

// S-17: everything the loan detail can ask its ViewModel to do; the screen keeps no state of its own
internal sealed interface LoanDetailEvent {
    data object OpenRepayment : LoanDetailEvent

    data object OpenDueDate : LoanDetailEvent

    data object DismissSheet : LoanDetailEvent

    data class DigitTyped(
        val digit: String,
    ) : LoanDetailEvent

    data object BackspacePressed : LoanDetailEvent

    data class WalletPicked(
        val walletId: WalletId,
    ) : LoanDetailEvent

    data object SaveRepayment : LoanDetailEvent

    data class DuePicked(
        val option: LoanDueOption,
    ) : LoanDetailEvent

    data class DialogOpened(
        val dialog: LoanDialog,
    ) : LoanDetailEvent

    data object DialogDismissed : LoanDetailEvent

    data object DialogConfirmed : LoanDetailEvent
}
