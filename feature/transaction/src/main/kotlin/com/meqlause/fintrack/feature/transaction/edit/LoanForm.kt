package com.meqlause.fintrack.feature.transaction.edit

import com.meqlause.fintrack.core.model.LoanDirection
import java.time.LocalDate

// FR-150, FR-151, FR-158: what the Pinjaman tab holds beside the shared amount, wallet and note fields
internal data class LoanForm(
    val direction: LoanDirection = LoanDirection.LENT,
    val personName: String = "",
    val dueDate: LocalDate? = null,
)
