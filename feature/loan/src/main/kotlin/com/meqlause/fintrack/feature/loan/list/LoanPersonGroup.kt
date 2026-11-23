package com.meqlause.fintrack.feature.loan.list

// FR-153: the open loans of one person, in the order they were recorded
internal data class LoanPersonGroup(
    val personName: String,
    val rows: List<LoanListRow>,
)
