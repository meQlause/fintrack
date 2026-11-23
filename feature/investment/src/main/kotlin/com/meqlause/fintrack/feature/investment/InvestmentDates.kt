package com.meqlause.fintrack.feature.investment

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

// FR-173: the date of a valuation or transfer as the Indonesian day, month and year
internal object InvestmentDates {
    private val formatter = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.forLanguageTag("id-ID"))

    fun shortDate(instant: Instant): String = formatter.format(instant.atZone(ZoneId.systemDefault()))
}
