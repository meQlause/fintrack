package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.domain.reminder.BackupReminderRules
import com.meqlause.fintrack.core.domain.repository.SettingsRepository
import com.meqlause.fintrack.core.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.first
import java.time.Instant
import javax.inject.Inject

/** FR-112: decides whether the backup reminder is posted; nothing is stored. */
public class BackupReminderUseCase
    @Inject
    constructor(
        private val settings: SettingsRepository,
        private val transactions: TransactionRepository,
    ) {
        /** FR-112: the reminder length in days when a reminder is due at [currentTime], null when none is due. */
        public suspend operator fun invoke(currentTime: Instant): Int? {
            val stored = settings.observe().first().backup
            val hasTransactions = transactions.observeAll().first().isNotEmpty()
            val due =
                BackupReminderRules.isDue(currentTime, stored.lastBackupAt, hasTransactions, stored.backupReminderDays)
            return stored.backupReminderDays.takeIf { due }
        }
    }
