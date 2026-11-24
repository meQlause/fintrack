package com.meqlause.fintrack.feature.transaction.edit

import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.QuickEntry
import com.meqlause.fintrack.core.model.QuickEntryId
import com.meqlause.fintrack.core.model.QuickTemplate
import java.time.Instant

// FR-039, BR-047: a shortcut filled into the draft, its note becomes the note; nothing is saved yet
internal fun QuickEntry.toForm(): TransactionForm =
    TransactionForm(
        type = template.type,
        rupiahDigits = template.amount.rupiah.toString(),
        route = FormRoute(walletId = walletId, categoryId = categoryId),
        note = template.note,
    )

// FR-039: the shortcut the sheet would create from its draft; null when the checkbox is off or does not apply
internal fun EditTransactionUiState.toShortcut(currentTime: Instant): QuickEntry? {
    val categoryId = form.categoryId
    val walletId = form.walletId
    if (!keepAsShortcut || !canKeepShortcut) return null
    return if (categoryId == null || walletId == null) {
        null
    } else {
        QuickEntry(
            quickEntryId = QuickEntryId(0L),
            template = QuickTemplate(title = shortcutName, type = form.type, amount = form.amount, note = form.note),
            categoryId = categoryId,
            walletId = walletId,
            sortOrder = 0,
            audit = Audit(createdAt = currentTime, updatedAt = currentTime),
        )
    }
}
