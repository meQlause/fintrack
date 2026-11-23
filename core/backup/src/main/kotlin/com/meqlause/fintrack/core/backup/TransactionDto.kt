package com.meqlause.fintrack.core.backup

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
import com.meqlause.fintrack.core.model.TransactionSource
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.WalletId
import kotlinx.serialization.Serializable

// DOC-11: one transaction row in the backup; nested groups mirror the domain model
@Serializable
internal data class TransactionDto(
    val transactionId: Long,
    val movement: MovementDto,
    val routing: RoutingDto,
    val memo: MemoDto,
    val origin: OriginDto,
    val timing: TimingDto,
) {
    fun toModel(): Transaction =
        Transaction(
            transactionId = TransactionId(transactionId),
            movement = movement.toModel(),
            routing = routing.toModel(),
            memo = Memo(memo.note, memo.merchant),
            origin =
                Origin(
                    BackupValues.enumNamed<TransactionSource>(origin.source),
                    origin.inboxItemId?.let(::InboxItemId),
                ),
            timing = Timing(BackupValues.instant(timing.occurredAt), timing.audit.toModel()),
        )

    companion object {
        fun from(transaction: Transaction): TransactionDto =
            TransactionDto(
                transactionId = transaction.transactionId.value,
                movement =
                    MovementDto(
                        transaction.movement.type.name,
                        transaction.movement.amount.rupiah,
                        transaction.movement.feeAmount.rupiah,
                    ),
                routing =
                    RoutingDto(
                        transaction.routing.walletId.value,
                        transaction.routing.targetWalletId?.value,
                        transaction.routing.categoryId?.value,
                        transaction.routing.loanId?.value,
                    ),
                memo = MemoDto(transaction.memo.note, transaction.memo.merchant),
                origin = OriginDto(transaction.origin.source.name, transaction.origin.inboxItemId?.value),
                timing = TimingDto(transaction.timing.occurredAt.toString(), AuditDto.from(transaction.timing.audit)),
            )
    }

    @Serializable
    internal data class MovementDto(
        val type: String,
        val amount: Long,
        val feeAmount: Long,
    ) {
        fun toModel(): Movement =
            Movement(BackupValues.enumNamed<TransactionType>(type), Money(amount), Money(feeAmount))
    }

    @Serializable
    internal data class RoutingDto(
        val walletId: Long,
        val targetWalletId: Long?,
        val categoryId: Long?,
        val loanId: Long?,
    ) {
        fun toModel(): Routing =
            Routing(
                walletId = WalletId(walletId),
                targetWalletId = targetWalletId?.let(::WalletId),
                categoryId = categoryId?.let(::CategoryId),
                loanId = loanId?.let(::LoanId),
            )
    }

    @Serializable
    internal data class MemoDto(
        val note: String,
        val merchant: String,
    )

    @Serializable
    internal data class OriginDto(
        val source: String,
        val inboxItemId: Long?,
    )

    @Serializable
    internal data class TimingDto(
        val occurredAt: String,
        val audit: AuditDto,
    )
}
