package com.meqlause.fintrack.core.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.meqlause.fintrack.core.model.InstallmentStatus

/** DOC-02: a purchase paid in portions on a credit wallet (FR-140); portion amounts are computed, not stored. */
@Entity(
    tableName = "installment_plan",
    foreignKeys = [
        ForeignKey(
            entity = TransactionEntity::class,
            parentColumns = ["transactionId"],
            childColumns = ["transactionId"],
            onDelete = ForeignKey.RESTRICT,
        ),
        ForeignKey(
            entity = WalletEntity::class,
            parentColumns = ["walletId"],
            childColumns = ["walletId"],
            onDelete = ForeignKey.RESTRICT,
        ),
        ForeignKey(
            entity = TransactionEntity::class,
            parentColumns = ["transactionId"],
            childColumns = ["adminFeeTransactionId"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [
        Index(value = ["transactionId"], unique = true),
        Index(value = ["walletId"]),
        Index(value = ["adminFeeTransactionId"]),
    ],
)
data class InstallmentPlanEntity(
    @PrimaryKey(autoGenerate = true) val installmentPlanId: Long = 0,
    @Embedded val purchase: Purchase,
    @Embedded val schedule: Schedule,
    val adminFeeTransactionId: Long? = null,
    val status: InstallmentStatus,
    @Embedded val audit: AuditColumns,
) {
    /** DOC-02: the purchase transaction this plan splits, its wallet, title and principal (FR-140). */
    data class Purchase(
        val transactionId: Long,
        val walletId: Long,
        val title: String,
        val principal: Long,
    )

    /** DOC-02: tenor and statement months; earlyPayoffMonth is set when the plan is paid off early (BR-037). */
    data class Schedule(
        val tenorMonths: Int,
        val firstStatementMonth: String,
        val earlyPayoffMonth: String? = null,
    )
}
