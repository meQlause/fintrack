package com.meqlause.fintrack.feature.dashboard

import android.content.Context
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.domain.networth.NetWorth
import com.meqlause.fintrack.core.model.Money
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.time.LocalDate

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
        val callbacks = HomeCallbacks(screen = HomeCallbacks.Screen(onToggleHidden = { toggled = true }))
        setHome(emptyState(), callbacks)

        composeRule.onNodeWithContentDescription(label(R.string.home_hide_amounts)).performClick()

        assertTrue(toggled)
    }

    private fun setHome(
        state: HomeUiState.Success,
        callbacks: HomeCallbacks,
    ) {
        composeRule.setContent {
            FintrackTheme(darkTheme = false) {
                HomeScreen(state = state, callbacks = callbacks)
            }
        }
    }

    private fun label(resId: Int): String = ApplicationProvider.getApplicationContext<Context>().getString(resId)

    private fun emptyState(): HomeUiState.Success {
        val zero = Money(0L)
        return HomeUiState.Success(
            hidden = false,
            netWorth = NetWorth(Money(1_000_000L), zero, zero, zero, zero),
            drawer = null,
            ledger =
                HomeUiState.Success.Ledger(
                    total = Money(1_000_000L),
                    wallets = emptyList(),
                    period =
                        HomePeriod(
                            startDate = LocalDate.of(2026, 11, 1),
                            endDate = LocalDate.of(2026, 11, 30),
                            income = zero,
                            expense = zero,
                        ),
                    categories = emptyList(),
                    recent = emptyList(),
                ),
            extras = HomeUiState.Success.Extras(),
        )
    }
}
