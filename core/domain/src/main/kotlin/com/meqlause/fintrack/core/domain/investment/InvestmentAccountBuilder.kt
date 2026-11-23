package com.meqlause.fintrack.core.domain.investment

import com.meqlause.fintrack.core.model.MoneyDirection
import com.meqlause.fintrack.core.model.Transaction
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.ValuationEntry
import com.meqlause.fintrack.core.model.Wallet
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.model.WalletType

// BR-050, BR-051: builds investment accounts from wallets, transfers and valuations
internal object InvestmentAccountBuilder {
    // FR-173: archived wallets and wallets of other types never appear in the portfolio
    fun isActiveInvestment(wallet: Wallet): Boolean =
        wallet.kind.type == WalletType.INVESTMENT && !wallet.status.isArchived

    // BR-051: the account of one wallet, valued from its own transfers and valuations only
    fun accountOf(
        wallet: Wallet,
        transactions: List<Transaction>,
        valuations: List<ValuationEntry>,
    ): InvestmentAccount {
        val flows = transactions.mapNotNull { flowOf(it, wallet.walletId) }
        val ownValuations = valuations.filter { it.walletId == wallet.walletId }
        return InvestmentAccount(
            wallet,
            InvestmentCalculator.valuate(wallet.terms.initialBalance, flows, ownValuations),
        )
    }

    fun accountsOf(
        wallets: List<Wallet>,
        transactions: List<Transaction>,
        valuations: List<ValuationEntry>,
    ): List<InvestmentAccount> =
        wallets.filter { isActiveInvestment(it) }.map { accountOf(it, transactions, valuations) }

    // BR-050: only TRANSFERs reach an investment; it is the target for money put in and the source for money taken out
    fun flowOf(
        transaction: Transaction,
        walletId: WalletId,
    ): InvestmentFlow? {
        if (transaction.movement.type != TransactionType.TRANSFER) return null
        val routing = transaction.routing
        val direction =
            when (walletId) {
                routing.targetWalletId -> MoneyDirection.CREDIT
                routing.walletId -> MoneyDirection.DEBIT
                else -> return null
            }
        return InvestmentFlow(transaction.timing.occurredAt, transaction.movement.amount, direction)
    }
}
