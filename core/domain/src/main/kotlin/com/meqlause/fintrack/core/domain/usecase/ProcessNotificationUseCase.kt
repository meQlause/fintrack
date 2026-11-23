package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.domain.detection.DetectedItemFactory
import com.meqlause.fintrack.core.domain.detection.DetectionFingerprint
import com.meqlause.fintrack.core.domain.detection.ProcessNotificationResult
import com.meqlause.fintrack.core.domain.detection.TransferPairFinder
import com.meqlause.fintrack.core.domain.repository.InboxRepository
import com.meqlause.fintrack.core.domain.repository.MonitoredAppRepository
import com.meqlause.fintrack.core.model.InboxItem
import com.meqlause.fintrack.core.model.MonitoredApp
import com.meqlause.fintrack.core.parser.NotificationParserRegistry
import com.meqlause.fintrack.core.parser.ParseResult
import com.meqlause.fintrack.core.parser.ParsedTransaction
import com.meqlause.fintrack.core.parser.RawNotification
import java.time.Instant
import javax.inject.Inject

/** FR-084 … FR-091: the single entry point of the detection pipeline, called by the notification listener. */
public class ProcessNotificationUseCase
    @Inject
    constructor(
        private val parsers: NotificationParserRegistry,
        private val monitoredApps: MonitoredAppRepository,
        private val inbox: InboxRepository,
        private val factory: DetectedItemFactory,
    ) {
        /** FR-084: stores a detected payment as PENDING; other notifications store nothing. */
        public suspend operator fun invoke(
            packageName: String,
            title: String,
            text: String,
            postedAt: Instant,
            bigText: String = "",
        ): ProcessNotificationResult {
            val monitored = monitoredApps.find(packageName)
            if (monitored == null || !monitored.isEnabled) return ProcessNotificationResult.NotMonitored
            val raw = RawNotification(packageName, title, text, bigText, postedAt, notificationKey = "")
            val parsed =
                (parsers.parse(raw) as? ParseResult.Transaction)?.parsed
                    ?: return ProcessNotificationResult.NotTransaction
            return store(raw, parsed, monitored)
        }

        // FR-086: a known fingerprint is a repost; FR-091: a matching pending item becomes the pair partner
        private suspend fun store(
            raw: RawNotification,
            parsed: ParsedTransaction,
            monitored: MonitoredApp,
        ): ProcessNotificationResult {
            val fingerprint = DetectionFingerprint.of(raw, parsed)
            if (inbox.containsFingerprint(fingerprint)) return ProcessNotificationResult.Duplicate
            val draft: InboxItem = factory.build(raw, parsed, monitored, fingerprint)
            val partner = TransferPairFinder.findPartner(draft, inbox.pendingItems())
            val itemId = inbox.insert(draft)
            partner?.let { inbox.linkPair(it.inboxItemId, itemId) }
            monitoredApps.recordNotification(raw.packageName, raw.postedAt)
            return ProcessNotificationResult.Stored(itemId, partner?.inboxItemId)
        }
    }
