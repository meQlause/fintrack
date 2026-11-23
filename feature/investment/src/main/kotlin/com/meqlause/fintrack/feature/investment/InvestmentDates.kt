package com.meqlause.fintrack.feature.investment

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

// FR-173: the date of a valuation or transfer as the Indonesian day, month and year
internal object InvestmentDates {
    private val formatter = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.forLanguageTag("id-ID"))

    private val dayMonth = DateTimeFormatter.ofPattern("d MMM", Locale.forLanguageTag("id-ID"))

    // S-08: "20 Nov", the date style of the Investasi lists
    fun dayMonth(instant: Instant): String = dayMonth.format(instant.atZone(ZoneId.systemDefault()))

    fun shortDate(instant: Instant): String = formatter.format(instant.atZone(ZoneId.systemDefault()))
}
