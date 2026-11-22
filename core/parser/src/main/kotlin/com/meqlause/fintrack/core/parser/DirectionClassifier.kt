package com.meqlause.fintrack.core.parser

import com.meqlause.fintrack.core.model.MoneyDirection

/** FR-083, DOC-07, BR-013: decides whether a notification text means money out or money in. */
public object DirectionClassifier {
    private const val NO_MATCH = Int.MAX_VALUE
    private val DEBIT_PATTERN =
        keywordRegex(
            listOf(
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
            ),
        )
    private val CREDIT_PATTERN =
        keywordRegex(
            listOf(
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
            ),
        )

    /** FR-083, DOC-07: DEBIT or CREDIT from the keywords; when both match, the keyword appearing first decides. */
    public fun classify(text: String): MoneyDirection? {
        val debitAt = firstMatchIndex(DEBIT_PATTERN, text)
        val creditAt = firstMatchIndex(CREDIT_PATTERN, text)
        return when {
            debitAt == NO_MATCH && creditAt == NO_MATCH -> null
            debitAt < creditAt -> MoneyDirection.DEBIT
            else -> MoneyDirection.CREDIT
        }
    }

    private fun firstMatchIndex(
        pattern: Regex,
        text: String,
    ): Int {
        val match = pattern.find(text) ?: return NO_MATCH
        return match.range.first
    }
}
