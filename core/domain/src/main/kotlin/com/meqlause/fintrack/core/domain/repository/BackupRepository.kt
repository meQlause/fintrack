package com.meqlause.fintrack.core.domain.repository

import com.meqlause.fintrack.core.model.BackupSnapshot

/** DOC-11, FR-110, FR-111: encrypted backup export and validated restore. */
public interface BackupRepository {
    /** FR-110: writes an encrypted backup to [fileUri], self-checks it and then stores the backup time. */
    public suspend fun export(
        fileUri: String,
        password: String,
    )

    /** FR-111: decrypts and validates the backup at [fileUri]; nothing in the database changes. */
    public suspend fun readBackup(
        fileUri: String,
        password: String,
    ): BackupSnapshot

    /** FR-111: replaces all backed-up data with [snapshot] and restores the non-secret settings. */
    public suspend fun restore(snapshot: BackupSnapshot)
}
