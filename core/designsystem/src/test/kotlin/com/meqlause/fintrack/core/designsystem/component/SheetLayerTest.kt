package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
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
class SheetLayerTest {
    @get:Rule
    val composeRule = createComposeRule()

    private fun show(content: @Composable () -> Unit) {
        val portal = SheetPortal()
        composeRule.setContent {
            FintrackTheme(darkTheme = false) {
                CompositionLocalProvider(LocalSheetPortal provides portal) {
                    content()
                    SheetLayer(portal = portal)
                }
            }
        }
    }

    // DOC-08: a picker opened over a sheet leaves exactly one panel, so one drag handle, on screen
    @Test
    fun `given a drawer over a sheet when both are open then one panel shows (DOC-08)`() {
        show {
            FintrackBottomSheet { Text(text = "Bawah") }
            PickerDrawer(title = "Atas", onClose = {}) { Text(text = "Isi") }
        }
        composeRule.onAllNodesWithTag(SHEET_PANEL_TAG).assertCountEquals(1)
        composeRule.onNodeWithText("Atas").assertExists()
        composeRule.onNodeWithText("Bawah").assertDoesNotExist()
    }

    // DOC-08: a sheet carries exactly one drag handle
    @Test
    fun `given one sheet when it opens then it has a single drag handle (DOC-08)`() {
        show { PickerDrawer(title = "Satu", onClose = {}) { Text(text = "Isi") } }
        composeRule.onAllNodesWithTag(SHEET_HANDLE_TAG, useUnmergedTree = true).assertCountEquals(1)
    }

    // DOC-08: a drawer with a long list is capped below the full screen height
    @Test
    fun `given a long list when the drawer opens then it stays under the maximum height (DOC-08)`() {
        show {
            PickerDrawer(title = "Panjang", onClose = {}) {
                Box(modifier = Modifier.fillMaxWidth().height(2000.dp))
            }
        }
        val panel = composeRule.onNodeWithTag(SHEET_PANEL_TAG).getBoundsInRoot()
        assertTrue(panel.bottom - panel.top <= 540.dp)
    }
}
