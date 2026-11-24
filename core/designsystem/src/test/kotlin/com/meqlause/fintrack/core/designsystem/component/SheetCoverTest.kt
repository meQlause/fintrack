package com.meqlause.fintrack.core.designsystem.component

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SheetCoverTest {
    // DOC-09: the bar hides while any sheet is open and returns when the last one closes
    @Test
    fun `given two open sheets when one closes then the cover holds until the last closes (DOC-09)`() {
        val cover = SheetCover()
        assertFalse(cover.isCovered)
        cover.opened()
        cover.opened()
        cover.closed()
        assertTrue(cover.isCovered)
        cover.closed()
        assertFalse(cover.isCovered)
    }
}
