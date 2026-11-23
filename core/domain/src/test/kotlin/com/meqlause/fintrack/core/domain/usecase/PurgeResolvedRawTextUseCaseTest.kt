package com.meqlause.fintrack.core.domain.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
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
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset

// FR-092: raw notification text is purged 30 days after an item is resolved; parsed data is kept
class PurgeResolvedRawTextUseCaseTest {
    private val now: Instant = Instant.parse("2026-11-23T00:00:00Z")
    private val inbox = InMemoryInboxRepository()
    private val useCase = PurgeResolvedRawTextUseCase(inbox, Clock.fixed(now, ZoneOffset.UTC))

    @Test
    fun `given a confirmed item resolved 31 days ago when purged then its raw text is cleared (FR-092)`() =
        runBlocking {
            val id = inbox.insert(item("old", InboxStatus.CONFIRMED, now.minus(Duration.ofDays(31))))

            useCase()

            assertRaw(id, title = "", text = "")
        }

    @Test
    fun `given a dismissed item resolved 31 days ago when purged then its raw text is cleared (FR-092)`() =
        runBlocking {
            val id = inbox.insert(item("dismissed", InboxStatus.DISMISSED, now.minus(Duration.ofDays(31))))

            useCase()

            assertRaw(id, title = "", text = "")
            assertThat(
                inbox
                    .findById(id)
                    ?.parsed
                    ?.amount
                    ?.rupiah,
            ).isEqualTo(25_000L)
        }

    @Test
    fun `given an item resolved 29 days ago when purged then its raw text is kept (FR-092)`() =
        runBlocking {
            val id = inbox.insert(item("recent", InboxStatus.CONFIRMED, now.minus(Duration.ofDays(29))))

            useCase()

            assertRaw(id, title = "Pembayaran", text = "Rp25.000")
        }

    @Test
    fun `given a pending item when purged then its raw text is kept (FR-092)`() =
        runBlocking {
            val id = inbox.insert(item("pending", InboxStatus.PENDING, null))

            useCase()

            assertRaw(id, title = "Pembayaran", text = "Rp25.000")
        }

    private suspend fun assertRaw(
        id: InboxItemId,
        title: String,
        text: String,
    ) {
        val notice = inbox.findById(id)?.notice
        assertThat(notice?.rawTitle).isEqualTo(title)
        assertThat(notice?.rawText).isEqualTo(text)
    }

    private fun item(
        fingerprint: String,
        status: InboxStatus,
        resolvedAt: Instant?,
    ): InboxItem =
        InboxItem(
            inboxItemId = InboxItemId(0L),
            notice =
                RawNotice(
                    packageName = "com.bca",
                    parserId = "bca",
                    rawTitle = "Pembayaran",
                    rawText = "Rp25.000",
                    postedAt = now.minus(Duration.ofDays(40)),
                    fingerprint = fingerprint,
                ),
            parsed = ParsedAmount(Money(25_000L), MoneyDirection.DEBIT, "INDOMARET"),
            suggestion = Suggestion(null, null, null),
            resolution = Resolution(status, null, resolvedAt),
            audit = Audit(now, now),
        )
}
