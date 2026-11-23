package com.meqlause.fintrack.core.notification

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.meqlause.fintrack.core.domain.reminder.ReminderRules
import java.time.Duration
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

private const val REMINDER_WORK_NAME = "due-reminders"
private const val REMINDER_INTERVAL_DAYS = 1L
private const val BACKUP_REMINDER_WORK_NAME = "backup-reminder"

/** BR-045: schedules the daily reminder job for 09:00; KEEP leaves an existing schedule untouched */
public object ReminderScheduler {
    /** BR-045: the first run is at the next 09:00 local time, then every day. */
    public fun schedule(context: Context) {
        val request =
            PeriodicWorkRequestBuilder<ReminderWorker>(REMINDER_INTERVAL_DAYS, TimeUnit.DAYS)
                .setInitialDelay(delayUntilNextRun(ZonedDateTime.now()))
                .build()
        WorkManager
            .getInstance(context)
            .enqueueUniquePeriodicWork(REMINDER_WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request)
    }

    /** FR-112: schedules the daily backup reminder at the same run hour; KEEP keeps an existing schedule. */
    public fun scheduleBackupReminder(context: Context) {
        val request =
            PeriodicWorkRequestBuilder<BackupReminderWorker>(REMINDER_INTERVAL_DAYS, TimeUnit.DAYS)
                .setInitialDelay(delayUntilNextRun(ZonedDateTime.now()))
                .build()
        WorkManager
            .getInstance(context)
            .enqueueUniquePeriodicWork(BACKUP_REMINDER_WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request)
    }

    private fun delayUntilNextRun(currentTime: ZonedDateTime): Duration {
        val todayRun =
            currentTime
                .withHour(ReminderRules.RUN_HOUR)
                .withMinute(0)
                .withSecond(0)
                .withNano(0)
        val nextRun = if (todayRun.isAfter(currentTime)) todayRun else todayRun.plusDays(1)
        return Duration.between(currentTime, nextRun)
    }
}
