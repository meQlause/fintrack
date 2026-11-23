package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.domain.repository.InboxRepository
import com.meqlause.fintrack.core.model.InboxItemId
import com.meqlause.fintrack.core.model.InboxStatus
import com.meqlause.fintrack.core.model.Resolution
import javax.inject.Inject

/** FR-087: undoes a dismissal so the item is PENDING again. */
public class RestoreInboxItemUseCase
    @Inject
    constructor(
        private val inbox: InboxRepository,
    ) {
        /** FR-087: only a dismissed item is restored; a confirmed item keeps its transaction. */
        public suspend operator fun invoke(inboxItemId: InboxItemId) {
            val item = inbox.findById(inboxItemId) ?: return
            if (item.resolution.status != InboxStatus.DISMISSED) return
            inbox.resolve(inboxItemId, Resolution(InboxStatus.PENDING, transactionId = null, resolvedAt = null))
        }
    }
