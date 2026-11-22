package com.meqlause.fintrack.core.ui.format

import java.time.Clock
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

// DOC-08: day headers read Hari ini, Kemarin or "Sen, 18 Nov"; times read HH:mm (24 h)
object DateFormatter {
    private const val TODAY_LABEL = "Hari ini"
    private const val YESTERDAY_LABEL = "Kemarin"
    private const val TIME_PATTERN = "HH:mm"
    private const val SUNDAY_INDEX = 7
    private val timeFormat = DateTimeFormatter.ofPattern(TIME_PATTERN, Locale.ROOT)
    private val weekdaysId = listOf("Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab")
    private val monthsId = listOf("Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agu", "Sep", "Okt", "Nov", "Des")

    /** DOC-08: relative label against the clock's date, otherwise weekday, day and short month in Indonesian. */
    fun dayLabel(
        day: LocalDate,
        clock: Clock,
    ): String {
        val today = LocalDate.now(clock)
        return when (day) {
            today -> TODAY_LABEL
            today.minusDays(1L) -> YESTERDAY_LABEL
            else -> "${weekdayShort(day)}, ${day.dayOfMonth} ${monthShort(day)}"
        }
    }

    /** DOC-08: a time of day as HH:mm. */
    fun timeLabel(time: LocalTime): String = time.format(timeFormat)

    // ISO weekdays run Monday = 1 to Sunday = 7, so Sunday maps to index 0 of the Indonesian list
    private fun weekdayShort(day: LocalDate): String = weekdaysId[day.dayOfWeek.value % SUNDAY_INDEX]

    private fun monthShort(day: LocalDate): String = monthsId[day.monthValue - 1]
}
