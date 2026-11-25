package com.meqlause.fintrack.feature.transaction.detail

import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.Origin
import com.meqlause.fintrack.core.model.Timing
import com.meqlause.fintrack.core.model.Transaction
import com.meqlause.fintrack.core.model.TransactionId
import com.meqlause.fintrack.core.model.TransactionSource
import java.time.Instant

// FR-038: a copy dated now, recorded as a manual entry, with a fresh id (a reused id breaks the insert)
internal fun Transaction.asDuplicateAt(currentTime: Instant): Transaction =
    copy(
        transactionId = TransactionId(0L),
        origin = Origin(source = TransactionSource.MANUAL, inboxItemId = null),
        timing = Timing(occurredAt = currentTime, audit = Audit(createdAt = currentTime, updatedAt = currentTime)),
    )
