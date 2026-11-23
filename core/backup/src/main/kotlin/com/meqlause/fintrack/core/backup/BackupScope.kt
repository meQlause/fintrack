package com.meqlause.fintrack.core.backup

import com.meqlause.fintrack.core.model.BackupSnapshot
import com.meqlause.fintrack.core.model.InboxItem
import com.meqlause.fintrack.core.model.Transaction

// DOC-11: a backup holds only pending inbox items, so links to inbox items that are not in it are dropped on export
internal object BackupScope {
    fun scope(snapshot: BackupSnapshot): BackupSnapshot {
        val pending =
            snapshot.adjunct.inboxItems
                .map { it.inboxItemId.value }
                .toSet()
        val transactions = snapshot.ledger.transactions.map { scopeTransaction(it, pending) }
        val items = snapshot.adjunct.inboxItems.map { scopeInboxItem(it, pending) }
        return snapshot.copy(
            ledger = snapshot.ledger.copy(transactions = transactions),
            adjunct = snapshot.adjunct.copy(inboxItems = items),
        )
    }

    private fun scopeTransaction(
        transaction: Transaction,
        pending: Set<Long>,
    ): Transaction {
        val link = transaction.origin.inboxItemId ?: return transaction
        if (link.value in pending) return transaction
        return transaction.copy(origin = transaction.origin.copy(inboxItemId = null))
    }

    private fun scopeInboxItem(
        item: InboxItem,
        pending: Set<Long>,
    ): InboxItem {
        val related = item.suggestion.relatedInboxItemId ?: return item
        if (related.value in pending) return item
        return item.copy(suggestion = item.suggestion.copy(relatedInboxItemId = null))
    }
}
