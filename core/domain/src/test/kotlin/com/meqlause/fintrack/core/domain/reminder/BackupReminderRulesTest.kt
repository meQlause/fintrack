package com.meqlause.fintrack.core.domain.reminder

import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import org.junit.jupiter.api.Test
import java.time.Instant

// FR-112: the backup reminder is due only when enabled and a backup is missing or old enough

/** Backup reminder rules tests. */
class BackupReminderRulesTest {
    private val currentTime = Instant.parse("2026-11-23T09:00:00Z")

    /** Given reminders off when due then false. */
    @Test
    fun givenRemindersOffWhenDueThenFalse() {
        assertThat(BackupReminderRules.isDue(currentTime, null, true, 0)).isFalse()
    }

    /** Given no backup and data when due then true. */
    @Test
    fun givenNoBackupAndDataWhenDueThenTrue() {
        assertThat(BackupReminderRules.isDue(currentTime, null, true, 7)).isTrue()
    }

    /** Given no backup and no data when due then false. */
    @Test
    fun givenNoBackupAndNoDataWhenDueThenFalse() {
        assertThat(BackupReminderRules.isDue(currentTime, null, false, 7)).isFalse()
    }

    /** Given backup six days ago when due then false. */
    @Test
    fun givenBackupSixDaysAgoWhenDueThenFalse() {
        assertThat(BackupReminderRules.isDue(currentTime, Instant.parse("2026-11-17T09:00:00Z"), true, 7)).isFalse()
    }

    /** Given backup seven days ago when due then true. */
    @Test
    fun givenBackupSevenDaysAgoWhenDueThenTrue() {
        assertThat(BackupReminderRules.isDue(currentTime, Instant.parse("2026-11-16T09:00:00Z"), true, 7)).isTrue()
    }
}
