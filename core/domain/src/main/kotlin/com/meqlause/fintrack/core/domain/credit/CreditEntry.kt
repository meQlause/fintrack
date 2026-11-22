package com.meqlause.fintrack.core.domain.credit

import com.meqlause.fintrack.core.model.Money
import java.time.LocalDate

/** BR-031, BR-033: one signed movement on a credit wallet; delta is negative for spending, positive for money in. */
public data class CreditEntry(
    public val date: LocalDate,
    public val delta: Money,
    public val isTransferIn: Boolean,
)
