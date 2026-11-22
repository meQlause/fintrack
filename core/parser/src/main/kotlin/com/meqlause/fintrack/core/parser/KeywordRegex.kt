package com.meqlause.fintrack.core.parser

// DOC-07: keywords are case-insensitive whole words or phrases, so "terima" does not match inside "diterima"
internal fun keywordRegex(keywords: List<String>): Regex {
    val alternatives = keywords.joinToString("|") { Regex.escape(it) }
    return Regex("(?<!\\p{L})(?:$alternatives)(?!\\p{L})", RegexOption.IGNORE_CASE)
}
