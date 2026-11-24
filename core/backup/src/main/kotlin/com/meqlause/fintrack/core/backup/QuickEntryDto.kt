package com.meqlause.fintrack.core.backup

import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.QuickEntry
import com.meqlause.fintrack.core.model.QuickEntryId
import com.meqlause.fintrack.core.model.QuickTemplate
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.WalletId
import kotlinx.serialization.Serializable

// DOC-11: one quick entry row in the backup
@Serializable
internal data class QuickEntryDto(
    val quickEntryId: Long,
    val template: TemplateDto,
    val categoryId: Long,
    val walletId: Long,
    val sortOrder: Int,
    val audit: AuditDto,
) {
    fun toModel(): QuickEntry =
        QuickEntry(
            quickEntryId = QuickEntryId(quickEntryId),
            template = template.toModel(),
            categoryId = CategoryId(categoryId),
            walletId = WalletId(walletId),
            sortOrder = sortOrder,
            audit = audit.toModel(),
        )

    companion object {
        fun from(entry: QuickEntry): QuickEntryDto =
            QuickEntryDto(
                quickEntryId = entry.quickEntryId.value,
                template = TemplateDto.from(entry.template),
                categoryId = entry.categoryId.value,
                walletId = entry.walletId.value,
                sortOrder = entry.sortOrder,
                audit = AuditDto.from(entry.audit),
            )
    }

    @Serializable
    internal data class TemplateDto(
        val title: String,
        val type: String,
        val amount: Long,
        // FR-039: backups written before database version 2 have no note, which reads as empty
        val note: String = "",
    ) {
        fun toModel(): QuickTemplate =
            QuickTemplate(title, BackupValues.enumNamed<TransactionType>(type), Money(amount), note)

        companion object {
            fun from(template: QuickTemplate): TemplateDto =
                TemplateDto(template.title, template.type.name, template.amount.rupiah, template.note)
        }
    }
}
