package com.meqlause.fintrack.feature.transaction.edit

import com.meqlause.fintrack.core.model.Money

private const val MIN_TENOR = 2
private const val MAX_TENOR = 36
private const val DEFAULT_TENOR = "6"

// FR-140: the Cicilan switch, its tenor and its admin fee as the sheet holds them; digits stay strings like the amount
internal data class InstalmentForm(
    val enabled: Boolean = false,
    val tenorDigits: String = DEFAULT_TENOR,
    val feeDigits: String = "",
) {
    // FR-140: the tenor in months, or null while the typed tenor is outside 2..36
    val tenor: Int?
        get() = tenorDigits.toIntOrNull()?.takeIf { it in MIN_TENOR..MAX_TENOR }

    val feeAmount: Money
        get() = Money(feeDigits.toLongOrNull() ?: 0L)
}
