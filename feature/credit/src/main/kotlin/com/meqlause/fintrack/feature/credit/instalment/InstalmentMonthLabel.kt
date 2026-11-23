package com.meqlause.fintrack.feature.credit.instalment

import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

private val MONTH_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM yyyy", Locale.forLanguageTag("id-ID"))

// S-16, S-14: a statement month in Indonesian, such as "Des 2026"
internal fun YearMonth.label(): String = format(MONTH_FORMAT)
