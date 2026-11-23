package com.meqlause.fintrack.core.parser

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEqualTo
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.MoneyDirection
import com.meqlause.fintrack.core.model.ParsedTransaction
import com.meqlause.fintrack.core.model.RawNotification
import org.junit.jupiter.api.Test
import java.time.Instant

/** FR-083, FR-085: the parser models compare by value and expose their fields. */
class ParserModelsTest {
    private val postedAt: Instant = Instant.parse("2026-11-20T03:15:00Z")
    private val parsed =
        ParsedTransaction(
            amount = Money(25000L),
            direction = MoneyDirection.DEBIT,
            merchant = "INDOMARET",
            merchantKey = "indomaret",
            occurredAt = postedAt,
            parserId = "synthetic.v1",
        )

    /** FR-083: a received notification keeps all six fields and copies by value. */
    @Test
    fun `given received notification when read and copied then fields and equality hold (FR-083)`() {
        val received = RawNotification("pkg.app", "Title", "Text", "Big", postedAt, "key-1")
        assertThat(received.packageName).isEqualTo("pkg.app")
        assertThat(received.bigText).isEqualTo("Big")
        assertThat(received.postedAt).isEqualTo(postedAt)
        assertThat(received.notificationKey).isEqualTo("key-1")
        assertThat(received.copy(text = "Other")).isNotEqualTo(received)
        assertThat(received.hashCode()).isEqualTo(received.copy().hashCode())
        assertThat(received.toString()).contains("key-1")
    }

    /** FR-083: a parsed transaction keeps its fields and compares by value. */
    @Test
    fun `given parsed transaction when read and copied then fields and equality hold (FR-083)`() {
        assertThat(parsed.amount).isEqualTo(Money(25000L))
        assertThat(parsed.direction).isEqualTo(MoneyDirection.DEBIT)
        assertThat(parsed.merchant).isEqualTo("INDOMARET")
        assertThat(parsed.occurredAt).isEqualTo(postedAt)
        assertThat(parsed.parserId).isEqualTo("synthetic.v1")
        assertThat(parsed.copy(merchant = "OTHER")).isNotEqualTo(parsed)
        assertThat(parsed.hashCode()).isEqualTo(parsed.copy().hashCode())
        assertThat(parsed.toString()).contains("INDOMARET")
    }

    /** FR-083, FR-085: parse results compare by value and the unrecognized result is a singleton. */
    @Test
    fun `given parse results when compared then variants keep their payload (FR-083, FR-085)`() {
        val transaction = ParseResult.Transaction(parsed)
        val ignored = ParseResult.Ignored(IgnoreReason.OTP)
        val (inner) = transaction
        val (reason) = ignored

        assertThat(inner).isEqualTo(parsed)
        assertThat(reason).isEqualTo(IgnoreReason.OTP)
        assertThat(transaction.copy()).isEqualTo(transaction)
        assertThat(ignored.copy(reason = IgnoreReason.PROMOTION)).isNotEqualTo(ignored)
        assertThat(ParseResult.Unrecognized).isEqualTo(ParseResult.Unrecognized)
        assertThat(ParseResult.Unrecognized.toString()).contains("Unrecognized")
        assertThat(ignored.hashCode()).isEqualTo(ParseResult.Ignored(IgnoreReason.OTP).hashCode())
    }
}
