package com.meqlause.fintrack.feature.loan.navigation

import kotlinx.serialization.Serializable

// FR-155, S-17: the detail route of one loan, identified by its loan id
@Serializable
public data class LoanDetailDestination(
    public val loanId: Long,
)
