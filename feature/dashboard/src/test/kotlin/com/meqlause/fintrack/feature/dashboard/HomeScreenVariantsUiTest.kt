package com.meqlause.fintrack.feature.dashboard

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.WalletId
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

// FR-040, FR-042: wallets and periods at the edges of their ranges, where the shares and signs change
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w411dp-h720dp")
class HomeScreenVariantsUiTest : HomeComposeTestBase() {
    // FR-040: when no wallet holds a positive balance every share is zero
    @Test
    fun givenNoPositiveWalletWhenShownThenSharesAreZero() {
        val wallets =
            listOf(
                HomeWallet(WalletId(2L), "Kartu", Money(-200_000L), isCredit = true),
                HomeWallet(WalletId(3L), "Tunai", Money(0L), appearance = Appearance("cash", "green")),
            )
        setHome(HomeFixtures.rich().withWallets(wallets))

        composeRule.onAllNodesWithText(label(R.string.home_balance_label)).onFirst().assertIsDisplayed()
    }

    // FR-137, FR-042: the Tagihan card can show without any category, and the category card without any bill
    @Test
    fun givenBillsOnlyWhenShownThenCategoryCardIsAbsent() {
        setHome(HomeFixtures.rich().withoutCategories())

        composeRule.onAllNodesWithText(label(R.string.home_bills_title)).onFirst().assertIsDisplayed()
        assertAbsent(label(R.string.home_category_none))
    }

    // FR-042, FR-137: a category card with no bills leaves the Tagihan card out
    @Test
    fun givenCategoriesOnlyWhenShownThenBillsCardIsAbsent() {
        setHome(HomeFixtures.rich().withoutBills())

        assertAbsent(label(R.string.home_bills_title))
        composeRule.onAllNodesWithText(label(R.string.home_balance_label)).onFirst().assertIsDisplayed()
    }

    // FR-040, BR-043: credit wallets stay out of the shares, so the only Saldo wallet holds the whole of it
    @Test
    fun givenCreditBesideCashWhenShownThenShareCountsOnlySaldo() {
        val wallets =
            listOf(
                HomeWallet(WalletId(1L), "Tunai", Money(100_000L)),
                HomeWallet(WalletId(2L), "Kartu", Money(100_000L), isCredit = true),
            )
        setHome(HomeFixtures.rich().withWallets(wallets))

        composeRule.onAllNodesWithText("100%").onFirst().assertIsDisplayed()
        assertAbsent("50%")
    }

    // FR-040: a credit wallet with a positive balance is shown as a credit chip
    @Test
    fun givenPositiveCreditWalletWhenShownThenChipIsRendered() {
        val wallets = listOf(HomeWallet(WalletId(2L), "Kartu", Money(90_000L), isCredit = true))
        setHome(HomeFixtures.rich().withWallets(wallets))

        composeRule.onAllNodesWithText(label(R.string.home_balance_label)).onFirst().assertIsDisplayed()
    }

    // FR-041: a net below zero in the Ringkasan drawer is written with a minus sign
    @Test
    fun givenNegativeNetWhenSummaryOpenThenGapIsShownNegative() {
        setHome(HomeFixtures.empty(drawer = HomeDrawer.SUMMARY, netRupiah = -300_000L))

        composeRule.onAllNodesWithText(label(R.string.home_balance_label)).onFirst().assertIsDisplayed()
    }

    // FR-041: a negative net on a full Beranda with the summary drawer open
    @Test
    fun givenFullStateWithNegativeNetWhenSummaryOpenThenDrawerIsRendered() {
        setHome(HomeFixtures.rich(drawer = HomeDrawer.SUMMARY, netRupiah = -100_000L))

        composeRule.onAllNodesWithText(label(R.string.home_balance_label)).onFirst().assertIsDisplayed()
    }

    // FR-040: a hidden Beranda with a negative net masks the pill amount and keeps the pill
    @Test
    fun givenHiddenFullStateWithNegativeNetWhenShownThenPillIsRendered() {
        setHome(HomeFixtures.rich(hidden = true, netRupiah = -100_000L, drawer = HomeDrawer.CATEGORIES))

        composeRule.onAllNodesWithText(label(R.string.home_balance_label)).onFirst().assertIsDisplayed()
    }
}
