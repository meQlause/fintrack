package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.runtime.mutableStateListOf

// DOC-08, DOC-09: the sheets the app shell draws over everything, newest last; each is one panel with its dismiss
class SheetPortal {
    private val shown = mutableStateListOf<SheetEntry>()

    /** DOC-09: the open sheets, so the shell can draw them above the bottom bar. */
    val entries: List<SheetEntry> get() = shown

    /** DOC-08: a sheet entered the composition. */
    fun open(entry: SheetEntry) {
        shown.add(entry)
    }

    /** DOC-08: a sheet left the composition. */
    fun close(entry: SheetEntry) {
        shown.remove(entry)
    }
}
