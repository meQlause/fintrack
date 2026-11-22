package com.meqlause.fintrack.core.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.meqlause.fintrack.core.model.InboxStatus
import com.meqlause.fintrack.core.model.MoneyDirection

/** DOC-02: a payment detected from a notification, waiting in the inbox; fingerprint is the dedup key (FR-092). */
@Entity(
    tableName = "inbox_item",
    indices = [Index(value = ["fingerprint"], unique = true)],
)
data class InboxItemEntity(
    @PrimaryKey(autoGenerate = true) val inboxItemId: Long = 0,
    @Embedded val source: Source,
    @Embedded val parsed: Parsed,
    @Embedded val suggestion: Suggestion = Suggestion(),
    @Embedded val lifecycle: Lifecycle,
    @Embedded val audit: AuditColumns,
) {
    /** DOC-02: where the notification came from; raw text is purged 30 days after resolution (FR-092). */
    data class Source(
        val packageName: String,
        val parserId: String,
        val postedAt: Long,
        val rawTitle: String,
        val rawText: String,
    )

    /** DOC-02: the parsed amount, direction and merchant (FR-090). */
    data class Parsed(
        val amount: Long,
        val direction: MoneyDirection,
        val merchant: String = "",
    )

    /** DOC-02: wallet and category proposed from the monitored app and merchant memory. */
    data class Suggestion(
        val suggestedWalletId: Long? = null,
        val suggestedCategoryId: Long? = null,
    )

    /** DOC-02: review state, dedup key, possible pair and the transaction created from it (FR-091). */
    data class Lifecycle(
        val status: InboxStatus,
        val fingerprint: String,
        val relatedInboxItemId: Long? = null,
        val transactionId: Long? = null,
        val resolvedAt: Long? = null,
    )
}
