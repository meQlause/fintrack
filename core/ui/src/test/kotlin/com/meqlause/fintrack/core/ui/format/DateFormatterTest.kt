package com.meqlause.fintrack.core.ui.format

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.meqlause.fintrack.core.testing.time.FakeClock
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import kotlin.time.Duration.Companion.days

class DateFormatterTest {
    // 2026-11-22 10:00 UTC is 17:00 in Asia/Jakarta, the zone FakeClock uses by default
    private val clock = FakeClock(Instant.parse("2026-11-22T10:00:00Z"))

    // DOC-08: the day header reads Hari ini for the current day
    @Test
    fun `given the current day when labelled then it reads Hari ini (DOC-08)`() {
        assertThat(DateFormatter.dayLabel(LocalDate.of(2026, 11, 22), clock)).isEqualTo("Hari ini")
    }

    // DOC-08: the day before the current day reads Kemarin
    @Test
    fun `given the previous day when labelled then it reads Kemarin (DOC-08)`() {
        assertThat(DateFormatter.dayLabel(LocalDate.of(2026, 11, 21), clock)).isEqualTo("Kemarin")
    }

    // DOC-08: older days read as weekday, day and short month in Indonesian
    @Test
    fun `given an older day when labelled then it reads weekday day and month (DOC-08)`() {
        assertThat(DateFormatter.dayLabel(LocalDate.of(2026, 11, 16), clock)).isEqualTo("Sen, 16 Nov")
        assertThat(DateFormatter.dayLabel(LocalDate.of(2026, 1, 5), clock)).isEqualTo("Sen, 5 Jan")
    }

    // DOC-08: relative labels follow the injected clock, so a day that passes turns Hari ini into Kemarin
    @Test
    fun `given the clock moves past midnight when labelled then the relative label follows it (DOC-08)`() {
        clock.advanceBy(1.days)
        assertThat(DateFormatter.dayLabel(LocalDate.of(2026, 11, 22), clock)).isEqualTo("Kemarin")
    }

    // DOC-08: times read HH:mm on a 24-hour clock
    @Test
    fun `given a time of day when labelled then it reads HH colon mm (DOC-08)`() {
        assertThat(DateFormatter.timeLabel(LocalTime.of(14, 5))).isEqualTo("14:05")
        assertThat(DateFormatter.timeLabel(LocalTime.MIDNIGHT)).isEqualTo("00:00")
    }
}
