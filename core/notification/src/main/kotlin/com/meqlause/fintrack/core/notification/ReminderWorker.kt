package com.meqlause.fintrack.core.notification

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.meqlause.fintrack.core.domain.usecase.TodayRemindersUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalDate
import java.time.ZoneId

/** BR-045, BR-055: daily job that posts the bill, loan and investment reminders due today */
@HiltWorker
public class ReminderWorker
    @AssistedInject
    constructor(
        @Assisted context: Context,
        @Assisted params: WorkerParameters,
        private val todayReminders: TodayRemindersUseCase,
        private val notifier: ReminderNotifier,
    ) : CoroutineWorker(context, params) {
        /** BR-045: a repeated run posts the same reminder ids, so one item never shows twice for a day. */
        override suspend fun doWork(): Result {
            val zone = ZoneId.systemDefault()
            todayReminders(LocalDate.now(zone), zone).forEach(notifier::show)
            return Result.success()
        }
    }
