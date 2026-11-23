package com.meqlause.fintrack.feature.transaction.edit

import com.meqlause.fintrack.core.domain.loan.LoanDueOption
import com.meqlause.fintrack.core.model.LoanDirection

// S-04: every change the Pinjaman tab can make; the sheet sends them wrapped in one event
internal sealed interface LoanChange {
    data object TabSelected : LoanChange

    data class DirectionPicked(
        val direction: LoanDirection,
    ) : LoanChange

    data class PersonChanged(
        val name: String,
    ) : LoanChange

    data class DuePicked(
        val option: LoanDueOption,
    ) : LoanChange
}
