package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.ui.test.junit4.v2.createComposeRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w411dp-h720dp")
class IconTileScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    // DOC-08 principle 4: every core component is designed and screenshot-tested in light and dark
    @Test
    fun `given light theme when rendered then the icon tile matches the reference (DOC-08)`() {
        composeRule.captureComponent(name = "IconTile", darkTheme = false) { IconTileSample() }
    }

    @Test
    fun `given dark theme when rendered then the icon tile matches the reference (DOC-08)`() {
        composeRule.captureComponent(name = "IconTile", darkTheme = true) { IconTileSample() }
    }
}
