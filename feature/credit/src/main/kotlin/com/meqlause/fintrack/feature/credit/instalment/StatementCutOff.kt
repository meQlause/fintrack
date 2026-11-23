package com.meqlause.fintrack.feature.credit.instalment

import java.time.LocalDate

// BR-035: the statement day of a card and the day the view is taken on
internal data class StatementCutOff(
    val statementDay: Int,
    val today: LocalDate,
)
