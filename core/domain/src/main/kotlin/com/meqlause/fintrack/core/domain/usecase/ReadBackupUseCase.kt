package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.domain.repository.BackupRepository
import com.meqlause.fintrack.core.model.BackupSnapshot
import javax.inject.Inject

/** FR-111: opens and validates a backup before the user confirms the restore. */
public class ReadBackupUseCase
    @Inject
    constructor(
        private val backups: BackupRepository,
    ) {
        /** FR-111: returns the validated content; throws BackupException with the reason otherwise. */
        public suspend operator fun invoke(
            fileUri: String,
            password: String,
        ): BackupSnapshot = backups.readBackup(fileUri, password)
    }
