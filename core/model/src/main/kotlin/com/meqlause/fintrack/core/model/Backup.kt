package com.meqlause.fintrack.core.model

import java.time.Instant

/** DOC-02: backup bookkeeping; backupReminderDays of 0 means reminders are off (DOC-11). */
public data class Backup(
    public val lastBackupAt: Instant? = null,
    public val backupReminderDays: Int = 7,
)
