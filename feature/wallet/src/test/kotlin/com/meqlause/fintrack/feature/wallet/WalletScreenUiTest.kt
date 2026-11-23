package com.meqlause.fintrack.feature.wallet

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

// FR-010, S-09: the main interaction of the Dompet tab, adding a wallet from the empty state
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w411dp-h720dp")
class WalletScreenUiTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun givenEmptyStateWhenAddTappedThenAddCallbackFires() {
        var added = false
        composeRule.setContent {
            WalletScreen(
                state = WalletUiState.Empty(editor = null, picker = null),
                callbacks = WalletCallbacks(onAdd = { added = true }),
            )
        }

        val label = ApplicationProvider.getApplicationContext<Context>().getString(R.string.wallet_add)
        composeRule.onNodeWithText(label).performClick()

        assertTrue(added)
    }
}
