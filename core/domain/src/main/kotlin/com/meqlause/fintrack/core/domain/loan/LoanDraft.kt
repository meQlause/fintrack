package com.meqlause.fintrack.core.domain.loan

import com.meqlause.fintrack.core.model.LoanDirection
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.WalletId
import java.time.Instant
import java.time.LocalDate

// FR-150, FR-151, FR-158: what the Pinjaman tab holds when a loan is recorded; a null dueDate means no due date
public data class LoanDraft(
    public val direction: LoanDirection,
    public val personName: String,
    public val principal: Money,
    public val walletId: WalletId?,
    public val startDate: Instant,
    public val dueDate: LocalDate?,
    public val note: String,
)
