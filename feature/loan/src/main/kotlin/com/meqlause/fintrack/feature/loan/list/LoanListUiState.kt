package com.meqlause.fintrack.feature.loan.list

import com.meqlause.fintrack.core.model.LoanDirection

// FR-153, S-17: the loans of one direction; open ones grouped by person, finished ones in their own section
internal data class LoanListUiState(
    val direction: LoanDirection = LoanDirection.LENT,
    val groups: List<LoanPersonGroup> = emptyList(),
    val settled: List<LoanListRow> = emptyList(),
)
