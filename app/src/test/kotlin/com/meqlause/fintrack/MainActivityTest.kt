package com.meqlause.fintrack

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MainActivityTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    // DOC-08: the launch screen shows the brand name from strings.xml until the navigation shell (T2.6)
    @Test
    fun `given app launched when shown then the brand name is displayed (DOC-08)`() {
        composeRule.onNodeWithText("Fintrack").assertIsDisplayed()
    }
}
