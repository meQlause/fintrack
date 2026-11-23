package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.domain.repository.AtomicRunner
import com.meqlause.fintrack.core.domain.repository.InboxRepository
import com.meqlause.fintrack.core.model.InboxStatus
import com.meqlause.fintrack.core.model.Resolution
import java.time.Clock
import java.time.Instant
import javax.inject.Inject

/** FR-087, S-07: "Abaikan semua" dismisses every pending item in one atomic step (BR-011). */
public class DismissAllInboxItemsUseCase
    @Inject
    constructor(
        private val inbox: InboxRepository,
        private val atomic: AtomicRunner,
        private val clock: Clock,
    ) {
        /** FR-087: nothing is stored when no item is pending. */
        public suspend operator fun invoke() {
            atomic.atomically {
                val now = Instant.now(clock)
                inbox.pendingItems().forEach { item ->
                    inbox.resolve(item.inboxItemId, Resolution(InboxStatus.DISMISSED, null, now))
                }
            }
        }
    }
