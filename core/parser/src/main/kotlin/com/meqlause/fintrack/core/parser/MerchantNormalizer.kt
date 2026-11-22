package com.meqlause.fintrack.core.parser

/** FR-088, DOC-07: merchant key for merchant memory and the merchant text shown in the Inbox. */
public object MerchantNormalizer {
    private const val KEY_MAX_LENGTH = 40
    private const val DISPLAY_MAX_LENGTH = 60
    private val DIGITS = Regex("\\d")
    private val PUNCTUATION = Regex("[^\\p{L}\\s]")
    private val WHITESPACE_RUN = Regex("\\s+")
    private val LEGAL_FORMS = keywordRegex(listOf("cabang", "tbk", "pt", "cv"))

    /** FR-088, DOC-07: lowercase key without digits, punctuation or legal forms, at most 40 characters. */
    public fun merchantKey(merchant: String): String =
        merchant
            .lowercase()
            .replace(DIGITS, "")
            .replace(PUNCTUATION, " ")
            .replace(LEGAL_FORMS, "")
            .replace(WHITESPACE_RUN, " ")
            .trim()
            .take(KEY_MAX_LENGTH)
            .trim()

    /** DOC-07: the merchant as shown in the Inbox, trimmed, letter case kept, at most 60 characters. */
    public fun displayName(merchant: String): String = merchant.trim().take(DISPLAY_MAX_LENGTH).trim()
}
