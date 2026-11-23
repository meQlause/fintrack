package com.meqlause.fintrack.feature.dashboard

import com.meqlause.fintrack.core.domain.networth.NetWorth
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.QuickEntry
import com.meqlause.fintrack.core.model.QuickEntryId
import com.meqlause.fintrack.core.model.QuickTemplate
import com.meqlause.fintrack.core.model.StatementStatus
import com.meqlause.fintrack.core.model.TransactionId
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.ui.shortcut.ShortcutTileParams
import java.time.Instant
import java.time.LocalDate

// FR-040..043, FR-137: fixed Beranda states for the Compose tests, one per interesting combination
internal object HomeFixtures {
    private val zero = Money(0L)

    // a full Beranda: every bill status, a credit and a zero-balance wallet, categories, recent rows and shortcuts
    fun rich(
        hidden: Boolean = false,
        drawer: HomeDrawer? = null,
        inboxCount: Int = 0,
        netRupiah: Long = 250_000L,
    ): HomeUiState.Success =
        HomeUiState.Success(
            hidden = hidden,
            netWorth = NetWorth(Money(1_250_000L), zero, Money(100_000L), zero, Money(50_000L)),
            drawer = drawer,
            ledger =
                HomeUiState.Success.Ledger(
                    total = Money(1_250_000L),
                    wallets = wallets(),
                    period = period(netRupiah),
                    categories = categories(),
                    recent = recent(),
                ),
            extras =
                HomeUiState.Success.Extras(
                    bills = HomeBills(bills(), Money(75_000L)),
                    inbox = HomeInbox(count = inboxCount, sources = listOf("bca", "gopay")),
                    shortcuts = shortcuts(),
                ),
        )

    // an empty Beranda: no wallets, rows or bills, with the net given so the summary sign can be tested
    fun empty(
        hidden: Boolean = false,
        drawer: HomeDrawer? = null,
        netRupiah: Long = 0L,
    ): HomeUiState.Success =
        HomeUiState.Success(
            hidden = hidden,
            netWorth = NetWorth(zero, zero, zero, zero, zero),
            drawer = drawer,
            ledger =
                HomeUiState.Success.Ledger(
                    total = zero,
                    wallets = emptyList(),
                    period = period(netRupiah),
                    categories = emptyList(),
                    recent = emptyList(),
                ),
            extras = HomeUiState.Success.Extras(),
        )

    fun loading(): HomeUiState = HomeUiState.Loading

    fun quickEntry(itemKey: Long = 7L): QuickEntry =
        QuickEntry(
            quickEntryId = QuickEntryId(itemKey),
            template = QuickTemplate(title = "Kopi", type = TransactionType.EXPENSE, amount = Money(18_000L)),
            categoryId = CategoryId(1L),
            walletId = WalletId(1L),
            sortOrder = 0,
            audit = Audit(createdAt = Instant.EPOCH, updatedAt = Instant.EPOCH),
        )

    private fun period(netRupiah: Long): HomePeriod =
        HomePeriod(
            startDate = LocalDate.of(2026, 11, 1),
            endDate = LocalDate.of(2026, 11, 30),
            income = Money(netRupiah.coerceAtLeast(0L) + EXPENSE_RUPIAH),
            expense = Money(EXPENSE_RUPIAH + (-netRupiah).coerceAtLeast(0L)),
        )

    private fun wallets(): List<HomeWallet> =
        listOf(
            HomeWallet(WalletId(1L), "BCA", Money(1_000_000L), appearance = Appearance("bank", "blue")),
            HomeWallet(
                WalletId(2L),
                "Kartu",
                Money(-200_000L),
                isCredit = true,
                appearance = Appearance("card", "red"),
            ),
            HomeWallet(WalletId(3L), "Tunai", zero),
        )

    private fun categories(): List<HomeCategoryShare> =
        listOf(
            HomeCategoryShare(name = "Makan", amount = Money(300_000L), count = 3, colorKey = "orange"),
            HomeCategoryShare(name = null, amount = Money(50_000L), count = 1),
        )

    private fun recent(): List<HomeRecent> =
        listOf(
            recentRow(1L, "Kopi", TransactionType.EXPENSE, auto = true),
            recentRow(2L, null, TransactionType.INCOME, auto = false),
            recentRow(3L, "Pindah", TransactionType.TRANSFER, auto = false),
            recentRow(4L, "Pinjam", TransactionType.BORROW, auto = false),
            recentRow(5L, null, TransactionType.LEND, auto = false),
            recentRow(6L, "Tagih", TransactionType.COLLECT, auto = false),
            recentRow(7L, "Bayar", TransactionType.REPAY, auto = false),
            recentRow(8L, null, TransactionType.EXPENSE, auto = false),
            recentRow(9L, null, TransactionType.TRANSFER, auto = false),
        )

    private fun recentRow(
        rowKey: Long,
        title: String?,
        type: TransactionType,
        auto: Boolean,
    ): HomeRecent =
        HomeRecent(
            transactionId = TransactionId(rowKey),
            title = title,
            amount = Money(20_000L),
            type = type,
            stamp = HomeRecent.Stamp(walletName = "BCA", whenLabel = "10.00"),
            style = HomeRecent.Style(appearance = Appearance("food", "orange"), auto = auto),
        )

    private fun bills(): List<HomeBill> =
        listOf(
            bill(1L, StatementStatus.NOT_ISSUED, zero),
            bill(2L, StatementStatus.DUE, Money(250_000L)),
            bill(3L, StatementStatus.PAID, Money(300_000L)),
            bill(4L, StatementStatus.OVERDUE, Money(80_000L)),
        )

    private fun bill(
        itemKey: Long,
        status: StatementStatus,
        amount: Money,
    ): HomeBill =
        HomeBill(
            walletId = WalletId(itemKey),
            name = "Kartu $itemKey",
            status = status,
            amount = amount,
            dateLabel = "5 Des",
        )

    private fun shortcuts(): List<ShortcutTileParams> =
        listOf(
            ShortcutTileParams(QuickEntryId(1L), "Kopi", Money(18_000L), "BCA", Appearance("food", "orange")),
            ShortcutTileParams(QuickEntryId(2L), "Ojek", Money(25_000L), "Tunai"),
        )

    private const val EXPENSE_RUPIAH = 400_000L
}

// the variations below change one block of a fixed Beranda state, so tests read as a single difference
internal fun HomeUiState.Success.withRemoved(entry: QuickEntry?): HomeUiState.Success =
    copy(extras = extras.copy(removedShortcut = entry))

internal fun HomeUiState.Success.withoutBills(): HomeUiState.Success =
    copy(extras = extras.copy(bills = HomeBills(emptyList(), Money(0L))))

internal fun HomeUiState.Success.withoutCategories(): HomeUiState.Success =
    copy(ledger = ledger.copy(categories = emptyList()))

internal fun HomeUiState.Success.withWallets(wallets: List<HomeWallet>): HomeUiState.Success =
    copy(ledger = ledger.copy(wallets = wallets))
