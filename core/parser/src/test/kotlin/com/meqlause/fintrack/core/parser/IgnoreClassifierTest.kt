package com.meqlause.fintrack.core.parser

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

/** FR-085, DOC-07: non-transaction notifications are classified as ignored before matching. */
class IgnoreClassifierTest {
    /** DOC-07: every keyword of the ignore table maps to its reason. */
    @ParameterizedTest(name = "{1} via {0} (FR-085)")
    @CsvSource(
        "otp|OTP",
        "kode verifikasi|OTP",
        "kode rahasia|OTP",
        "jangan berikan|OTP",
        "one time password|OTP",
        "promo|PROMOTION",
        "diskon|PROMOTION",
        "voucher|PROMOTION",
        "cashback hingga|PROMOTION",
        "s.d.|PROMOTION",
        "dapatkan|PROMOTION",
        "gratis ongkir|PROMOTION",
        "hemat|PROMOTION",
        "saldo anda saat ini|BALANCE_INFO",
        "info saldo|BALANCE_INFO",
        "sisa saldo|BALANCE_INFO",
        "gagal|FAILED_TRANSACTION",
        "ditolak|FAILED_TRANSACTION",
        "dibatalkan|FAILED_TRANSACTION",
        "tidak berhasil|FAILED_TRANSACTION",
        "sedang diproses|PENDING_TRANSACTION",
        "menunggu pembayaran|PENDING_TRANSACTION",
        "belum dibayar|PENDING_TRANSACTION",
        "login|LOGIN_ALERT",
        "masuk ke akun|LOGIN_ALERT",
        "perangkat baru|LOGIN_ALERT",
        delimiter = '|',
    )
    fun `given keyword of ignore table when classified then its reason is returned (FR-085)`(
        keyword: String,
        reason: String,
    ) {
        val text = "Pesan: $keyword"

        assertThat(IgnoreClassifier.classify(text)?.name).isEqualTo(reason)
    }

    /** DOC-07: the OTP check runs first of all, before any other ignore keyword. */
    @Test
    fun `given otp and promo keywords together when classified then OTP wins (DOC-07)`() {
        assertThat(IgnoreClassifier.classify("Kode OTP Anda. Promo hemat hari ini")?.name).isEqualTo("OTP")
    }

    /** DOC-07: keywords match whole words only, so a keyword inside another word does not count. */
    @Test
    fun `given keyword inside another word when classified then it is not ignored (DOC-07)`() {
        assertThat(IgnoreClassifier.classify("Hotpot Rp25.000 berhasil")).isNull()
    }

    /** DOC-07: a balance phrase is ignored only when no transaction verb is present. */
    @Test
    fun `given balance phrase with transfer verb when classified then it is not ignored (DOC-07)`() {
        assertThat(IgnoreClassifier.classify("Sisa saldo Rp1.000 setelah transfer ke BCA")).isNull()
    }

    /** FR-085: a plain transaction text has no ignore reason. */
    @Test
    fun `given transaction text when classified then no reason is returned (FR-085)`() {
        assertThat(IgnoreClassifier.classify("Pembayaran QRIS Rp25.000 ke INDOMARET berhasil")).isNull()
    }
}
