package com.meqlause.fintrack.feature.dashboard

// DOC-08, S-03: how a figure of the info drawer is tinted
internal enum class HomeTone { NORMAL, INCOME, EXPENSE }

// S-03: one icon row of an info drawer card, every text already formatted
internal data class HomeInfoRow(
    val label: String,
    val sub: String,
    val value: String,
    val tone: HomeTone,
    val iconKey: String,
    val colorKey: String,
)

// S-03: a titled card of rows with its total on the right of the title
internal data class HomeInfoSection(
    val title: String,
    val total: String,
    val tone: HomeTone,
    val rows: List<HomeInfoRow>,
)

// S-03: everything the Ringkasan drawer shows, as plain strings
internal data class HomeInfoContent(
    val title: String,
    val heroLabel: String,
    val heroValue: String,
    val heroNote: String,
    val sections: List<HomeInfoSection>,
    val actionLabel: String,
)
