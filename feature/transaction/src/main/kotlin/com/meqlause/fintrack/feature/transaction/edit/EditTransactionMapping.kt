package com.meqlause.fintrack.feature.transaction.edit

import com.meqlause.fintrack.core.domain.transaction.DraftRouting
import com.meqlause.fintrack.core.domain.transaction.TransactionDraft
import com.meqlause.fintrack.core.model.CategoryKind
import com.meqlause.fintrack.core.model.Memo
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Movement
import com.meqlause.fintrack.core.model.Transaction
import com.meqlause.fintrack.core.model.TransactionType

// FR-030..FR-034: the sheet state as the domain draft that SaveTransactionUseCase checks and stores
internal fun EditTransactionUiState.toDraft(): TransactionDraft {
    val form = this.form
    return TransactionDraft(
        existing = record.existing,
        movement =
            Movement(
                type = form.type,
                amount = form.amount,
                feeAmount = if (form.isTransfer) form.fee else Money(0L),
            ),
        routing =
            DraftRouting(
                walletId = form.walletId,
                targetWalletId = form.targetWalletId.takeIf { form.isTransfer },
                categoryId = form.categoryId.takeIf { !form.isTransfer },
            ),
        memo = (record.existing?.memo ?: Memo()).copy(note = form.note.trim()),
        occurredAt = record.occurredAt,
    )
}

// FR-034: an existing transaction filled into the sheet for editing
internal fun Transaction.toForm(): TransactionForm =
    TransactionForm(
        type = movement.type,
        amountDigits = digitsOf(movement.amount),
        feeDigits = digitsOf(movement.feeAmount),
        walletId = routing.walletId,
        targetWalletId = routing.targetWalletId,
        categoryId = routing.categoryId,
        note = memo.note,
    )

// BR-006: the category kind an expense or income needs; a transfer has none
internal fun TransactionType.categoryKind(): CategoryKind? =
    when (this) {
        TransactionType.EXPENSE -> CategoryKind.EXPENSE
        TransactionType.INCOME -> CategoryKind.INCOME
        else -> null
    }

// S-04: zero is shown as an empty field so the keypad starts clean
private fun digitsOf(money: Money): String = if (money.isZero) "" else money.rupiah.toString()
