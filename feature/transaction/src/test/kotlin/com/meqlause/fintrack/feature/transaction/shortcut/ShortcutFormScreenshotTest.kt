package com.meqlause.fintrack.feature.transaction.shortcut

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

// FR-039, S-03, DOC-08: screenshot references of the "Pintasan baru" form at the mockup size
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w390dp-h844dp")
class ShortcutFormScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun givenNewShortcutInLightThemeWhenRenderedThenItMatchesReference() {
        composeRule.setContent { ShortcutFormLightPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/ShortcutForm_light.png")
    }

    @Test
    fun givenNewShortcutInDarkThemeWhenRenderedThenItMatchesReference() {
        composeRule.setContent { ShortcutFormDarkPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/ShortcutForm_dark.png")
    }

    @Test
    fun givenEditedShortcutWhenRenderedThenItMatchesReference() {
        composeRule.setContent { ShortcutFormLightPreview(state = shortcutFormEditSample()) }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/ShortcutFormEdit_light.png")
    }
}
