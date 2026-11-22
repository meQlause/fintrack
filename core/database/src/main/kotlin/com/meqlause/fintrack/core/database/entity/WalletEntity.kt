package com.meqlause.fintrack.core.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.meqlause.fintrack.core.model.InvestKind
import com.meqlause.fintrack.core.model.WalletType

/** DOC-02: a wallet; the balance is computed from transactions, never stored (BR-008 guards deletion). */
@Entity(tableName = "wallet")
data class WalletEntity(
    @PrimaryKey(autoGenerate = true) val walletId: Long = 0,
    @Embedded val identity: Identity,
    @Embedded val terms: Terms = Terms(),
    @Embedded val appearance: AppearanceColumns,
    @Embedded val state: State = State(),
    @Embedded val audit: AuditColumns,
) {
    /** DOC-02: name, type and opening balance; credit wallets hold a balance at or below zero. */
    data class Identity(
        val name: String,
        val type: WalletType,
        val initialBalance: Long,
    )

    /** DOC-02: terms that only some wallet types use; all null otherwise. */
    data class Terms(
        val investKind: InvestKind? = null,
        val creditLimit: Long? = null,
        val statementDay: Int? = null,
        val dueDay: Int? = null,
    )

    /** DOC-02: visibility in totals, archive state and manual order. */
    data class State(
        val isIncludedInTotal: Boolean = true,
        val isArchived: Boolean = false,
        val sortOrder: Int = 0,
    )
}
