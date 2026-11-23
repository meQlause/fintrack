package com.meqlause.fintrack.feature.loan

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val LOAN_DATE: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.forLanguageTag("id"))

// FR-158, S-17: a due date as the Indonesian day, short month and year, e.g. "10 Des 2026"
internal fun LocalDate.loanDateLabel(): String = format(LOAN_DATE)
