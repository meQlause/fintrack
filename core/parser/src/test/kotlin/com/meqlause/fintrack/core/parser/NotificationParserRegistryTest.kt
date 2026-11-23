package com.meqlause.fintrack.core.parser

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.MoneyDirection
import com.meqlause.fintrack.core.model.ParsedTransaction
import com.meqlause.fintrack.core.model.RawNotification
import org.junit.jupiter.api.Test
import java.time.Instant

/** FR-083, DOC-07, FR-085: the first parser of a package that recognises a notification decides. */
class NotificationParserRegistryTest {
    private val postedAt = Instant.parse("2026-11-23T02:05:00Z")
    private val notice = RawNotification("com.bca", "Transfer", "Rp25.000", "", postedAt, "key-1")

    /** FR-085: a package without any registered parser is unrecognised. */
    @Test
    fun `given no parser for the package when parsed then it is unrecognized (FR-085)`() {
        val registry =
            NotificationParserRegistry(listOf(FixedParser("gopay.v1", setOf("com.gopay"), ParseResult.Unrecognized)))
        assertThat(registry.parse(notice)).isEqualTo(ParseResult.Unrecognized)
    }

    /** FR-085: an unrecognised first parser is skipped and the next one of the package decides. */
    @Test
    fun `given first parser unrecognized when parsed then the next parser decides (FR-085)`() {
        val ignored = ParseResult.Ignored(IgnoreReason.OTP)
        val registry =
            NotificationParserRegistry(
                listOf(
                    FixedParser("bca.v1", setOf("com.bca"), ParseResult.Unrecognized),
                    FixedParser("bca.v2", setOf("com.bca"), ignored),
                ),
            )
        assertThat(registry.parse(notice)).isEqualTo(ignored)
    }

    /** DOC-07: the first recognising parser wins even when a later one would also match. */
    @Test
    fun `given two recognizing parsers when parsed then the earlier one wins (DOC-07)`() {
        val first =
            ParseResult.Transaction(
                ParsedTransaction(Money(25_000L), MoneyDirection.DEBIT, "Kopi", "kopi", postedAt, "bca.v1"),
            )
        val registry =
            NotificationParserRegistry(
                listOf(
                    FixedParser("bca.v1", setOf("com.bca"), first),
                    FixedParser("bca.v2", setOf("com.bca"), ParseResult.Ignored(IgnoreReason.PROMOTION)),
                ),
            )
        assertThat(registry.parse(notice)).isEqualTo(first)
    }

    /** FR-083: a parser for another package is never asked about this notification. */
    @Test
    fun `given parser for another package when parsed then it is not consulted (FR-083)`() {
        val other = ParseResult.Ignored(IgnoreReason.OTP)
        val registry = NotificationParserRegistry(listOf(FixedParser("gopay.v1", setOf("com.gopay"), other)))
        assertThat(registry.parse(notice)).isEqualTo(ParseResult.Unrecognized)
    }

    private class FixedParser(
        override val parserId: String,
        override val packageNames: Set<String>,
        private val result: ParseResult,
    ) : NotificationParser {
        override fun parse(rawNotification: RawNotification): ParseResult = result
    }
}
