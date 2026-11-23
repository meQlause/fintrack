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

// BR-045: schedules the daily reminder job for 09:00; KEEP leaves an existing schedule untouched
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

    private fun delayUntilNextRun(now: ZonedDateTime): Duration {
        val todayRun =
            now
                .withHour(ReminderRules.RUN_HOUR)
                .withMinute(0)
                .withSecond(0)
                .withNano(0)
        val nextRun = if (todayRun.isAfter(now)) todayRun else todayRun.plusDays(1)
        return Duration.between(now, nextRun)
    }
}
