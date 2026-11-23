package com.meqlause.fintrack.feature.transaction.edit

import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.QuickEntry
import com.meqlause.fintrack.core.model.QuickEntryId
import com.meqlause.fintrack.core.model.QuickTemplate
import java.time.Instant

// FR-039, BR-047: a shortcut filled into the draft; every field stays editable and nothing is saved yet
internal fun QuickEntry.toForm(): TransactionForm =
    TransactionForm(
        type = template.type,
        amountDigits = template.amount.rupiah.toString(),
        walletId = walletId,
        categoryId = categoryId,
        note = template.title,
    )

// FR-039: the shortcut the sheet would create from its draft; null when the checkbox is off or does not apply
internal fun EditTransactionUiState.toShortcut(now: Instant): QuickEntry? {
    val categoryId = form.categoryId
    val walletId = form.walletId
    if (!keepAsShortcut || !canKeepShortcut || categoryId == null || walletId == null) return null
    return QuickEntry(
        quickEntryId = QuickEntryId(0L),
        template = QuickTemplate(title = form.note.trim(), type = form.type, amount = form.amount),
        categoryId = categoryId,
        walletId = walletId,
        sortOrder = 0,
        audit = Audit(createdAt = now, updatedAt = now),
    )
}
