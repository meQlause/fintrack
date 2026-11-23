package com.meqlause.fintrack.core.domain.credit

import com.meqlause.fintrack.core.model.InstallmentPlan
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Movement
import com.meqlause.fintrack.core.model.Transaction
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.Wallet
import com.meqlause.fintrack.core.model.WalletId
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/** BR-031 to BR-036: turns a stored credit wallet with its transactions and plans into the pure statement input. */
public object CreditAccountFactory {
    /** BR-031, BR-033: [wallet] must carry credit terms; [zone] turns instants into the dates statement math uses. */
    public fun build(
        wallet: Wallet,
        transactions: List<Transaction>,
        plans: List<InstallmentPlan>,
        zone: ZoneId,
    ): CreditAccount {
        val terms = requireNotNull(wallet.terms.credit) { "a credit wallet needs credit terms (FR-139)" }
        val id = wallet.walletId
        return CreditAccount(
            terms = terms,
            initialBalance = wallet.terms.initialBalance,
            createdOn = wallet.audit.createdAt.toLocalDate(zone),
            entries = transactions.mapNotNull { entryOf(it, id, zone) },
            instalments =
                plans.filter { it.purchase.walletId == id }.mapNotNull { instalmentOf(it, transactions, zone) },
        )
    }

    // BR-031: the signed effect of one transaction on this wallet, the same rule the balance query uses
    private fun entryOf(
        transaction: Transaction,
        walletId: WalletId,
        zone: ZoneId,
    ): CreditEntry? {
        val date = transaction.timing.occurredAt.toLocalDate(zone)
        return when {
            transaction.routing.walletId == walletId -> {
                CreditEntry(date, Money(outgoingDelta(transaction.movement)), isTransferIn = false)
            }

            transaction.routing.targetWalletId == walletId -> {
                CreditEntry(date, transaction.movement.amount, isTransferIn = true)
            }

            else -> {
                null
            }
        }
    }

    // BR-004, BR-031: money leaving the wallet is negative; a transfer also pays its fee from the source
    private fun outgoingDelta(movement: Movement): Long {
        val amount = movement.amount.rupiah
        return when (movement.type) {
            TransactionType.EXPENSE, TransactionType.LEND, TransactionType.REPAY -> -amount
            TransactionType.INCOME, TransactionType.BORROW, TransactionType.COLLECT -> amount
            TransactionType.TRANSFER -> -(amount + movement.feeAmount.rupiah)
        }
    }

    // BR-035, BR-036: the statement input of a plan; the purchase date comes from its stored purchase transaction
    public fun instalmentOf(
        plan: InstallmentPlan,
        transactions: List<Transaction>,
        zone: ZoneId,
    ): CreditInstalment? {
        val purchase = plan.purchase
        val purchaseTx = transactions.firstOrNull { it.transactionId == purchase.transactionId } ?: return null
        return CreditInstalment(
            purchaseDate = purchaseTx.timing.occurredAt.toLocalDate(zone),
            principal = purchase.principal,
            tenorMonths = plan.schedule.tenorMonths,
            firstStatementMonth = plan.schedule.firstStatementMonth,
            earlyPayoffMonth = plan.schedule.earlyPayoffMonth,
        )
    }

    private fun Instant.toLocalDate(zone: ZoneId): LocalDate = atZone(zone).toLocalDate()
}
