package com.meqlause.fintrack.core.notification

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.meqlause.fintrack.core.domain.usecase.PurgeResolvedRawTextUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

// FR-092: daily job that clears the raw notification text of resolved inbox items
@HiltWorker
public class RawTextPurgeWorker
    @AssistedInject
    constructor(
        @Assisted context: Context,
        @Assisted params: WorkerParameters,
        private val purgeResolved: PurgeResolvedRawTextUseCase,
    ) : CoroutineWorker(context, params) {
        /** FR-092: runs the purge once per period; parsed data stays in the inbox. */
        override suspend fun doWork(): Result {
            purgeResolved()
            return Result.success()
        }
    }
