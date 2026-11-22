package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage

// DOC-06: one screenshot per component and theme, recorded under src/test/screenshots
internal fun ComposeContentTestRule.captureComponent(
    name: String,
    darkTheme: Boolean,
    content: @Composable () -> Unit,
) {
    setContent { ComponentCanvas(darkTheme = darkTheme, content = content) }
    val theme = if (darkTheme) "dark" else "light"
    onRoot().captureRoboImage("src/test/screenshots/${name}_$theme.png")
}
