package com.meqlause.fintrack.feature.transaction.history

// S-05: where Riwayat leads, a tapped row opens its detail and the empty state opens the add sheet
internal data class HistoryLinks(
    val onOpenTransaction: (Long) -> Unit,
    val onAdd: () -> Unit,
)
