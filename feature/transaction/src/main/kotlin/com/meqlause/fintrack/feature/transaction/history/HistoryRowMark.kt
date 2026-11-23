package com.meqlause.fintrack.feature.transaction.history

import com.meqlause.fintrack.core.model.Appearance

// FR-036: the icon and colour of a list row and whether it came from a detected notification
internal data class HistoryRowMark(
    val appearance: Appearance = Appearance("other", "gray"),
    val auto: Boolean = false,
)
