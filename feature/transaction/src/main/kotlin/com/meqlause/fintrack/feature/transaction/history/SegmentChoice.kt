package com.meqlause.fintrack.feature.transaction.history

// S-05, S-08: the value that is picked now and what to do when another segment is tapped
internal data class SegmentChoice<T>(
    val selected: T,
    val onPick: (T) -> Unit,
)
