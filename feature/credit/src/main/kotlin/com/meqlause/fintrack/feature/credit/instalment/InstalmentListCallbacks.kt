package com.meqlause.fintrack.feature.credit.instalment

// FR-142, S-16: what the instalment list does when the user goes back, picks a segment or opens a plan
internal data class InstalmentListCallbacks(
    val onBack: () -> Unit,
    val onPickFilter: (InstalmentFilter) -> Unit,
    val onOpenInstalment: (Long) -> Unit,
)
