package com.meqlause.fintrack.core.model

import java.time.Instant

/** DOC-11: the whole content of one backup file: records, non-secret preferences and the creation time. */
public data class BackupSnapshot(
    public val createdAt: Instant,
    public val ledger: BackupLedger,
    public val adjunct: BackupAdjunct,
    public val preferences: Preferences,
    public val backupReminderDays: Int,
)
