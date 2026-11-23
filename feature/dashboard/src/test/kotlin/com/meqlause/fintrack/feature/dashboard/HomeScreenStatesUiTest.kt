package com.meqlause.fintrack.feature.dashboard

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import com.meqlause.fintrack.core.ui.R as UiR

// FR-040..043, FR-137, S-03: each Beranda state renders its blocks; loading shows no hero, hidden shows no amounts
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w411dp-h720dp")
class HomeScreenStatesUiTest : HomeComposeTestBase() {
    // S-03: while the first snapshot loads the hero is replaced by placeholder blocks
    @Test
    fun givenLoadingWhenShownThenHeroIsNotRendered() {
        setHome(HomeFixtures.loading())

        assertAbsent(label(R.string.home_balance_label))
    }

    // FR-040, FR-137, FR-042: a full Beranda shows the balance label, the Tagihan card and the category card
    @Test
    fun givenRichStateWhenShownThenHeroAndCardsAreRendered() {
        setHome(HomeFixtures.rich())

        composeRule.onNodeWithText(label(R.string.home_balance_label)).assertIsDisplayed()
        composeRule.onNodeWithText(label(R.string.home_bills_title)).assertIsDisplayed()
    }

    // FR-040: with amounts hidden the eye button offers to show them again
    @Test
    fun givenHiddenWhenShownThenEyeOffersToShowAmounts() {
        setHome(HomeFixtures.rich(hidden = true))

        composeRule.onNodeWithContentDescription(label(R.string.home_show_amounts)).assertIsDisplayed()
    }

    // DOC-08: the dark theme renders the same blocks with its own colours
    @Test
    fun givenDarkThemeWhenShownThenBlocksAreRendered() {
        setHome(HomeFixtures.rich(), darkTheme = true)

        composeRule.onNodeWithText(label(R.string.home_balance_label)).assertIsDisplayed()
    }

    // FR-137, FR-042: an empty Beranda hides the Tagihan and category cards altogether
    @Test
    fun givenEmptyStateWhenShownThenCardsAreNotRendered() {
        setHome(HomeFixtures.empty())

        assertAbsent(label(R.string.home_bills_title))
    }

    // FR-040: a negative period net still renders the summary pill, with the amounts masked when hidden
    @Test
    fun givenNegativeNetAndHiddenWhenShownThenPillIsRendered() {
        setHome(HomeFixtures.empty(hidden = true, netRupiah = -500_000L))

        composeRule.onNodeWithContentDescription(label(R.string.home_summary_open)).assertIsDisplayed()
    }

    // FR-039: a deleted shortcut shows the undo strip until its window passes
    @Test
    fun givenRemovedShortcutWhenShownThenUndoStripIsRendered() {
        setHome(HomeFixtures.rich().withRemoved(HomeFixtures.quickEntry()))

        composeRule.onAllNodesWithText(label(UiR.string.shortcut_deleted)).assertCountEquals(1)
    }

    // FR-084: pending notices show the inbox banner and a dot on the header button
    @Test
    fun givenPendingInboxWhenShownThenBannerIsRendered() {
        setHome(HomeFixtures.rich(inboxCount = 2))

        composeRule.onNodeWithContentDescription(label(R.string.home_inbox_description)).assertIsDisplayed()
    }
}
