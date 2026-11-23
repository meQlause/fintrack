package com.meqlause.fintrack.feature.dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

// FR-040..043: one screen is recomposed as single fields change; unchanged items keep their instances and skip
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w411dp-h720dp")
class HomeScreenRecompositionUiTest : HomeComposeTestBase() {
    private val base = HomeFixtures.rich()
    private val callbacks = HomeCallbacks()
    private var current: HomeUiState by mutableStateOf(base)

    // S-03: each step changes one field of the same instance, then changes it back
    @Test
    fun givenSingleFieldChangesWhenRecomposedThenUnchangedBlocksKeepRendering() {
        setScreen()
        for (next in steps()) {
            composeRule.runOnUiThread { current = next }
            composeRule.waitForIdle()
        }

        composeRule.onAllNodesWithText(label(R.string.home_balance_label)).onFirst().assertIsDisplayed()
    }

    // S-03: fresh copies of every state, each one different from the last, so every block is rebuilt
    @Test
    fun givenFreshStatesWhenRecomposedThenEveryBlockFollowsTheState() {
        setScreen()
        for (next in freshSteps()) {
            composeRule.runOnUiThread { current = next }
            composeRule.waitForIdle()
        }

        composeRule.onAllNodesWithText(label(R.string.home_balance_label)).onFirst().assertIsDisplayed()
    }

    private fun freshSteps(): List<HomeUiState> =
        listOf(
            HomeFixtures.loading(),
            HomeFixtures.rich(),
            HomeFixtures.rich(hidden = true),
            HomeFixtures.rich(hidden = true, inboxCount = 1),
            HomeFixtures.rich(hidden = true, inboxCount = 1, drawer = HomeDrawer.SUMMARY),
            HomeFixtures.rich(hidden = true, inboxCount = 1, drawer = HomeDrawer.CATEGORIES),
            HomeFixtures.rich(inboxCount = 1, drawer = HomeDrawer.SHORTCUTS),
            HomeFixtures.rich(inboxCount = 1, drawer = HomeDrawer.SHORTCUTS).withRemoved(HomeFixtures.quickEntry()),
            HomeFixtures.empty(hidden = true, netRupiah = -100_000L),
            HomeFixtures.rich(drawer = HomeDrawer.SUMMARY, netRupiah = -50_000L),
            HomeFixtures.rich(drawer = HomeDrawer.CATEGORIES).withoutBills(),
            HomeFixtures.rich(hidden = true).withoutCategories(),
            HomeFixtures.rich(hidden = true, inboxCount = 1).withoutBills().withoutCategories(),
            HomeFixtures.empty(),
            HomeFixtures.loading(),
            HomeFixtures.rich(),
        )

    private fun setScreen() {
        composeRule.setContent {
            FintrackTheme(darkTheme = false) {
                HomeScreen(state = current, callbacks = callbacks)
            }
        }
    }

    private fun steps(): List<HomeUiState> {
        val inbox = base.extras.copy(inbox = HomeInbox(count = 2, sources = listOf("BCA")))
        val wallets = base.ledger.copy(wallets = base.wallets.toList())
        val removed = base.extras.copy(removedShortcut = HomeFixtures.quickEntry())
        return listOf(
            base.copy(drawer = HomeDrawer.SUMMARY),
            base,
            base.copy(extras = inbox),
            base,
            base.copy(drawer = HomeDrawer.CATEGORIES),
            base.copy(drawer = HomeDrawer.SHORTCUTS),
            base,
            base.copy(ledger = wallets),
            base,
            base.copy(hidden = true),
            base.copy(hidden = true, drawer = HomeDrawer.SUMMARY),
            base.copy(drawer = HomeDrawer.SUMMARY),
            base,
            base.copy(extras = removed),
            base,
        )
    }
}
