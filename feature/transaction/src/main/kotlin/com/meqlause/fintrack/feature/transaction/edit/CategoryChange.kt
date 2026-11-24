package com.meqlause.fintrack.feature.transaction.edit

// FR-021, S-04: every change the category drawer can make; the sheet sends them wrapped in one event
internal sealed interface CategoryChange {
    data object AddStarted : CategoryChange

    data class NameChanged(
        val name: String,
    ) : CategoryChange

    data class IconChanged(
        val iconKey: String,
    ) : CategoryChange

    data class ColorChanged(
        val colorKey: String,
    ) : CategoryChange

    data object SaveRequested : CategoryChange
}
