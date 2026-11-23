package com.meqlause.fintrack.feature.transaction.history

// S-05, S-08: one segment of a switch; [value] is what the user picks, [label] is what the segment shows
internal data class HistorySegmentOption<T>(
    val value: T,
    val label: String,
    val iconRes: Int? = null,
)
