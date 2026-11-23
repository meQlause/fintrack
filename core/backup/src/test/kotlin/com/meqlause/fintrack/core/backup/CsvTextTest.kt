package com.meqlause.fintrack.core.backup

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.startsWith
import org.junit.Test

// FR-113, DOC-11: CSV quoting, record ends and byte order mark
class CsvTextTest {
    @Test
    fun givenPlainCellWhenEscapedThenUnchanged() {
        assertThat(CsvText.escape("Makanan & Minuman")).isEqualTo("Makanan & Minuman")
    }

    @Test
    fun givenCommaInCellWhenEscapedThenQuoted() {
        assertThat(CsvText.escape("nasi, ayam")).isEqualTo("\"nasi, ayam\"")
    }

    @Test
    fun givenQuoteInCellWhenEscapedThenDoubled() {
        assertThat(CsvText.escape("kopi \"susu\"")).isEqualTo("\"kopi \"\"susu\"\"\"")
    }

    @Test
    fun givenLineBreakInCellWhenEscapedThenQuoted() {
        assertThat(CsvText.escape("baris 1\nbaris 2")).isEqualTo("\"baris 1\nbaris 2\"")
    }

    @Test
    fun givenCellsWhenRecordedThenJoinedWithCommasAndCrlf() {
        assertThat(CsvText.record(listOf("a", "b,c", "d"))).isEqualTo("a,\"b,c\",d\r\n")
    }

    @Test
    fun givenBomWhenWrittenThenStartsTheFile() {
        assertThat(CsvText.BYTE_ORDER_MARK + CsvText.record(listOf("tanggal"))).startsWith("﻿")
    }
}
