package com.meqlause.fintrack.feature.dashboard

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.meqlause.fintrack.core.domain.bills.BillsCard
import com.meqlause.fintrack.core.domain.networth.NetWorth
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.Category
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.CategoryKind
import com.meqlause.fintrack.core.model.CategoryProfile
import com.meqlause.fintrack.core.model.CategoryState
import com.meqlause.fintrack.core.model.Memo
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Origin
import com.meqlause.fintrack.core.model.Routing
import com.meqlause.fintrack.core.model.Settings
import com.meqlause.fintrack.core.model.Transaction
import com.meqlause.fintrack.core.model.TransactionId
import com.meqlause.fintrack.core.model.TransactionSource
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.Wallet
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.model.WalletKind
import com.meqlause.fintrack.core.model.WalletProfile
import com.meqlause.fintrack.core.model.WalletStatus
import com.meqlause.fintrack.core.model.WalletTerms
import com.meqlause.fintrack.core.model.WalletType
import com.meqlause.fintrack.core.testing.time.FakeClock
import org.junit.Test
import java.time.Instant

// FR-040..043: HomeCalculator derives every Beranda block from one snapshot, without any repository
class HomeCalculatorTest : HomeViewModelTestBase() {
    private val clock = FakeClock(fixedNow)
    private val zero = Money(0L)
    private val worth = NetWorth(Money(1_000_000L), zero, zero, zero, zero)

    // FR-040: archived wallets stay out of the chips; the credit flag follows the wallet type
    @Test
    fun givenWalletTypesWhenCalculatedThenArchivedIsHiddenAndCreditIsFlagged() {
        val snapshot =
            snapshot(
                wallets =
                    listOf(
                        walletOf(1L, "Tunai", WalletType.BANK),
                        walletOf(2L, "Kartu", WalletType.CREDIT_CARD),
                        walletOf(3L, "Paylater", WalletType.PAYLATER),
                        walletOf(4L, "Lama", WalletType.BANK).copy(status = WalletStatus(isArchived = true)),
                    ),
            )
        val wallets = calculate(snapshot).wallets

        assertThat(wallets.map { it.name }).containsExactly("Tunai", "Kartu", "Paylater")
        assertThat(wallets.map { it.isCredit }).containsExactly(false, true, true)
    }

    // FR-040: a wallet without a computed balance shows its initial balance
    @Test
    fun givenWalletWithoutBalanceWhenCalculatedThenInitialBalanceIsShown() {
        val wallet = walletOf(1L, "Tunai", WalletType.BANK).copy(terms = WalletTerms(Money(75_000L), null))
        val snapshot = snapshot(wallets = listOf(wallet))

        assertThat(calculate(snapshot).wallets.single().balance).isEqualTo(Money(75_000L))
    }

    // FR-041: only transactions inside the period count towards income and expense
    @Test
    fun givenRowsOutsidePeriodWhenCalculatedThenTheyAreLeftOut() {
        val earlier = movement(WalletId(1L), TransactionType.EXPENSE, 9_000L, fixedNow.minusSeconds(60L * DAY_SECONDS))
        val today = movement(WalletId(1L), TransactionType.INCOME, 4_000L, fixedNow)
        val later = movement(WalletId(1L), TransactionType.INCOME, 2_000L, fixedNow.plusSeconds(30L * DAY_SECONDS))
        val period = calculate(snapshot(transactions = listOf(earlier, today, later))).period

        assertThat(period.income).isEqualTo(Money(4_000L))
        assertThat(period.expense).isEqualTo(zero)
    }

    // FR-042: expense groups carry their category name and colour; unknown categories fall back to the gray look
    @Test
    fun givenKnownAndUnknownCategoriesWhenCalculatedThenSharesAreNamedOrGray() {
        val food = category(1L, "Makan", "orange")
        val known = expense(1L, 60_000L).copy(routing = routing(categoryId = CategoryId(1L)))
        val unknown = expense(2L, 20_000L).copy(routing = routing(categoryId = CategoryId(9L)))
        val none = expense(3L, 10_000L)
        val shares =
            calculate(
                snapshot(transactions = listOf(known, unknown, none), categories = listOf(food)),
            ).categories

        assertThat(shares.map { it.name }).containsExactly("Makan", null, null)
        assertThat(shares.map { it.colorKey }).containsExactly("orange", "gray", "gray")
    }

    // FR-043: the row title is the merchant, then the note, then the category name, and null when all are blank
    @Test
    fun givenMemoAndCategoryWhenRecentIsBuiltThenTitleFallsBackInOrder() {
        val food = category(1L, "Makan", "orange")
        val merchant = expense(1L, 1L, occurredAt = fixedNow).withMemo(merchant = "Kopi Kenangan", note = "pagi")
        val note = expense(2L, 2L, occurredAt = fixedNow.minusSeconds(60L)).withMemo(note = "siang")
        val byCategory =
            expense(
                3L,
                3L,
                occurredAt = fixedNow.minusSeconds(120L),
            ).copy(routing = routing(CategoryId(1L)))
        val blank = expense(4L, 4L, occurredAt = fixedNow.minusSeconds(180L))
        val titles =
            calculate(snapshot(transactions = listOf(merchant, note, byCategory, blank), categories = listOf(food)))
                .recent
                .map { it.title }

        assertThat(titles).containsExactly("Kopi Kenangan", "siang", "Makan", null)
    }

    // FR-043: a row created from a notification is marked as automatic
    @Test
    fun givenNotificationOriginWhenRecentIsBuiltThenRowIsAutomatic() {
        val auto = expense(1L, 1L).copy(origin = Origin(TransactionSource.NOTIFICATION, null))
        val recent = calculate(snapshot(transactions = listOf(auto))).recent.single()

        assertThat(recent.style.auto).isEqualTo(true)
    }

    // FR-042: a share of nothing is zero percent; otherwise the whole-number percent
    @Test
    fun givenWholeOfZeroWhenPercentIsComputedThenItIsZero() {
        assertThat(HomeCalculator.percentOf(Money(5L), zero)).isEqualTo(0)
        assertThat(HomeCalculator.percentOf(Money(1L), Money(3L))).isEqualTo(33)
    }

    // FR-040: no bills and no categories produce empty lists rather than failures
    @Test
    fun givenEmptySnapshotWhenCalculatedThenBlocksAreEmpty() {
        val state = calculate(snapshot())

        assertThat(state.total).isEqualTo(Money(1_000_000L))
        assertThat(state.categories.isEmpty()).isEqualTo(true)
        assertThat(state.recent.isEmpty()).isEqualTo(true)
        assertThat(state.drawer).isNull()
    }

    private fun calculate(snapshot: HomeSnapshot): HomeUiState.Success =
        HomeCalculator.success(snapshot, BillsCard(emptyList(), zero), worth, clock)

    private fun snapshot(
        wallets: List<Wallet> = emptyList(),
        transactions: List<Transaction> = emptyList(),
        categories: List<Category> = emptyList(),
    ): HomeSnapshot =
        HomeSnapshot(
            wallets = wallets,
            moneyByWallet = emptyMap(),
            transactions = transactions,
            categories = categories,
            settings = Settings(),
        )

    private fun walletOf(
        itemKey: Long,
        name: String,
        type: WalletType,
    ): Wallet =
        Wallet(
            walletId = WalletId(itemKey),
            kind = WalletKind(type = type, investKind = null),
            terms = WalletTerms(initialBalance = Money(0L), credit = null),
            profile = WalletProfile(name = name, appearance = Appearance("bank", "blue"), sortOrder = 0),
            status = WalletStatus(),
            audit = Audit(createdAt = fixedNow, updatedAt = fixedNow),
        )

    private fun category(
        itemKey: Long,
        name: String,
        colorKey: String,
    ): Category =
        Category(
            categoryId = CategoryId(itemKey),
            profile =
                CategoryProfile(
                    name = name,
                    kind = CategoryKind.EXPENSE,
                    appearance = Appearance("food", colorKey),
                    sortOrder = 0,
                ),
            state = CategoryState(isDefault = true, isArchived = false),
            audit = Audit(createdAt = fixedNow, updatedAt = fixedNow),
        )

    private fun expense(
        itemKey: Long,
        rupiah: Long,
        occurredAt: Instant = fixedNow,
    ): Transaction =
        movement(WalletId(1L), TransactionType.EXPENSE, rupiah, occurredAt).copy(
            transactionId = TransactionId(itemKey),
        )

    private fun routing(categoryId: CategoryId?) =
        Routing(
            walletId = WalletId(1L),
            targetWalletId = null,
            categoryId = categoryId,
            loanId = null,
        )

    private fun Transaction.withMemo(
        merchant: String = "",
        note: String = "",
    ): Transaction = copy(memo = Memo(note = note, merchant = merchant))

    private companion object {
        const val DAY_SECONDS = 86_400L
    }
}
