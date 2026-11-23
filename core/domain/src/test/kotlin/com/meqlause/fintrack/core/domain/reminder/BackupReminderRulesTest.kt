package com.meqlause.fintrack.core.domain.reminder

import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import org.junit.jupiter.api.Test
import java.time.Instant

// FR-112: the backup reminder is due only when enabled and a backup is missing or old enough
class BackupReminderRulesTest {
    private val now = Instant.parse("2026-11-23T09:00:00Z")

    @Test
    fun givenRemindersOffWhenDueThenFalse() {
        assertThat(BackupReminderRules.isDue(now, null, true, 0)).isFalse()
    }

    @Test
    fun givenNoBackupAndDataWhenDueThenTrue() {
        assertThat(BackupReminderRules.isDue(now, null, true, 7)).isTrue()
    }

    @Test
    fun givenNoBackupAndNoDataWhenDueThenFalse() {
        assertThat(BackupReminderRules.isDue(now, null, false, 7)).isFalse()
    }

    @Test
    fun givenBackupSixDaysAgoWhenDueThenFalse() {
        assertThat(BackupReminderRules.isDue(now, Instant.parse("2026-11-17T09:00:00Z"), true, 7)).isFalse()
    }

    @Test
    fun givenBackupSevenDaysAgoWhenDueThenTrue() {
        assertThat(BackupReminderRules.isDue(now, Instant.parse("2026-11-16T09:00:00Z"), true, 7)).isTrue()
    }
}
