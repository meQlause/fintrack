package com.meqlause.fintrack.core.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.meqlause.fintrack.core.model.LoanDirection
import com.meqlause.fintrack.core.model.LoanStatus

/** DOC-02: money lent to or borrowed from a person; its movements are transactions with this loanId (ADR-016). */
@Entity(tableName = "loan")
data class LoanEntity(
    @PrimaryKey(autoGenerate = true) val loanId: Long = 0,
    @Embedded val terms: Terms,
    val status: LoanStatus,
    val closedAt: Long? = null,
    @Embedded val audit: AuditColumns,
) {
    /** DOC-02: the agreement made at opening; direction is immutable (FR-150). */
    data class Terms(
        val direction: LoanDirection,
        val personName: String,
        val principal: Long,
        val startDate: Long,
        val dueDate: Long? = null,
        val note: String = "",
    )
}
