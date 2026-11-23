package com.meqlause.fintrack.feature.transaction.detail

// FR-034, FR-035, S-06: what the detail sheet can ask of its host: leave, Pintasan, Ubah, Ulangi, Hapus and undo
internal data class TransactionDetailCallbacks(
    val onBack: () -> Unit,
    val onShortcuts: () -> Unit,
    val onEdit: () -> Unit,
    val onDuplicate: () -> Unit,
    val onDelete: () -> Unit,
    val onUndo: () -> Unit,
)
