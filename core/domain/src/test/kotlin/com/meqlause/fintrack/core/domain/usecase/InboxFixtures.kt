package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.InboxItem
import com.meqlause.fintrack.core.model.InboxItemId
import com.meqlause.fintrack.core.model.InboxStatus
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.MoneyDirection
import com.meqlause.fintrack.core.model.ParsedAmount
import com.meqlause.fintrack.core.model.RawNotice
import com.meqlause.fintrack.core.model.Resolution
import com.meqlause.fintrack.core.model.Suggestion
import java.time.Instant

internal val INBOX_NOW: Instant = Instant.parse("2026-11-23T07:05:00Z")

/** FR-087, FR-091: a pending or resolved inbox item for the inbox use case tests; the fake assigns its id. */
internal fun inboxItemOf(
    fingerprint: String,
    direction: MoneyDirection = MoneyDirection.DEBIT,
    merchant: String = "Kopi",
    status: InboxStatus = InboxStatus.PENDING,
): InboxItem =
    InboxItem(
        inboxItemId = InboxItemId(0L),
        notice =
            RawNotice(
                packageName = "com.bca",
                parserId = "bca",
                rawTitle = "Pembayaran",
                rawText = "Rp25.000",
                postedAt = INBOX_NOW,
                fingerprint = fingerprint,
            ),
        parsed = ParsedAmount(Money(25_000L), direction, merchant),
        suggestion = Suggestion(null, null, null),
        resolution = Resolution(status, null, null),
        audit = Audit(INBOX_NOW, INBOX_NOW),
    )
