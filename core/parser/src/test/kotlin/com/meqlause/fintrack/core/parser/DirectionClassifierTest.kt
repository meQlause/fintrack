package com.meqlause.fintrack.core.parser

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.meqlause.fintrack.core.model.MoneyDirection
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

/** FR-083, DOC-07: direction keywords decide whether money leaves or enters the wallet (BR-013). */
class DirectionClassifierTest {
    /** DOC-07: every DEBIT keyword of the direction table. */
    @ParameterizedTest(name = "{0} is DEBIT (FR-083)")
    @CsvSource(
        "pembayaran",
        "bayar",
        "membayar",
        "berhasil dibayar",
        "transfer ke",
        "kirim ke",
        "mengirim",
        "tarik tunai",
        "penarikan",
        "debit",
        "didebet",
        "pendebetan",
        "pembelian",
        "belanja",
    )
    fun `given debit keyword when classified then DEBIT is returned (FR-083)`(keyword: String) {
        assertThat(DirectionClassifier.classify("Notifikasi $keyword Rp1.000")).isEqualTo(MoneyDirection.DEBIT)
    }

    /** DOC-07: every CREDIT keyword of the direction table. */
    @ParameterizedTest(name = "{0} is CREDIT (FR-083)")
    @CsvSource(
        "menerima",
        "diterima",
        "terima",
        "transfer dari",
        "kiriman dari",
        "dana masuk",
        "top up berhasil",
        "isi saldo",
        "kredit",
        "dikreditkan",
        "refund",
        "pengembalian dana",
        "cashback diterima",
    )
    fun `given credit keyword when classified then CREDIT is returned (FR-083)`(keyword: String) {
        assertThat(DirectionClassifier.classify("Notifikasi $keyword Rp1.000")).isEqualTo(MoneyDirection.CREDIT)
    }

    /** DOC-07: matching is case-insensitive. */
    @Test
    fun `given upper case keyword when classified then direction is still returned (DOC-07)`() {
        assertThat(DirectionClassifier.classify("PEMBAYARAN QRIS")).isEqualTo(MoneyDirection.DEBIT)
    }

    /** DOC-07: when both directions match, the keyword that appears first decides. */
    @Test
    fun `given debit and credit keywords when classified then the earlier keyword decides (DOC-07)`() {
        assertThat(DirectionClassifier.classify("Pembayaran kartu kredit Rp1.000")).isEqualTo(MoneyDirection.DEBIT)
        assertThat(DirectionClassifier.classify("Refund untuk pembelian Rp1.000")).isEqualTo(MoneyDirection.CREDIT)
    }

    /** FR-083: text with no direction keyword is not classified. */
    @Test
    fun `given text without direction keyword when classified then null is returned (FR-083)`() {
        assertThat(DirectionClassifier.classify("Info Rp1.000")).isNull()
    }

    /** DOC-07: keywords match whole words only, so "terima" does not match inside "diterima" alone. */
    @Test
    fun `given keyword inside another word when classified then it does not match (DOC-07)`() {
        assertThat(DirectionClassifier.classify("Unterima Rp1.000")).isNull()
    }
}
