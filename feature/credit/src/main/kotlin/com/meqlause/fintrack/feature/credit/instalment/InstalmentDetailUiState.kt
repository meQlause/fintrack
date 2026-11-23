package com.meqlause.fintrack.feature.credit.instalment

// S-16: one plan with its schedule; Missing when the id is unknown, Closed once the plan was deleted
internal sealed interface InstalmentDetailUiState {
    data object Loading : InstalmentDetailUiState

    data object Missing : InstalmentDetailUiState

    data object Closed : InstalmentDetailUiState

    data class Success(
        val row: InstalmentRow,
        val confirmDelete: Boolean,
    ) : InstalmentDetailUiState
}
