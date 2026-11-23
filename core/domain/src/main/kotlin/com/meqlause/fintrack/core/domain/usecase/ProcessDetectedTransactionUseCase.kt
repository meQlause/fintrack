package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.domain.detection.DetectedItemFactory
import com.meqlause.fintrack.core.domain.detection.DetectionFingerprint
import com.meqlause.fintrack.core.domain.detection.ProcessNotificationResult
import com.meqlause.fintrack.core.domain.detection.TransferPairFinder
import com.meqlause.fintrack.core.domain.repository.InboxRepository
import com.meqlause.fintrack.core.domain.repository.MonitoredAppRepository
import com.meqlause.fintrack.core.model.InboxItem
import com.meqlause.fintrack.core.model.MonitoredApp
import com.meqlause.fintrack.core.model.ParsedTransaction
import com.meqlause.fintrack.core.model.RawNotification
import javax.inject.Inject

/** FR-084 … FR-091: the detection pipeline step after parsing, called once per detected transaction. */
public class ProcessDetectedTransactionUseCase
    @Inject
    constructor(
        private val monitoredApps: MonitoredAppRepository,
        private val inbox: InboxRepository,
        private val factory: DetectedItemFactory,
    ) {
        /** FR-084, FR-090: stores a detected payment as PENDING; an app that is not monitored and enabled stores nothing. */
        public suspend operator fun invoke(
            rawNotification: RawNotification,
            parsed: ParsedTransaction,
        ): ProcessNotificationResult {
            val monitored = monitoredApps.find(rawNotification.packageName)
            if (monitored == null || !monitored.isEnabled) return ProcessNotificationResult.NotMonitored
            return store(rawNotification, parsed, monitored)
        }

        // FR-086: a known fingerprint is a repost; FR-091: a matching pending item becomes the pair partner
        private suspend fun store(
            rawNotification: RawNotification,
            parsed: ParsedTransaction,
            monitored: MonitoredApp,
        ): ProcessNotificationResult {
            val fingerprint = DetectionFingerprint.of(rawNotification, parsed)
            if (inbox.containsFingerprint(fingerprint)) return ProcessNotificationResult.Duplicate
            val draft: InboxItem = factory.build(rawNotification, parsed, monitored, fingerprint)
            val partner = TransferPairFinder.findPartner(draft, inbox.pendingItems())
            val itemId = inbox.insert(draft)
            partner?.let { inbox.linkPair(it.inboxItemId, itemId) }
            monitoredApps.recordNotification(rawNotification.packageName, rawNotification.postedAt)
            return ProcessNotificationResult.Stored(itemId, partner?.inboxItemId)
        }
    }
