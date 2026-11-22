package com.meqlause.fintrack.core.data.mapper

import com.meqlause.fintrack.core.database.entity.TransactionEntity
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.InboxItemId
import com.meqlause.fintrack.core.model.LoanId
import com.meqlause.fintrack.core.model.Memo
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Movement
import com.meqlause.fintrack.core.model.Origin
import com.meqlause.fintrack.core.model.Routing
import com.meqlause.fintrack.core.model.Timing
import com.meqlause.fintrack.core.model.Transaction
import com.meqlause.fintrack.core.model.TransactionId
import com.meqlause.fintrack.core.model.WalletId

// DOC-02, BR-001, BR-002: maps a transaction row to the domain model; optional links stay null
internal fun TransactionEntity.toTransaction(): Transaction =
    Transaction(
        transactionId = TransactionId(transactionId),
        movement =
            Movement(
                type = details.type,
                amount = Money(figures.amount),
                feeAmount = Money(figures.feeAmount),
            ),
        routing =
            Routing(
                walletId = WalletId(accounts.walletId),
                targetWalletId = accounts.targetWalletId?.let(::WalletId),
                categoryId = accounts.categoryId?.let(::CategoryId),
                loanId = accounts.loanId?.let(::LoanId),
            ),
        memo = Memo(note = details.note, merchant = details.merchant),
        origin = Origin(source = origin.source, inboxItemId = origin.inboxItemId?.let(::InboxItemId)),
        timing = Timing(occurredAt = details.occurredAt.toInstant(), audit = audit.toAudit()),
    )

// DOC-02, BR-001, BR-002: maps a transaction to its row
internal fun Transaction.toEntity(): TransactionEntity =
    TransactionEntity(
        transactionId = transactionId.value,
        figures =
            TransactionEntity.Amounts(
                amount = movement.amount.rupiah,
                feeAmount = movement.feeAmount.rupiah,
            ),
        accounts =
            TransactionEntity.Accounts(
                walletId = routing.walletId.value,
                targetWalletId = routing.targetWalletId?.value,
                categoryId = routing.categoryId?.value,
                loanId = routing.loanId?.value,
            ),
        details =
            TransactionEntity.Details(
                type = movement.type,
                note = memo.note,
                merchant = memo.merchant,
                occurredAt = timing.occurredAt.toEpochMilli(),
            ),
        origin =
            TransactionEntity.Origin(
                source = origin.source,
                inboxItemId = origin.inboxItemId?.value,
            ),
        audit = timing.audit.toColumns(),
    )
