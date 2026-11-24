package com.meqlause.fintrack.core.ui.shortcut

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w411dp-h720dp")
class ShortcutManageScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    // FR-039: the shortcut manager drawer in light theme, a card of rows over the dashed "Pintasan baru"
    @Test
    fun `given light theme when rendered then the shortcut manager matches the reference (FR-039)`() {
        composeRule.setContent { ShortcutManageLightPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/ShortcutManage_light.png")
    }

    // FR-039: the shortcut manager drawer in dark theme
    @Test
    fun `given dark theme when rendered then the shortcut manager matches the reference (FR-039)`() {
        composeRule.setContent { ShortcutManageDarkPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/ShortcutManage_dark.png")
    }

    // FR-039: with no shortcuts only the hint and the "Pintasan baru" button show
    @Test
    fun `given no shortcuts when rendered then the empty manager matches the reference (FR-039)`() {
        composeRule.setContent { ShortcutManageEmptyPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/ShortcutManage_empty.png")
    }
}
