package com.meqlause.fintrack.core.data.mapper

import com.meqlause.fintrack.core.database.entity.QuickEntryEntity
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.QuickEntry
import com.meqlause.fintrack.core.model.QuickEntryId
import com.meqlause.fintrack.core.model.QuickTemplate
import com.meqlause.fintrack.core.model.WalletId

// DOC-02, FR-039: maps a quick entry row to the domain model
internal fun QuickEntryEntity.toQuickEntry(): QuickEntry =
    QuickEntry(
        quickEntryId = QuickEntryId(quickEntryId),
        template = QuickTemplate(title = title, type = target.type, amount = Money(target.amount), note = note),
        categoryId = CategoryId(target.categoryId),
        walletId = WalletId(target.walletId),
        sortOrder = sortOrder,
        audit = audit.toAudit(),
    )

// DOC-02, FR-039: maps a quick entry to its row
internal fun QuickEntry.toEntity(): QuickEntryEntity =
    QuickEntryEntity(
        quickEntryId = quickEntryId.value,
        title = template.title,
        note = template.note,
        sortOrder = sortOrder,
        target =
            QuickEntryEntity.Target(
                type = template.type,
                amount = template.amount.rupiah,
                categoryId = categoryId.value,
                walletId = walletId.value,
            ),
        audit = audit.toColumns(),
    )
