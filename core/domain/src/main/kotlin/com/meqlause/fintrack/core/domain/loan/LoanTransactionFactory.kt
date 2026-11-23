package com.meqlause.fintrack.core.domain.loan

import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.LoanId
import com.meqlause.fintrack.core.model.Memo
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Movement
import com.meqlause.fintrack.core.model.Origin
import com.meqlause.fintrack.core.model.Routing
import com.meqlause.fintrack.core.model.Timing
import com.meqlause.fintrack.core.model.Transaction
import com.meqlause.fintrack.core.model.TransactionId
import com.meqlause.fintrack.core.model.TransactionSource
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.WalletId
import java.time.Instant

// BR-040, FR-156: a loan movement carries the loan id and no category, so statistics and budgets never count it
internal object LoanTransactionFactory {
    fun movement(
        type: TransactionType,
        amount: Money,
        walletId: WalletId,
        loanId: LoanId,
        note: String,
        occurredAt: Instant,
        now: Instant,
    ): Transaction =
        Transaction(
            transactionId = TransactionId(0L),
            movement = Movement(type = type, amount = amount),
            routing = Routing(walletId = walletId, targetWalletId = null, categoryId = null, loanId = loanId),
            memo = Memo(note = note),
            origin = Origin(source = TransactionSource.MANUAL, inboxItemId = null),
            timing = Timing(occurredAt = occurredAt, audit = Audit(createdAt = now, updatedAt = now)),
        )
}
