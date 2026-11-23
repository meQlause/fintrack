package com.meqlause.fintrack.feature.dashboard

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import com.meqlause.fintrack.core.ui.R as UiR

// FR-039, FR-040, FR-042: the three Beranda drawers open over the screen with their own title
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w411dp-h720dp")
class HomeScreenDrawersUiTest : HomeComposeTestBase() {
    // FR-040: the Ringkasan drawer, with the amounts shown
    @Test
    fun givenSummaryDrawerWhenShownThenItsTitleIsDisplayed() {
        setHome(HomeFixtures.rich(drawer = HomeDrawer.SUMMARY))

        composeRule.onAllNodesWithText(label(R.string.home_summary_title)).onFirst().assertIsDisplayed()
    }

    // FR-040: the Ringkasan drawer of a negative period with the amounts hidden
    @Test
    fun givenNegativeSummaryWhenHiddenThenItsTitleIsDisplayed() {
        setHome(HomeFixtures.empty(hidden = true, drawer = HomeDrawer.SUMMARY, netRupiah = -300_000L))

        composeRule.onAllNodesWithText(label(R.string.home_summary_title)).onFirst().assertIsDisplayed()
    }

    // FR-040: the Ringkasan drawer of a negative period with the amounts shown
    @Test
    fun givenNegativeSummaryWhenShownThenItsTitleIsDisplayed() {
        setHome(HomeFixtures.rich(drawer = HomeDrawer.SUMMARY, netRupiah = -100_000L))

        composeRule.onAllNodesWithText(label(R.string.home_summary_title)).onFirst().assertIsDisplayed()
    }

    // FR-042: the Kategori drawer lists the expense shares with the amounts masked
    @Test
    fun givenCategoriesDrawerWhenHiddenThenItsTitleIsDisplayed() {
        setHome(HomeFixtures.rich(drawer = HomeDrawer.CATEGORIES, hidden = true))

        composeRule.onAllNodesWithText(label(R.string.home_categories_title)).onFirst().assertIsDisplayed()
    }

    // FR-042: the Kategori drawer with no expenses in the period
    @Test
    fun givenCategoriesDrawerWhenEmptyThenItsTitleIsDisplayed() {
        setHome(HomeFixtures.empty(drawer = HomeDrawer.CATEGORIES))

        composeRule.onAllNodesWithText(label(R.string.home_categories_title)).onFirst().assertIsDisplayed()
    }

    // FR-039: the shortcut manager lists the catat cepat tiles and offers undo after a delete
    @Test
    fun givenShortcutDrawerWithUndoWhenShownThenItsTitleIsDisplayed() {
        setHome(HomeFixtures.rich(drawer = HomeDrawer.SHORTCUTS).withRemoved(HomeFixtures.quickEntry()))

        composeRule.onAllNodesWithText(label(UiR.string.shortcut_manage_title)).onFirst().assertIsDisplayed()
    }

    // FR-039: the shortcut manager with nothing to show
    @Test
    fun givenShortcutDrawerWhenEmptyThenItsTitleIsDisplayed() {
        setHome(HomeFixtures.empty(drawer = HomeDrawer.SHORTCUTS))

        composeRule.onAllNodesWithText(label(UiR.string.shortcut_manage_title)).onFirst().assertIsDisplayed()
    }
}
