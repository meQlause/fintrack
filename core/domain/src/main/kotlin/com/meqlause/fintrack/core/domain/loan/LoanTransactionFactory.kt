package com.meqlause.fintrack.core.domain.loan

import com.meqlause.fintrack.core.model.Memo
import com.meqlause.fintrack.core.model.Movement
import com.meqlause.fintrack.core.model.Origin
import com.meqlause.fintrack.core.model.Routing
import com.meqlause.fintrack.core.model.Timing
import com.meqlause.fintrack.core.model.Transaction
import com.meqlause.fintrack.core.model.TransactionId
import com.meqlause.fintrack.core.model.TransactionSource

// BR-040, FR-156: a loan movement carries the loan id and no category, so statistics and budgets never count it
internal object LoanTransactionFactory {
    fun movement(
        movement: Movement,
        routing: Routing,
        memo: Memo,
        timing: Timing,
    ): Transaction =
        Transaction(
            transactionId = TransactionId(0L),
            movement = movement,
            routing = routing,
            memo = memo,
            origin = Origin(source = TransactionSource.MANUAL, inboxItemId = null),
            timing = timing,
        )
}
