package com.meqlause.fintrack.core.domain.detection

import com.meqlause.fintrack.core.model.ParsedTransaction
import com.meqlause.fintrack.core.model.RawNotification
import java.security.MessageDigest

/** FR-086, DOC-07: the dedup key of a detected payment, so a reposted notification yields the same key. */
public object DetectionFingerprint {
    private const val SECONDS_PER_MINUTE = 60L
    private const val BYTE_MASK = 0xff
    private val DIGITS = Regex("""\d""")
    private val WHITESPACE_RUN = Regex("""\s+""")

    /** FR-086, DOC-07: sha256 of package, amount, direction, normalized text and the posting minute. */
    public fun of(
        raw: RawNotification,
        parsed: ParsedTransaction,
    ): String {
        val minute = raw.postedAt.epochSecond / SECONDS_PER_MINUTE
        val source = listOf(raw.packageName, parsed.amount.rupiah, parsed.direction, normalizedText(raw), minute)
        return sha256(source.joinToString("|"))
    }

    // DOC-07: lowercase, reference digits removed, whitespace collapsed
    private fun normalizedText(raw: RawNotification): String =
        raw.bigText
            .ifEmpty { raw.text }
            .lowercase()
            .replace(DIGITS, "")
            .replace(WHITESPACE_RUN, " ")
            .trim()

    private fun sha256(source: String): String =
        MessageDigest
            .getInstance("SHA-256")
            .digest(source.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it.toInt() and BYTE_MASK) }
}
