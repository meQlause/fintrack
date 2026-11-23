package com.meqlause.fintrack.feature.transaction.history

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

// FR-036, S-05: the Riwayat list shows its day groups and reports a tapped row by transaction id
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w411dp-h720dp")
class HistoryScreenUiTest {
    @get:Rule
    val composeRule = createComposeRule()

    /** FR-036: a tapped row reports its transaction id so the app can open the edit sheet. */
    @Test
    fun givenSampleListWhenRowTappedThenItsTransactionIdIsReported() {
        val opened = mutableListOf<Long>()
        composeRule.setContent {
            FintrackTheme(darkTheme = false) {
                HistoryScreen(
                    state = sampleHistoryState(),
                    onEvent = {},
                    onOpenTransaction = { opened += it },
                )
            }
        }
        composeRule.onNodeWithText("Warung Bu Tini").performClick()
        assertEquals(listOf(1L), opened)
    }

    /** FR-036: the day header and the transfer row without a merchant are drawn. */
    @Test
    fun givenSampleListWhenRenderedThenDayHeaderAndTransferTitleAreShown() {
        composeRule.setContent {
            FintrackTheme(darkTheme = false) {
                HistoryScreen(state = sampleHistoryState(), onEvent = {}, onOpenTransaction = {})
            }
        }
        composeRule.onNodeWithText("Hari ini").performClick()
        composeRule.onNodeWithText("Transfer").performClick()
    }
}
