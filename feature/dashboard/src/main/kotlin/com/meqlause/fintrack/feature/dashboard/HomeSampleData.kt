package com.meqlause.fintrack.feature.dashboard

import com.meqlause.fintrack.core.domain.networth.NetWorth
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.QuickEntryId
import com.meqlause.fintrack.core.model.StatementStatus
import com.meqlause.fintrack.core.model.TransactionId
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.ui.shortcut.ShortcutTileParams
import java.time.LocalDate

// S-03: the mockup seed of 2026-11-22 that the previews and the screenshot tests render
internal fun sampleSuccess(
    hidden: Boolean = false,
    recent: List<HomeRecent> = sampleRecent(),
    drawer: HomeDrawer? = null,
): HomeUiState.Success =
    HomeUiState.Success(
        hidden = hidden,
        netWorth = sampleNetWorth(),
        drawer = drawer,
        ledger =
            HomeUiState.Success.Ledger(
                total = Money(rupiah = 12_412_207L),
                wallets = sampleWallets(),
                period = samplePeriod(),
                categories = sampleCategories(),
                recent = recent,
            ),
        extras =
            HomeUiState.Success.Extras(
                bills = sampleBills(),
                inbox = HomeInbox(count = 4, sources = listOf("BCA", "GoPay", "DANA")),
                shortcuts = sampleShortcuts(),
            ),
    )

private fun sampleBills(): HomeBills =
    HomeBills(
        bills =
            listOf(
                HomeBill(
                    walletId = WalletId(value = 6L),
                    name = "BCA Card",
                    status = StatementStatus.DUE,
                    amount = Money(rupiah = 1_300_000L),
                    dateLabel = "1 Des · 9 hari lagi",
                ),
                HomeBill(
                    walletId = WalletId(value = 7L),
                    name = "SPayLater",
                    status = StatementStatus.PAID,
                    amount = Money(rupiah = 200_000L),
                    dateLabel = "25 Nov",
                    appearance = Appearance("clock", "orange"),
                ),
            ),
        instalmentsDue = Money(rupiah = 200_000L),
    )

private fun samplePeriod(): HomePeriod =
    HomePeriod(
        startDate = LocalDate.parse("2026-11-01"),
        endDate = LocalDate.parse("2026-11-30"),
        income = Money(rupiah = 9_700_000L),
        expense = Money(rupiah = 8_214_500L),
    )

private fun sampleCategories(): List<HomeCategoryShare> =
    listOf(
        HomeCategoryShare("Belanja", Money(rupiah = 4_190_000L), count = 12, colorKey = "pink"),
        HomeCategoryShare("Tagihan", Money(rupiah = 2_300_000L), count = 8, colorKey = "purple"),
        HomeCategoryShare("Makanan", Money(rupiah = 900_000L), count = 5, colorKey = "orange"),
        HomeCategoryShare("Transport", Money(rupiah = 400_000L), count = 4, colorKey = "blue"),
        HomeCategoryShare("Hiburan", Money(rupiah = 224_500L), count = 2, colorKey = "violet"),
    )

private val PARKIR =
    ShortcutTileParams(
        quickEntryId = QuickEntryId(value = 1L),
        title = "Parkir",
        amount = Money(rupiah = 4_500L),
        walletName = "Tunai",
        appearance = Appearance("transport", "blue"),
    )

private val KOPI =
    ShortcutTileParams(
        quickEntryId = QuickEntryId(value = 2L),
        title = "Kopi",
        amount = Money(rupiah = 25_000L),
        walletName = "GoPay",
        appearance = Appearance("food", "orange"),
    )

private val OJEK =
    ShortcutTileParams(
        quickEntryId = QuickEntryId(value = 3L),
        title = "Ojek",
        amount = Money(rupiah = 18_000L),
        walletName = "GoPay",
        appearance = Appearance("transport", "blue"),
    )

private val MAKAN_SIANG =
    ShortcutTileParams(
        quickEntryId = QuickEntryId(value = 4L),
        title = "Makan siang",
        amount = Money(rupiah = 35_000L),
        walletName = "BCA",
        appearance = Appearance("food", "orange"),
    )

private fun sampleShortcuts(): List<ShortcutTileParams> = listOf(PARKIR, KOPI, OJEK, MAKAN_SIANG)

private fun sampleWallets(): List<HomeWallet> =
    listOf(
        wallet(id = 1L, name = "Tunai", balance = 223_000L, appearance = Appearance("cash", "green")),
        wallet(id = 2L, name = "BCA", balance = 10_000_000L, appearance = Appearance("bank", "blue")),
        wallet(id = 3L, name = "GoPay", balance = 222_000L, appearance = Appearance("ewallet", "teal")),
        wallet(id = 4L, name = "DANA", balance = 300_000L, appearance = Appearance("ewallet", "indigo")),
        wallet(id = 5L, name = "Tabungan", balance = 2_800_000L, appearance = Appearance("bank", "amber")),
    )

private fun wallet(
    id: Long,
    name: String,
    balance: Long,
    appearance: Appearance,
): HomeWallet =
    HomeWallet(
        walletId = WalletId(value = id),
        name = name,
        balance = Money(rupiah = balance),
        appearance = appearance,
    )

private val FOOD = Appearance("food", "orange")

private val RECENT_LUNCH =
    HomeRecent(
        transactionId = TransactionId(value = 1L),
        title = "Makan siang",
        amount = Money(rupiah = 45_000L),
        type = TransactionType.EXPENSE,
        stamp = HomeRecent.Stamp(walletName = "BCA", whenLabel = "12:30"),
        style = HomeRecent.Style(appearance = FOOD, auto = true),
    )

private val RECENT_COFFEE =
    HomeRecent(
        transactionId = TransactionId(value = 2L),
        title = "Kopi pagi",
        amount = Money(rupiah = 28_000L),
        type = TransactionType.EXPENSE,
        stamp = HomeRecent.Stamp(walletName = "GoPay", whenLabel = "08:10"),
        style = HomeRecent.Style(appearance = FOOD, auto = true),
    )

private val RECENT_GROCERY =
    HomeRecent(
        transactionId = TransactionId(value = 3L),
        title = "Belanja bulanan",
        amount = Money(rupiah = 186_500L),
        type = TransactionType.EXPENSE,
        stamp = HomeRecent.Stamp(walletName = "BCA", whenLabel = "19:45"),
        style = HomeRecent.Style(appearance = Appearance("shopping", "pink"), auto = true),
    )

private fun sampleRecent(): List<HomeRecent> = listOf(RECENT_LUNCH, RECENT_COFFEE, RECENT_GROCERY)

// FR-040, S-03: the Ringkasan sample; the parts add up to the Saldo shown in the hero
private fun sampleNetWorth(): NetWorth =
    NetWorth(
        saldo = Money(rupiah = 12_450_000L),
        investasi = Money(rupiah = 3_000_000L),
        piutang = Money(rupiah = 200_000L),
        utangKartu = Money(rupiah = 3_500_000L),
        hutang = Money(rupiah = 500_000L),
    )
