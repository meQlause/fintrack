package com.meqlause.fintrack.feature.wallet.detail

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.Wallet
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.model.WalletKind
import com.meqlause.fintrack.core.model.WalletProfile
import com.meqlause.fintrack.core.model.WalletStatus
import com.meqlause.fintrack.core.model.WalletTerms
import com.meqlause.fintrack.core.model.WalletType
import com.meqlause.fintrack.feature.wallet.WalletItem
import java.time.Instant

// FR-017, S-19: previews of the wallet detail, light and dark, collapsed and expanded
private val previewAudit = Audit(createdAt = Instant.EPOCH, updatedAt = Instant.EPOCH)

// design/screens/wallet-detail.png: the BCA wallet of the prototype seed
private fun previewWallet(): WalletItem {
    val wallet =
        Wallet(
            walletId = WalletId(1L),
            kind = WalletKind(type = WalletType.BANK, investKind = null),
            terms = WalletTerms(initialBalance = Money(0L), credit = null),
            profile =
                WalletProfile(
                    name = "BCA",
                    appearance = Appearance(iconKey = "bank", colorKey = "blue"),
                    sortOrder = 0,
                ),
            status = WalletStatus(),
            audit = previewAudit,
        )
    return WalletItem(wallet = wallet, balance = Money(9_976_500L))
}

private fun previewRow(
    index: Long,
    title: String,
    amount: Long,
    look: Triple<String, String, TransactionType>,
    time: String,
): WalletDetailRow =
    WalletDetailRow(
        transactionId = index,
        title = title,
        subtitle = if (look.third == TransactionType.TRANSFER) "BCA → BCA Card · $time" else "BCA · $time",
        amount = Money(amount),
        type = look.third,
        appearance = Appearance(look.first, look.second),
    )

private fun previewRows(): List<WalletDetailRow> {
    val spend = TransactionType.EXPENSE
    return listOf(
        previewRow(1, "Makan siang", 45_000L, Triple("food", "orange", spend), "12:30"),
        previewRow(2, "Belanja bulanan", 186_500L, Triple("shopping", "rose", spend), "19:45"),
        previewRow(3, "Langganan musik", 54_000L, Triple("entertainment", "indigo", spend), "20:15"),
        previewRow(4, "Bayar BCA Card", 500_000L, Triple("card", "blue", TransactionType.TRANSFER), "10:00"),
        previewRow(5, "Listrik", 450_000L, Triple("bill", "purple", spend), "10:00"),
        previewRow(6, "Nonton", 100_000L, Triple("entertainment", "indigo", spend), "19:00"),
        previewRow(7, "Transfer ke Tunai", 300_000L, Triple("transfer", "blue", TransactionType.TRANSFER), "08:00"),
        previewRow(8, "Kopi", 28_000L, Triple("food", "orange", spend), "07:30"),
        previewRow(9, "Bensin", 60_000L, Triple("transport", "blue", spend), "07:00"),
    )
}

private val previewStats =
    WalletDetailStats(
        inflow = Money(10_200_000L),
        outflow = Money(5_300_000L),
        count = 18,
        perDay =
            listOf(
                900_000L,
                700_000L,
                40_000L,
                110_000L,
                30_000L,
                50_000L,
                20_000L,
                100_000L,
                10_000L,
                10_000L,
                10_000L,
                20_000L,
                40_000L,
                30_000L,
                150_000L,
                20_000L,
                10_000L,
                200_000L,
                40_000L,
                20_000L,
                10_000L,
                0L,
                0L,
                0L,
                0L,
                0L,
                0L,
                0L,
                0L,
                0L,
            ),
        today = 22,
        monthLabel = "Nov",
    )

private fun successState(expanded: Boolean): WalletDetailUiState.Success =
    WalletDetailUiState.Success(item = previewWallet(), rows = previewRows(), expanded = expanded, stats = previewStats)

@Composable
private fun ScreenSample(state: WalletDetailUiState) {
    WalletDetailScreen(state = state, callbacks = WalletDetailCallbacks())
}

@Preview(name = "Wallet detail light", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
internal fun WalletDetailLightPreview() {
    FintrackTheme(darkTheme = false) { ScreenSample(successState(expanded = false)) }
}

@Preview(name = "Wallet detail dark", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
internal fun WalletDetailDarkPreview() {
    FintrackTheme(darkTheme = true) { ScreenSample(successState(expanded = false)) }
}

@Preview(name = "Wallet detail expanded light", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
internal fun WalletDetailExpandedLightPreview() {
    FintrackTheme(darkTheme = false) { ScreenSample(successState(expanded = true)) }
}

@Preview(name = "Wallet detail expanded dark", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
internal fun WalletDetailExpandedDarkPreview() {
    FintrackTheme(darkTheme = true) { ScreenSample(successState(expanded = true)) }
}
