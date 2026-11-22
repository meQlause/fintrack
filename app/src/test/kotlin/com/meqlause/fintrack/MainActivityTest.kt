package com.meqlause.fintrack

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MainActivityTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    // DOC-09: the app opens on Beranda; tapping each bottom-bar tab shows that tab's header title
    @Test
    fun `given app launched when each tab is tapped then its title is shown (DOC-09)`() {
        composeRule.onNodeWithText("Fintrack").assertIsDisplayed()

        composeRule.onNodeWithContentDescription("Riwayat").performClick()
        composeRule.onNodeWithText("Riwayat").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Riwayat").assertIsSelected()

        composeRule.onNodeWithContentDescription("Investasi").performClick()
        composeRule.onNodeWithText("Investasi").assertIsDisplayed()

        composeRule.onNodeWithContentDescription("Dompet").performClick()
        composeRule.onNodeWithText("Dompet").assertIsDisplayed()

        composeRule.onNodeWithContentDescription("Beranda").performClick()
        composeRule.onNodeWithText("Fintrack").assertIsDisplayed()
    }
}
