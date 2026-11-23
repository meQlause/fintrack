package com.meqlause.fintrack.feature.credit.instalment

// S-16: the instalment list; rows are already split by the chosen filter
internal sealed interface InstalmentListUiState {
    data object Loading : InstalmentListUiState

    data class Success(
        val rows: List<InstalmentRow>,
        val filter: InstalmentFilter,
    ) : InstalmentListUiState
}
