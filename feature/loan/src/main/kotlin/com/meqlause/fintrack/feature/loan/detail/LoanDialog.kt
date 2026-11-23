package com.meqlause.fintrack.feature.loan.detail

// FR-155, BR-046: the confirmations the loan detail asks for before it writes off or deletes
internal enum class LoanDialog {
    WRITE_OFF,
    DELETE,
}
