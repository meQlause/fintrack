package com.meqlause.fintrack.feature.transaction.edit

// FR-030..FR-032, S-04: the drawers the sheet can open; CATEGORY lists and adds categories;
// WALLET is "Bayar pakai", "Masuk ke" or the transfer source
internal enum class EditSheet {
    WALLET,
    TARGET_WALLET,
    CATEGORY,
    SHORTCUTS,
    SHORTCUT_MANAGE,
    TENOR,
    LOAN_DUE,
}
