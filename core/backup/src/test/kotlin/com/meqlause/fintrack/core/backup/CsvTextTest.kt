package com.meqlause.fintrack.core.backup

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.startsWith
import org.junit.Test

// FR-113, DOC-11: CSV quoting, record ends and byte order mark

/** Csv text tests. */
class CsvTextTest {
    /** Given plain cell when escaped then unchanged. */
    @Test
    fun givenPlainCellWhenEscapedThenUnchanged() {
        assertThat(CsvText.escape("Makanan & Minuman")).isEqualTo("Makanan & Minuman")
    }

    /** Given comma in cell when escaped then quoted. */
    @Test
    fun givenCommaInCellWhenEscapedThenQuoted() {
        assertThat(CsvText.escape("nasi, ayam")).isEqualTo("\"nasi, ayam\"")
    }

    /** Given quote in cell when escaped then doubled. */
    @Test
    fun givenQuoteInCellWhenEscapedThenDoubled() {
        assertThat(CsvText.escape("kopi \"susu\"")).isEqualTo("\"kopi \"\"susu\"\"\"")
    }

    /** Given line break in cell when escaped then quoted. */
    @Test
    fun givenLineBreakInCellWhenEscapedThenQuoted() {
        assertThat(CsvText.escape("baris 1\nbaris 2")).isEqualTo("\"baris 1\nbaris 2\"")
    }

    /** Given cells when recorded then joined with commas and crlf. */
    @Test
    fun givenCellsWhenRecordedThenJoinedWithCommasAndCrlf() {
        assertThat(CsvText.record(listOf("a", "b,c", "d"))).isEqualTo("a,\"b,c\",d\r\n")
    }

    /** Given bom when written then starts the file. */
    @Test
    fun givenBomWhenWrittenThenStartsTheFile() {
        assertThat(CsvText.BYTE_ORDER_MARK + CsvText.record(listOf("tanggal"))).startsWith("")
    }
}
