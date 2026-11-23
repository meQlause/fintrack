package com.meqlause.fintrack

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.work.Configuration
import com.meqlause.fintrack.budget.BudgetAlertWatcher
import com.meqlause.fintrack.core.notification.RawTextPurgeScheduler
import com.meqlause.fintrack.core.notification.ReminderScheduler
import com.meqlause.fintrack.lock.AppLockGate
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

// NFR-001: application entry point; Hilt root for the whole app graph
@HiltAndroidApp
class FintrackApplication :
    Application(),
    Configuration.Provider {
    // FR-092: WorkManager builds workers through Hilt, so the factory is injected before WorkManager starts
    @Inject lateinit var workerFactory: HiltWorkerFactory

    // FR-073: budget alerts follow every transaction change for the life of the process
    @Inject lateinit var budgetAlertWatcher: BudgetAlertWatcher

    // FR-102: the lock gate follows the app going to the background and back for the life of the process
    @Inject lateinit var appLockGate: AppLockGate

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(workerFactory).build()

    override fun onCreate() {
        super.onCreate()
        RawTextPurgeScheduler.schedule(this)
        ReminderScheduler.schedule(this)
        budgetAlertWatcher.start()
        ProcessLifecycleOwner.get().lifecycle.addObserver(appLockGate)
    }
}
