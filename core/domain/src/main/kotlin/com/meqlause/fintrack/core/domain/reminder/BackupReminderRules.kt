package com.meqlause.fintrack.core.domain.reminder

import java.time.Duration
import java.time.Instant

/** FR-112: when the backup reminder is due */
public object BackupReminderRules {
    /**
     * FR-112: off when [reminderDays] is 0; otherwise due when data exists without a backup, or the last
     * backup is old enough.
     */
    public fun isDue(
        currentTime: Instant,
        lastBackupAt: Instant?,
        hasTransactions: Boolean,
        reminderDays: Int,
    ): Boolean {
        if (reminderDays <= 0) return false
        if (lastBackupAt == null) return hasTransactions
        return Duration.between(lastBackupAt, currentTime) >= Duration.ofDays(reminderDays.toLong())
    }
}
