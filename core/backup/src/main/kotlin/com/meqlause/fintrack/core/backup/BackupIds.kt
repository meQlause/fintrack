package com.meqlause.fintrack.core.backup

import com.meqlause.fintrack.core.model.BackupSnapshot

// DOC-11: the ids each table holds, so every foreign reference in a backup can be checked before a restore
internal class BackupIds(
    snapshot: BackupSnapshot,
) {
    val walletIds: Set<Long> =
        snapshot.ledger.wallets
            .map { it.walletId.value }
            .toSet()
    val categoryIds: Set<Long> =
        snapshot.ledger.categories
            .map { it.categoryId.value }
            .toSet()
    val transactionIds: Set<Long> =
        snapshot.ledger.transactions
            .map { it.transactionId.value }
            .toSet()
    val budgetIds: Set<Long> =
        snapshot.ledger.budgets
            .map { it.budgetId.value }
            .toSet()
    val loanIds: Set<Long> =
        snapshot.adjunct.loans
            .map { it.loanId.value }
            .toSet()
    val inboxItemIds: Set<Long> =
        snapshot.adjunct.inboxItems
            .map { it.inboxItemId.value }
            .toSet()
}
