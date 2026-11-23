package com.meqlause.fintrack.core.backup

// FR-113, DOC-11: RFC 4180 text of a CSV file with CRLF record ends
internal object CsvText {
    /** DOC-11: UTF-8 byte order mark, so Excel reads Indonesian text correctly. */
    const val BOM: String = "﻿"

    private const val SEPARATOR = ","
    private const val LINE_END = "\r\n"
    private const val QUOTE = "\""
    private val specialChars = charArrayOf(',', '"', '\r', '\n')

    /** FR-113: one record of [cells], each escaped, joined by commas and ended with CRLF. */
    fun record(cells: List<String>): String = cells.joinToString(SEPARATOR, postfix = LINE_END) { escape(it) }

    /** FR-113: a cell holding a comma, quote or line break is wrapped in quotes, with inner quotes doubled. */
    fun escape(cell: String): String =
        if (cell.any { it in specialChars }) {
            QUOTE + cell.replace(QUOTE, QUOTE + QUOTE) + QUOTE
        } else {
            cell
        }
}
