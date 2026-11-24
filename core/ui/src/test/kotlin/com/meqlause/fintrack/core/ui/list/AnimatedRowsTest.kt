package com.meqlause.fintrack.core.ui.list

import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w411dp-h720dp")
class AnimatedRowsTest {
    @get:Rule
    val composeRule = createComposeRule()

    private var rows by mutableStateOf(listOf("Kopi", "Bensin"))

    private fun show() {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            FintrackTheme(darkTheme = false) {
                AnimatedRows(items = rows, keyOf = { it }) { Text(text = it) }
            }
        }
        composeRule.mainClock.advanceTimeBy(SETTLE_MILLIS)
    }

    // DOC-08, FR-039: a removed row is still drawn while it animates out, then it is gone
    @Test
    fun `given a removed row when time passes then it leaves after its exit animation (DOC-08)`() {
        show()
        composeRule.onNodeWithText("Kopi").assertIsDisplayed()

        composeRule.runOnUiThread { rows = listOf("Bensin") }
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.onNodeWithText("Kopi").assertExists()

        composeRule.mainClock.advanceTimeBy(SETTLE_MILLIS)
        composeRule.onNodeWithText("Kopi").assertDoesNotExist()
        composeRule.onNodeWithText("Bensin").assertIsDisplayed()
    }

    // DOC-08, FR-039: Urungkan puts the row back and it animates in again
    @Test
    fun `given a removed row when it is restored then it is shown again (FR-039)`() {
        show()
        composeRule.runOnUiThread { rows = listOf("Bensin") }
        composeRule.mainClock.advanceTimeBy(SETTLE_MILLIS)

        composeRule.runOnUiThread { rows = listOf("Kopi", "Bensin") }
        composeRule.mainClock.advanceTimeBy(SETTLE_MILLIS)

        composeRule.onNodeWithText("Kopi").assertIsDisplayed()
        composeRule.onNodeWithText("Bensin").assertIsDisplayed()
    }

    private companion object {
        const val SETTLE_MILLIS = 1_000L
    }
}
