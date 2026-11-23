package com.meqlause.fintrack.core.backup

import com.meqlause.fintrack.core.model.BackupLedger
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Transaction
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.Wallet

// DOC-11, BR-001, BR-002, BR-044: ledger rules a crafted or damaged backup must not break
internal object LedgerRules {
    private const val MAX_NAME_LENGTH = 30
    private const val MAX_DAY = 28

    fun validate(
        ledger: BackupLedger,
        backupIds: BackupIds,
    ) {
        validateWallets(ledger.wallets)
        validateCategories(ledger)
        validateTransactions(ledger.transactions, backupIds)
        validateBudgets(ledger, backupIds)
        BackupChecks.unique(ledger.merchantRules.map { it.merchantRuleId.value })
        BackupChecks.unique(ledger.merchantRules.map { it.merchantKey })
        ledger.merchantRules.forEach { rule ->
            BackupChecks.present(backupIds.categoryIds, rule.categoryId.value)
            BackupChecks.optional(backupIds.walletIds, rule.walletId?.value)
        }
    }

    private fun validateWallets(wallets: List<Wallet>) {
        BackupChecks.unique(wallets.map { it.walletId.value })
        BackupChecks.unique(wallets.filterNot { it.status.isArchived }.map { it.profile.name.lowercase() })
        wallets.forEach { wallet ->
            BackupChecks.ensure(wallet.profile.name.length in 1..MAX_NAME_LENGTH)
            wallet.terms.credit?.let { credit ->
                BackupChecks.ensure(credit.creditLimit.rupiah > 0)
                BackupChecks.ensure(credit.statementDay in 1..MAX_DAY && credit.dueDay in 1..MAX_DAY)
            }
        }
    }

    private fun validateCategories(ledger: BackupLedger) {
        BackupChecks.unique(ledger.categories.map { it.categoryId.value })
        ledger.categories.forEach { BackupChecks.ensure(it.profile.name.isNotBlank()) }
    }

    private fun validateTransactions(
        transactions: List<Transaction>,
        backupIds: BackupIds,
    ) {
        BackupChecks.unique(transactions.map { it.transactionId.value })
        transactions.forEach { transaction ->
            validateTransaction(transaction, backupIds)
        }
    }

    private fun validateTransaction(
        transaction: Transaction,
        backupIds: BackupIds,
    ) {
        val routing = transaction.routing
        BackupChecks.amount(transaction.movement.amount)
        BackupChecks.ensure(transaction.movement.feeAmount.rupiah in 0..Money.MAX_TRANSACTION_RUPIAH)
        BackupChecks.present(backupIds.walletIds, routing.walletId.value)
        BackupChecks.optional(backupIds.walletIds, routing.targetWalletId?.value)
        BackupChecks.optional(backupIds.categoryIds, routing.categoryId?.value)
        BackupChecks.optional(backupIds.loanIds, routing.loanId?.value)
        BackupChecks.optional(backupIds.inboxItemIds, transaction.origin.inboxItemId?.value)
        if (transaction.movement.type == TransactionType.TRANSFER) {
            BackupChecks.ensure(routing.targetWalletId != null && routing.targetWalletId != routing.walletId)
        }
    }

    private fun validateBudgets(
        ledger: BackupLedger,
        backupIds: BackupIds,
    ) {
        BackupChecks.unique(ledger.budgets.map { it.budgetId.value })
        ledger.budgets.forEach { budget ->
            BackupChecks.ensure(budget.limitAmount.rupiah > 0)
            BackupChecks.optional(backupIds.categoryIds, budget.categoryId?.value)
        }
        ledger.budgetAlertLogs.forEach { BackupChecks.present(backupIds.budgetIds, it.budgetId.value) }
    }
}
