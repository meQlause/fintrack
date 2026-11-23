package com.meqlause.fintrack.core.notification

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.meqlause.fintrack.core.domain.usecase.BackupReminderUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.Instant

// FR-112: daily job that posts the backup reminder when a backup is due
@HiltWorker
public class BackupReminderWorker
    @AssistedInject
    constructor(
        @Assisted context: Context,
        @Assisted params: WorkerParameters,
        private val backupReminder: BackupReminderUseCase,
        private val notifier: BackupReminderNotifier,
    ) : CoroutineWorker(context, params) {
        /** FR-112: at most one notification per run; its fixed id makes a repeat replace the earlier one. */
        override suspend fun doWork(): Result {
            backupReminder(Instant.now())?.let(notifier::show)
            return Result.success()
        }
    }
