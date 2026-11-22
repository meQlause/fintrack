package com.meqlause.fintrack.core.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.meqlause.fintrack.core.model.TransactionSource
import com.meqlause.fintrack.core.model.TransactionType

/** DOC-02: every money movement; all foreign keys are RESTRICT (BR-008). */
@Entity(
    tableName = "transaction",
    foreignKeys = [
        ForeignKey(
            entity = WalletEntity::class,
            parentColumns = ["walletId"],
            childColumns = ["walletId"],
            onDelete = ForeignKey.RESTRICT,
        ),
        ForeignKey(
            entity = WalletEntity::class,
            parentColumns = ["walletId"],
            childColumns = ["targetWalletId"],
            onDelete = ForeignKey.RESTRICT,
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["categoryId"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.RESTRICT,
        ),
        ForeignKey(
            entity = InboxItemEntity::class,
            parentColumns = ["inboxItemId"],
            childColumns = ["inboxItemId"],
            onDelete = ForeignKey.RESTRICT,
        ),
        ForeignKey(
            entity = LoanEntity::class,
            parentColumns = ["loanId"],
            childColumns = ["loanId"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [
        Index(value = ["occurredAt"]),
        Index(value = ["walletId", "occurredAt"]),
        Index(value = ["targetWalletId"]),
        Index(value = ["categoryId", "occurredAt"]),
        Index(value = ["inboxItemId"]),
        Index(value = ["loanId"]),
    ],
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val transactionId: Long = 0,
    @Embedded val figures: Amounts,
    @Embedded val accounts: Accounts,
    @Embedded val details: Details,
    @Embedded val origin: Origin,
    @Embedded val audit: AuditColumns,
) {
    /** DOC-02: amount in whole rupiah (BR-001) and the transfer fee (BR-002). */
    data class Amounts(
        val amount: Long,
        val feeAmount: Long = 0,
    )

    /** DOC-02: the wallets, category and loan the movement touches (BR-002, BR-006). */
    data class Accounts(
        val walletId: Long,
        val targetWalletId: Long? = null,
        val categoryId: Long? = null,
        val loanId: Long? = null,
    )

    /** DOC-02: what happened, when, and the free text the user or a notification supplied. */
    data class Details(
        val type: TransactionType,
        val note: String = "",
        val merchant: String = "",
        val occurredAt: Long,
    )

    /** DOC-02: whether it was entered manually or created from a notification in the inbox. */
    data class Origin(
        val source: TransactionSource,
        val inboxItemId: Long? = null,
    )
}
