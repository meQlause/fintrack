package com.meqlause.fintrack.core.notification

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

private const val PURGE_WORK_NAME = "raw-text-purge"
private const val PURGE_INTERVAL_DAYS = 1L

/** FR-092: schedules the daily raw text purge; KEEP leaves an existing schedule untouched */
public object RawTextPurgeScheduler {
    /** FR-092: enqueues the daily purge once; calling it again at every app start changes nothing. */
    public fun schedule(context: Context) {
        val request =
            PeriodicWorkRequestBuilder<RawTextPurgeWorker>(PURGE_INTERVAL_DAYS, TimeUnit.DAYS).build()
        WorkManager
            .getInstance(context)
            .enqueueUniquePeriodicWork(PURGE_WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request)
    }
}
