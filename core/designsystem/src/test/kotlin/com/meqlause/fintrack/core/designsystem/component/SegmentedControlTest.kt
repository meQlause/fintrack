package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class SegmentedControlTest {
    @get:Rule
    val composeRule = createComposeRule()

    // DOC-08 principle 6: 2-4 mutually exclusive modes use a segmented control; the caller owns the selection
    @Test
    fun `given segmented control when a segment is clicked then the selection changes (DOC-08)`() {
        composeRule.setContent {
            FintrackTheme(darkTheme = false) {
                var selectedIndex by remember { mutableIntStateOf(0) }
                SegmentedControl(
                    options = listOf("Keluar", "Masuk", "Transfer"),
                    selectedIndex = selectedIndex,
                    onSelect = { selectedIndex = it },
                )
            }
        }

        composeRule.onNodeWithText("Masuk").performClick()

        composeRule.onNodeWithText("Masuk").assertIsSelected()
    }
}
