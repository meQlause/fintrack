package com.meqlause.fintrack.core.database.entity

/** DOC-02: icon and colour keys stored as two columns on the tables that show a visual identity. */
data class AppearanceColumns(
    val iconKey: String,
    val colorKey: String,
)
