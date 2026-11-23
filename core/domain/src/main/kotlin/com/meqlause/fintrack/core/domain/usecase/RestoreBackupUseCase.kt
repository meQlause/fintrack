package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.domain.repository.BackupRepository
import com.meqlause.fintrack.core.model.BackupSnapshot
import javax.inject.Inject

/** FR-111: replaces all data with a backup the user has confirmed. */
public class RestoreBackupUseCase
    @Inject
    constructor(
        private val backups: BackupRepository,
    ) {
        /** FR-111: all-or-nothing; the old data stays when the restore fails. */
        public suspend operator fun invoke(snapshot: BackupSnapshot) {
            backups.restore(snapshot)
        }
    }
