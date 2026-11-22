package com.meqlause.fintrack.core.parser

/** FR-085, DOC-07: classifies a notification text as non-transaction before any transaction matching. */
public object IgnoreClassifier {
    private val PATTERNS: Map<IgnoreReason, Regex> =
        mapOf(
            IgnoreReason.OTP to
                keywordRegex(listOf("otp", "kode verifikasi", "kode rahasia", "jangan berikan", "one time password")),
            IgnoreReason.PROMOTION to
                keywordRegex(
                    listOf(
                        "promo",
                        "diskon",
                        "voucher",
                        "cashback hingga",
                        "s.d.",
                        "dapatkan",
                        "gratis ongkir",
                        "hemat",
                    ),
                ),
            IgnoreReason.BALANCE_INFO to keywordRegex(listOf("saldo anda saat ini", "info saldo", "sisa saldo")),
            IgnoreReason.FAILED_TRANSACTION to keywordRegex(listOf("gagal", "ditolak", "dibatalkan", "tidak berhasil")),
            IgnoreReason.PENDING_TRANSACTION to
                keywordRegex(listOf("sedang diproses", "menunggu pembayaran", "belum dibayar")),
            IgnoreReason.LOGIN_ALERT to keywordRegex(listOf("login", "masuk ke akun", "perangkat baru")),
        )

    /** FR-085, DOC-07: the first matching reason in check order, or null when the text may be a transaction. */
    public fun classify(text: String): IgnoreReason? = IgnoreReason.entries.firstOrNull { matches(it, text) }

    private fun matches(
        reason: IgnoreReason,
        text: String,
    ): Boolean {
        if (!PATTERNS.getValue(reason).containsMatchIn(text)) return false
        // DOC-07: a balance phrase is only info when no transaction verb is present
        return reason != IgnoreReason.BALANCE_INFO || DirectionClassifier.classify(text) == null
    }
}
