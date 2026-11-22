package com.meqlause.fintrack.feature.category

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

// FR-020..023, S-10: screenshot references of the Kategori previews in light and dark themes
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w411dp-h720dp")
class CategoryScreenScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `given success state in light theme when rendered then it matches the reference (S-10)`() {
        composeRule.setContent { CategorySuccessLightPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/CategorySuccess_light.png")
    }

    @Test
    fun `given success state in dark theme when rendered then it matches the reference (S-10)`() {
        composeRule.setContent { CategorySuccessDarkPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/CategorySuccess_dark.png")
    }

    @Test
    fun `given empty state in light theme when rendered then it matches the reference (S-10)`() {
        composeRule.setContent { CategoryEmptyLightPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/CategoryEmpty_light.png")
    }

    @Test
    fun `given empty state in dark theme when rendered then it matches the reference (S-10)`() {
        composeRule.setContent { CategoryEmptyDarkPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/CategoryEmpty_dark.png")
    }
}
