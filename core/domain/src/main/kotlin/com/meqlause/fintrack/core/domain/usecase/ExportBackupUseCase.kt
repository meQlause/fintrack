package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.domain.repository.BackupRepository
import javax.inject.Inject

/** FR-110: creates the encrypted backup file at the chosen location. */
public class ExportBackupUseCase
    @Inject
    constructor(
        private val backups: BackupRepository,
    ) {
        /** FR-110: throws BackupException when the password is too short or the file cannot be written. */
        public suspend operator fun invoke(
            fileUri: String,
            password: String,
        ) {
            backups.export(fileUri, password)
        }
    }
