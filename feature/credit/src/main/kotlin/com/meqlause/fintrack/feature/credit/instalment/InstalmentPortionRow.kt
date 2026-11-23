package com.meqlause.fintrack.feature.credit.instalment

import com.meqlause.fintrack.core.model.Money
import java.time.YearMonth

// BR-035, BR-036, S-16: one portion of a plan with the statement that bills it and whether that statement has come
internal data class InstalmentPortionRow(
    val number: Int,
    val statementMonth: YearMonth,
    val amount: Money,
    val billed: Boolean,
)
