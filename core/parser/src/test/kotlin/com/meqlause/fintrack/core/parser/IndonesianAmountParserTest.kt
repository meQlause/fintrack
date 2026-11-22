package com.meqlause.fintrack.core.parser

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource

/** FR-083, DOC-07: the amount table of 07 and the separator rules of the amount algorithm. */
class IndonesianAmountParserTest {
    /** DOC-07: every row of the amount table, plus the cases the algorithm must decide. */
    @ParameterizedTest(name = "{0} -> {1} (FR-083, DOC-07)")
    @CsvSource(
        "Rp25.000|25000",
        "Rp 25.000|25000",
        "Rp25.000,00|25000",
        "Rp 1.250.000,50|1250000",
        "IDR 25,000.00|25000",
        "IDR25000|25000",
        "Rp. 15.500|15500",
        "Rp0|0",
        "Rp15rb|''",
        "Rp25,00|25",
        "Rp 1.25|1",
        "Rp 1.250,000|1250000",
        "Rp25.000.|25000",
        "rp25.000|25000",
        delimiter = '|',
    )
    fun `given amount fragment when parsed then whole rupiah is returned (FR-083)`(
        text: String,
        expected: String,
    ) {
        val amounts = IndonesianAmountParser.findAll(text).map { it.rupiah }

        assertThat(amounts).isEqualTo(if (expected.isEmpty()) emptyList() else listOf(expected.toLong()))
    }

    /** DOC-07: a text with several amounts yields every amount in order; the app parser picks one. */
    @Test
    fun `given text with amount fee and balance when parsed then all amounts are returned in order (DOC-07)`() {
        val amounts = IndonesianAmountParser.findAll("Rp25.000 fee Rp2.500 saldo Rp1.000.000").map { it.rupiah }

        assertThat(amounts).isEqualTo(listOf(25000L, 2500L, 1000000L))
    }

    /** DOC-07: text without a currency marker has no amount. */
    @Test
    fun `given text without currency marker when parsed then no amount is returned (DOC-07)`() {
        assertThat(IndonesianAmountParser.findAll("Pembayaran 25.000 berhasil").isEmpty()).isEqualTo(true)
    }

    /** DOC-07: a currency marker inside another word is not an amount. */
    @Test
    fun `given currency marker inside a word when parsed then no amount is returned (DOC-07)`() {
        assertThat(IndonesianAmountParser.findAll("SURp5000").isEmpty()).isEqualTo(true)
    }

    /** DOC-07: a separator pattern that is neither thousands nor decimal is rejected. */
    @ParameterizedTest(name = "{0} is rejected (DOC-07)")
    @ValueSource(strings = ["Rp2500.000", "Rp1.25.000", "Rp1.250.25", "Rp25.000,5", "Rp1,5", "Rp25,,00"])
    fun `given invalid grouping when parsed then no amount is returned (DOC-07)`(text: String) {
        assertThat(IndonesianAmountParser.findAll(text).isEmpty()).isEqualTo(true)
    }

    /** BR-001: a number too large for a Long is not a valid amount. */
    @Test
    fun `given digits beyond Long range when parsed then no amount is returned (BR-001)`() {
        assertThat(IndonesianAmountParser.findAll("Rp99999999999999999999").isEmpty()).isEqualTo(true)
    }
}
