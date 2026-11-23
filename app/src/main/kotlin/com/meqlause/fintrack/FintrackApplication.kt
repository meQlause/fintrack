package com.meqlause.fintrack

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.meqlause.fintrack.budget.BudgetAlertWatcher
import com.meqlause.fintrack.core.notification.RawTextPurgeScheduler
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

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(workerFactory).build()

    override fun onCreate() {
        super.onCreate()
        RawTextPurgeScheduler.schedule(this)
        budgetAlertWatcher.start()
    }
}
