package com.meqlause.fintrack.core.model

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test
import java.time.Instant

/** FR-083, DOC-07: a notification is captured as received and parsed into an amount, direction and merchant. */
class NotificationModelTest {
    private val postedAt = Instant.parse("2026-11-23T02:05:00Z")

    /** FR-083: a raw notification keeps every field the listener delivered. */
    @Test
    fun `given raw notification when read then every field is kept (FR-083)`() {
        val notice = RawNotification("com.bca", "Transfer", "Rp25.000", "Rp25.000 ke Kopi", postedAt, "key-1")
        assertThat(notice.bigText).isEqualTo("Rp25.000 ke Kopi")
        assertThat(notice.notificationKey).isEqualTo("key-1")
        assertThat(notice.copy(text = "x").text).isEqualTo("x")
    }

    /** DOC-07: a parsed transaction keeps the display merchant and the normalized memory key apart. */
    @Test
    fun `given parsed transaction when read then display merchant and key are kept (DOC-07)`() {
        val parsed =
            ParsedTransaction(Money(25_000L), MoneyDirection.DEBIT, "Kopi Kenangan", "kopikenangan", postedAt, "bca")
        assertThat(parsed.merchant).isEqualTo("Kopi Kenangan")
        assertThat(parsed.merchantKey).isEqualTo("kopikenangan")
        assertThat(parsed.parserId).isEqualTo("bca")
        assertThat(parsed.occurredAt).isEqualTo(postedAt)
    }
}
