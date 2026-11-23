package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.domain.repository.InboxRepository
import com.meqlause.fintrack.core.model.InboxItemId
import com.meqlause.fintrack.core.model.InboxStatus
import com.meqlause.fintrack.core.model.Resolution
import java.time.Clock
import java.time.Instant
import javax.inject.Inject

/** FR-087: dismisses one pending item; no transaction is created. */
public class DismissInboxItemUseCase
    @Inject
    constructor(
        private val inbox: InboxRepository,
        private val clock: Clock,
    ) {
        /** FR-087, FR-092: the item stays for the 30-day purge; only a PENDING item can be dismissed. */
        public suspend operator fun invoke(inboxItemId: InboxItemId) {
            val item = inbox.findById(inboxItemId) ?: return
            if (item.resolution.status != InboxStatus.PENDING) return
            inbox.resolve(inboxItemId, Resolution(InboxStatus.DISMISSED, null, Instant.now(clock)))
        }
    }
