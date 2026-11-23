package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.domain.repository.InboxRepository
import java.time.Clock
import java.time.Duration
import java.time.Instant
import javax.inject.Inject

// FR-092: the raw notification text of a resolved inbox item is kept for 30 days only
private const val RAW_TEXT_RETENTION_DAYS = 30L

/** FR-092: clears the raw notification text of inbox items resolved more than 30 days ago. */
public class PurgeResolvedRawTextUseCase
    @Inject
    constructor(
        private val inbox: InboxRepository,
        private val clock: Clock,
    ) {
        /** FR-092: run daily by the purge job; parsed fields, status and transaction links are kept. */
        public suspend operator fun invoke() {
            val cutoff = Instant.now(clock).minus(Duration.ofDays(RAW_TEXT_RETENTION_DAYS))
            inbox.purgeRawTextResolvedBefore(cutoff)
        }
    }
