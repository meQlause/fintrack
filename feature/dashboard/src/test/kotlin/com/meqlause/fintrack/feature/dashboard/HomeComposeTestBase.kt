package com.meqlause.fintrack.feature.dashboard

import android.content.Context
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.test.core.app.ApplicationProvider
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import org.junit.Rule

// FR-040, S-03: shared Compose set-up for the Beranda screen tests; the theme and the callbacks are the only inputs
abstract class HomeComposeTestBase {
    @get:Rule
    val composeRule = createComposeRule()

    internal fun setHome(
        state: HomeUiState,
        callbacks: HomeCallbacks = HomeCallbacks(),
        darkTheme: Boolean = false,
    ) {
        composeRule.setContent {
            FintrackTheme(darkTheme = darkTheme) {
                HomeScreen(state = state, callbacks = callbacks)
            }
        }
    }

    protected fun label(resId: Int): String = ApplicationProvider.getApplicationContext<Context>().getString(resId)

    // S-03: a node that must not be on screen at all, such as a card with no rows
    protected fun assertAbsent(text: String) {
        composeRule.onAllNodesWithText(text).assertCountEquals(0)
    }
}
