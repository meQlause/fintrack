package com.meqlause.fintrack.core.notification

import android.content.ComponentName
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.meqlause.fintrack.core.common.coroutines.ApplicationScope
import com.meqlause.fintrack.core.domain.detection.DetectionCounters
import com.meqlause.fintrack.core.domain.detection.ProcessNotificationResult
import com.meqlause.fintrack.core.domain.repository.InboxRepository
import com.meqlause.fintrack.core.domain.repository.MonitoredAppRepository
import com.meqlause.fintrack.core.domain.repository.SettingsRepository
import com.meqlause.fintrack.core.domain.usecase.ProcessDetectedTransactionUseCase
import com.meqlause.fintrack.core.parser.NotificationParserRegistry
import com.meqlause.fintrack.core.parser.ParseResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

// FR-080 … FR-083: reads notifications of enabled monitored apps on-device; nothing is sent anywhere

/** FR-080: reads monitored app notifications on-device and hands them to the inbox pipeline. */
@AndroidEntryPoint
public class FintrackNotificationListener : NotificationListenerService() {
    @Inject public lateinit var monitoredApps: MonitoredAppRepository

    @Inject public lateinit var parsers: NotificationParserRegistry

    @Inject public lateinit var detectTransaction: ProcessDetectedTransactionUseCase

    @Inject public lateinit var counters: DetectionCounters

    @Inject public lateinit var inbox: InboxRepository

    @Inject public lateinit var settings: SettingsRepository

    @Inject public lateinit var notifier: InboxNotifier

    @Inject @ApplicationScope
    public lateinit var appScope: CoroutineScope

    override fun onNotificationPosted(posted: StatusBarNotification) {
        if (posted.isSummaryOrOngoing()) return
        appScope.launch(Dispatchers.Default) { detect(posted) }
    }

    override fun onListenerDisconnected() {
        requestRebind(ComponentName(this, FintrackNotificationListener::class.java))
    }

    // FR-080, DOC-07 step 1: an app that is not monitored and enabled is never read beyond its package name
    private suspend fun detect(posted: StatusBarNotification) {
        val monitored = monitoredApps.find(posted.packageName)
        if (monitored == null || !monitored.isEnabled) return
        val notice = posted.toRawNotification()
        monitoredApps.recordNotification(notice.packageName, notice.postedAt)
        when (val result = parsers.parse(notice)) {
            is ParseResult.Transaction -> promptIfStored(detectTransaction(notice, result.parsed))
            is ParseResult.Ignored -> counters.countIgnored()
            ParseResult.Unrecognized -> counters.countUnrecognized()
        }
    }

    // FR-089: the heads-up prompt is optional and only announces an item that was really stored
    private suspend fun promptIfStored(result: ProcessNotificationResult) {
        if (result !is ProcessNotificationResult.Stored) return
        if (!settings
                .observe()
                .first()
                .preferences.headsUpForInbox
        ) {
            return
        }
        notifier.showConfirmPrompt(inbox.findById(result.inboxItemId) ?: return)
    }
}
