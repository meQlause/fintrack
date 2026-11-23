package com.meqlause.fintrack.feature.transaction.detail

import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.Origin
import com.meqlause.fintrack.core.model.Timing
import com.meqlause.fintrack.core.model.Transaction
import com.meqlause.fintrack.core.model.TransactionSource
import java.time.Instant

// FR-038: a copy dated now and recorded as a manual entry; the repository assigns the new transactionId
internal fun Transaction.asDuplicateAt(currentTime: Instant): Transaction =
    copy(
        origin = Origin(source = TransactionSource.MANUAL, inboxItemId = null),
        timing = Timing(occurredAt = currentTime, audit = Audit(createdAt = currentTime, updatedAt = currentTime)),
    )
