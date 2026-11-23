package com.meqlause.fintrack.feature.dashboard

import android.content.Context
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.Money
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

// FR-040, S-03: the hide toggle of the hero reports its tap; the wallet state is not needed for it
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w411dp-h720dp")
class HomeScreenUiTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun givenAmountsShownWhenToggleTappedThenToggleCallbackFires() {
        var toggled = false
        composeRule.setContent {
            FintrackTheme(darkTheme = false) {
                HomeScreen(
                    state =
                        HomeUiState.Success(
                            hidden = false,
                            total = Money(1_000_000L),
                            wallets = emptyList(),
                            period =
                                HomePeriod(
                                    startDate = java.time.LocalDate.of(2026, 11, 1),
                                    endDate = java.time.LocalDate.of(2026, 11, 30),
                                    income = Money(0L),
                                    expense = Money(0L),
                                ),
                            categories = emptyList(),
                            recent = emptyList(),
                            drawer = null,
                        ),
                    callbacks = HomeCallbacks(onToggleHidden = { toggled = true }),
                )
            }
        }

        val label =
            ApplicationProvider
                .getApplicationContext<Context>()
                .getString(R.string.home_hide_amounts)
        composeRule.onNodeWithContentDescription(label).performClick()

        assertTrue(toggled)
    }
}
