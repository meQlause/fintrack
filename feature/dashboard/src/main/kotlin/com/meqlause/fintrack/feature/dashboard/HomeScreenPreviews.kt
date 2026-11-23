package com.meqlause.fintrack.feature.dashboard

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
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

// S-03: previews of every state and drawer, light and dark; the sample is the mockup seed of 2026-11-22
internal fun sampleSuccess(
    hidden: Boolean = false,
    recent: List<HomeRecent> = sampleRecent(),
    drawer: HomeDrawer? = null,
): HomeUiState.Success =
    HomeUiState.Success(
        hidden = hidden,
        total = Money(12_412_207L),
        netWorth = sampleNetWorth(),
        bills =
            HomeBills(
                bills =
                    listOf(
                        HomeBill(
                            WalletId(6L),
                            "BCA Card",
                            StatementStatus.DUE,
                            Money(1_300_000L),
                            "1 Des · 9 hari lagi",
                        ),
                        HomeBill(
                            WalletId(7L),
                            "SPayLater",
                            StatementStatus.PAID,
                            Money(200_000L),
                            "25 Nov",
                            Appearance("clock", "orange"),
                        ),
                    ),
                instalmentsDue = Money(200_000L),
            ),
        wallets = sampleWallets(),
        period =
            HomePeriod(
                startDate = LocalDate.of(2026, 11, 1),
                endDate = LocalDate.of(2026, 11, 30),
                income = Money(9_700_000L),
                expense = Money(8_214_500L),
            ),
        categories =
            listOf(
                HomeCategoryShare("Belanja", Money(4_190_000L), 12, "pink"),
                HomeCategoryShare("Tagihan", Money(2_300_000L), 8, "purple"),
                HomeCategoryShare("Makanan", Money(900_000L), 5, "orange"),
                HomeCategoryShare("Transport", Money(400_000L), 4, "blue"),
                HomeCategoryShare("Hiburan", Money(224_500L), 2, "violet"),
            ),
        recent = recent,
        drawer = drawer,
        shortcuts = sampleShortcuts(),
    )

private fun sampleShortcuts(): List<ShortcutTileParams> =
    listOf(
        ShortcutTileParams(QuickEntryId(1L), "Parkir", Money(4_500L), "Tunai", Appearance("transport", "blue")),
        ShortcutTileParams(QuickEntryId(2L), "Kopi", Money(25_000L), "GoPay", Appearance("food", "orange")),
        ShortcutTileParams(QuickEntryId(3L), "Ojek", Money(18_000L), "GoPay", Appearance("transport", "blue")),
        ShortcutTileParams(QuickEntryId(4L), "Makan siang", Money(35_000L), "BCA", Appearance("food", "orange")),
    )

private fun sampleWallets(): List<HomeWallet> =
    listOf(
        HomeWallet(WalletId(1L), "Tunai", Money(223_000L), appearance = Appearance("cash", "green")),
        HomeWallet(WalletId(2L), "BCA", Money(10_000_000L), appearance = Appearance("bank", "blue")),
        HomeWallet(WalletId(3L), "GoPay", Money(222_000L), appearance = Appearance("ewallet", "teal")),
        HomeWallet(WalletId(4L), "DANA", Money(300_000L), appearance = Appearance("ewallet", "indigo")),
        HomeWallet(WalletId(5L), "Tabungan", Money(2_800_000L), appearance = Appearance("bank", "amber")),
    )

private fun sampleRecent(): List<HomeRecent> =
    listOf(
        HomeRecent(
            transactionId = TransactionId(1L),
            title = "Makan siang",
            walletName = "BCA",
            whenLabel = "12:30",
            amount = Money(45_000L),
            type = TransactionType.EXPENSE,
            appearance = Appearance("food", "orange"),
            auto = true,
        ),
        HomeRecent(
            transactionId = TransactionId(2L),
            title = "Kopi pagi",
            walletName = "GoPay",
            whenLabel = "08:10",
            amount = Money(28_000L),
            type = TransactionType.EXPENSE,
            appearance = Appearance("food", "orange"),
            auto = true,
        ),
        HomeRecent(
            transactionId = TransactionId(3L),
            title = "Belanja bulanan",
            walletName = "BCA",
            whenLabel = "19:45",
            amount = Money(186_500L),
            type = TransactionType.EXPENSE,
            appearance = Appearance("shopping", "pink"),
            auto = true,
        ),
    )

// FR-040, S-03: the Ringkasan sample; the parts add up to the Saldo shown in the hero
private fun sampleNetWorth(): NetWorth =
    NetWorth(
        saldo = Money(12_450_000L),
        investasi = Money(3_000_000L),
        piutang = Money(200_000L),
        utangKartu = Money(3_500_000L),
        hutang = Money(500_000L),
    )

@Composable
private fun HomeSample(
    state: HomeUiState,
    darkTheme: Boolean,
) {
    FintrackTheme(darkTheme = darkTheme) {
        HomeScreen(state = state, callbacks = HomeCallbacks())
    }
}

@Preview(name = "Beranda success light", showBackground = true, heightDp = 720)
@Composable
internal fun HomeSuccessLightPreview() = HomeSample(state = sampleSuccess(), darkTheme = false)

@Preview(name = "Beranda success dark", showBackground = true, heightDp = 720)
@Composable
internal fun HomeSuccessDarkPreview() = HomeSample(state = sampleSuccess(), darkTheme = true)

@Preview(name = "Beranda hidden light", showBackground = true, heightDp = 720)
@Composable
internal fun HomeHiddenLightPreview() = HomeSample(state = sampleSuccess(hidden = true), darkTheme = false)

@Preview(name = "Beranda empty light", showBackground = true, heightDp = 720)
@Composable
internal fun HomeEmptyLightPreview() = HomeSample(state = sampleSuccess(recent = emptyList()), darkTheme = false)

@Preview(name = "Beranda empty dark", showBackground = true, heightDp = 720)
@Composable
internal fun HomeEmptyDarkPreview() = HomeSample(state = sampleSuccess(recent = emptyList()), darkTheme = true)

@Preview(name = "Ringkasan drawer dark", showBackground = true, heightDp = 720)
@Composable
internal fun HomeSummaryDrawerDarkPreview() =
    HomeSample(state = sampleSuccess(drawer = HomeDrawer.SUMMARY), darkTheme = true)

@Preview(name = "Kategori drawer light", showBackground = true, heightDp = 720)
@Composable
internal fun HomeCategoriesDrawerLightPreview() =
    HomeSample(state = sampleSuccess(drawer = HomeDrawer.CATEGORIES), darkTheme = false)

// NFR-010: font scale 2.0 variant for the screenshot tests
@Preview(name = "Beranda font 200", showBackground = true, heightDp = 1200, fontScale = 2f)
@Composable
internal fun HomeFontScalePreview() = HomeSample(state = sampleSuccess(), darkTheme = false)
