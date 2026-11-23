package com.meqlause.fintrack.feature.dashboard

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.StatementStatus
import com.meqlause.fintrack.core.model.TransactionId
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.WalletId
import java.time.LocalDate

// S-03: previews of every state and drawer, light and dark
private fun sampleSuccess(
    hidden: Boolean = false,
    recent: List<HomeRecent> = sampleRecent(),
    drawer: HomeDrawer? = null,
): HomeUiState.Success =
    HomeUiState.Success(
        hidden = hidden,
        total = Money(12_450_000L),
        bills =
            HomeBills(
                bills =
                    listOf(
                        HomeBill(WalletId(4L), "BCA Kredit", StatementStatus.DUE, Money(1_250_000L), "Sen, 10 Nov"),
                        HomeBill(WalletId(5L), "SPayLater", StatementStatus.PAID, Money(0L), "Sen, 10 Nov"),
                    ),
                instalmentsDue = Money(350_000L),
            ),
        wallets =
            listOf(
                HomeWallet(WalletId(1L), "BCA", Money(8_200_000L)),
                HomeWallet(WalletId(2L), "GoPay", Money(1_250_000L)),
                HomeWallet(WalletId(3L), "Tunai", Money(3_000_000L)),
            ),
        period =
            HomePeriod(
                startDate = LocalDate.of(2026, 11, 1),
                endDate = LocalDate.of(2026, 11, 30),
                income = Money(5_000_000L),
                expense = Money(3_500_000L),
            ),
        categories =
            listOf(
                HomeCategoryShare("Makanan & Minuman", Money(1_400_000L), 12),
                HomeCategoryShare("Transportasi", Money(900_000L), 8),
                HomeCategoryShare("Belanja", Money(700_000L), 5),
                HomeCategoryShare(null, Money(500_000L), 2),
            ),
        recent = recent,
        drawer = drawer,
    )

private fun sampleRecent(): List<HomeRecent> =
    listOf(
        HomeRecent(
            transactionId = TransactionId(1L),
            title = "Kopi Kenangan",
            walletName = "BCA",
            whenLabel = "Hari ini, 14:05",
            amount = Money(32_000L),
            type = TransactionType.EXPENSE,
        ),
        HomeRecent(
            transactionId = TransactionId(2L),
            title = null,
            walletName = "GoPay",
            whenLabel = "Kemarin, 09:12",
            amount = Money(5_000_000L),
            type = TransactionType.INCOME,
        ),
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
