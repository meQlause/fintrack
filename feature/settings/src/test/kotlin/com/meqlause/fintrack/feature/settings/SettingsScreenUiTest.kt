package com.meqlause.fintrack.feature.settings

import android.content.Context
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

// FR-120, S-12: the main interaction of the screen, opening Kategori from the Data section
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w411dp-h720dp")
class SettingsScreenUiTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun givenSettingsScreenWhenCategoriesTappedThenCategoriesCallbackFires() {
        var opened = false
        composeRule.setContent {
            SettingsScreen(
                state = SettingsUiState(),
                callbacks = SettingsCallbacks(links = SettingsLinks(onOpenCategories = { opened = true })),
            )
        }

        val label = ApplicationProvider.getApplicationContext<Context>().getString(R.string.settings_categories_label)
        composeRule.onNodeWithText(label).performClick()

        assertTrue(opened)
    }
}
