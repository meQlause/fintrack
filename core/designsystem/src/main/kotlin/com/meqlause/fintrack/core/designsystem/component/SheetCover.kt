package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue

// DOC-09: counts the open sheets, so the floating bottom bar steps aside while any sheet covers the screen
class SheetCover {
    private var openCount by mutableIntStateOf(0)

    /** DOC-09: true while at least one sheet is on screen. */
    val isCovered: Boolean get() = openCount > 0

    /** DOC-09: a sheet entered the composition. */
    fun opened() {
        openCount += 1
    }

    /** DOC-09: a sheet left the composition. */
    fun closed() {
        openCount -= 1
    }
}

// DOC-09: reports the calling sheet as open for as long as it stays in the composition
@Composable
internal fun ReportSheetOpen() {
    val cover = LocalSheetCover.current
    DisposableEffect(cover) {
        cover.opened()
        onDispose { cover.closed() }
    }
}
