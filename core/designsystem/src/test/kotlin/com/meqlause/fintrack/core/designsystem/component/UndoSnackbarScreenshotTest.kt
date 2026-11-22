package com.meqlause.fintrack.core.designsystem.component

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
class UndoSnackbarScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    // DOC-08 principle 4: every core component is designed and screenshot-tested in light and dark
    @Test
    fun `given light theme when rendered then the undo snackbar matches the reference (DOC-08)`() {
        composeRule.setContent { UndoSnackbarLightPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/UndoSnackbar_light.png")
    }

    @Test
    fun `given dark theme when rendered then the undo snackbar matches the reference (DOC-08)`() {
        composeRule.setContent { UndoSnackbarDarkPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/UndoSnackbar_dark.png")
    }
}
