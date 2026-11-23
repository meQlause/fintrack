package com.meqlause.fintrack.core.domain.inbox

/** FR-088: normalized merchant key; must stay identical to MerchantNormalizer.merchantKey in core:parser. */
public object MerchantKey {
    private const val KEY_MAX_LENGTH = 40
    private val digits = Regex("\\d")
    private val punctuation = Regex("[^\\p{L}\\s]")
    private val whitespaceRun = Regex("\\s+")
    private val legalForms = Regex("(?<!\\p{L})(?:cabang|tbk|pt|cv)(?!\\p{L})", RegexOption.IGNORE_CASE)

    /** FR-088: lowercase key without digits, punctuation or legal forms, at most 40 characters. */
    public fun normalize(merchant: String): String =
        merchant
            .lowercase()
            .replace(digits, "")
            .replace(punctuation, " ")
            .replace(legalForms, "")
            .replace(whitespaceRun, " ")
            .trim()
            .take(KEY_MAX_LENGTH)
            .trim()
}
