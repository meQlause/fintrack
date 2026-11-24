package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w411dp-h720dp")
class UndoSlotTest {
    @get:Rule
    val composeRule = createComposeRule()

    private var shown by mutableStateOf(false)
    private var undone = 0

    private fun show() {
        composeRule.setContent {
            FintrackTheme(darkTheme = false) {
                UndoSlot(removed = shown.takeIf { it }, message = "Dihapus", onUndo = { undone++ })
                FintrackBarVisibility(visible = shown) { Text(text = "Bar") }
            }
        }
    }

    // DOC-09: the undo bar grows in with the delete and tapping Urungkan reports the undo
    @Test
    fun `given a delete when the bar appears then Urungkan reports the undo (DOC-09)`() {
        show()
        composeRule.onNodeWithText("Dihapus").assertDoesNotExist()

        composeRule.runOnUiThread { shown = true }
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Dihapus").assertIsDisplayed()
        composeRule.onNodeWithText("Urungkan").performClick()

        assertEquals(1, undone)
    }

    // DOC-09: the bar is still drawn while it animates out and gone once the animation ends
    @Test
    fun `given a shown bar when it hides then it animates out before leaving (DOC-09)`() {
        show()
        composeRule.runOnUiThread { shown = true }
        composeRule.waitForIdle()

        composeRule.mainClock.autoAdvance = false
        composeRule.runOnUiThread { shown = false }
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.onNodeWithText("Dihapus").assertExists()
        composeRule.onNodeWithText("Bar").assertExists()

        composeRule.mainClock.autoAdvance = true
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Dihapus").assertDoesNotExist()
        composeRule.onNodeWithText("Bar").assertDoesNotExist()
    }

    // FR-035: every undo bar waits the same short window, not the long default
    @Test
    fun `given the shared undo window then it is a few seconds (FR-035)`() {
        assertTrue(UndoWindow.MILLIS in MIN_WINDOW..MAX_WINDOW)
    }

    private companion object {
        const val MIN_WINDOW = 2_000L
        const val MAX_WINDOW = 4_000L
    }
}
