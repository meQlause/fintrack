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
    return WalletItem(wallet = wallet, balance = Money(rupiah = 9_976_500L))
}

// S-19: the icon, colour, type and time of one sample row
private data class PreviewLook(
    val iconKey: String,
    val colorKey: String,
    val type: TransactionType,
    val time: String,
)

private fun previewRow(
    index: Long,
    title: String,
    amount: Long,
    look: PreviewLook,
): WalletDetailRow =
    WalletDetailRow(
        transactionId = index,
        title = title,
        subtitle = if (look.type == TransactionType.TRANSFER) "BCA → BCA Card · ${look.time}" else "BCA · ${look.time}",
        amount = Money(amount),
        type = look.type,
        appearance = Appearance(look.iconKey, look.colorKey),
    )

// S-19: the nine sample rows of the wallet detail
private data class SampleRow(
    val index: Long,
    val title: String,
    val amount: Long,
    val look: PreviewLook,
)

private val SAMPLE_ROWS =
    listOf(
        SampleRow(
            index = 1,
            title = "Makan siang",
            amount = 45_000L,
            look = PreviewLook("food", "orange", TransactionType.EXPENSE, "12:30"),
        ),
        SampleRow(
            index = 2,
            title = "Belanja bulanan",
            amount = 186_500L,
            look = PreviewLook("shopping", "rose", TransactionType.EXPENSE, "19:45"),
        ),
        SampleRow(
            index = 3,
            title = "Langganan musik",
            amount = 54_000L,
            look = PreviewLook("entertainment", "indigo", TransactionType.EXPENSE, "20:15"),
        ),
        SampleRow(
            index = 4,
            title = "Bayar BCA Card",
            amount = 500_000L,
            look = PreviewLook("card", "blue", TransactionType.TRANSFER, "10:00"),
        ),
        SampleRow(
            index = 5,
            title = "Listrik",
            amount = 450_000L,
            look = PreviewLook("bill", "purple", TransactionType.EXPENSE, "10:00"),
        ),
        SampleRow(
            index = 6,
            title = "Nonton",
            amount = 100_000L,
            look = PreviewLook("entertainment", "indigo", TransactionType.EXPENSE, "19:00"),
        ),
        SampleRow(
            index = 7,
            title = "Transfer ke Tunai",
            amount = 300_000L,
            look = PreviewLook("transfer", "blue", TransactionType.TRANSFER, "08:00"),
        ),
        SampleRow(
            index = 8,
            title = "Kopi",
            amount = 28_000L,
            look = PreviewLook("food", "orange", TransactionType.EXPENSE, "07:30"),
        ),
        SampleRow(
            index = 9,
            title = "Bensin",
            amount = 60_000L,
            look = PreviewLook("transport", "blue", TransactionType.EXPENSE, "07:00"),
        ),
    )

private fun previewRows(): List<WalletDetailRow> =
    SAMPLE_ROWS.map { previewRow(index = it.index, title = it.title, amount = it.amount, look = it.look) }

// S-19: thirty days of outflow, nothing after the ninth of the month
private val PER_DAY_SAMPLE: List<Long> =
    """
    900000 700000 40000 110000 30000 50000 20000 100000 10000 10000 10000 20000 40000 30000 150000
    20000 10000 200000 40000 20000 10000 0 0 0 0 0 0 0 0 0
    """.trim().split(Regex("""\s+""")).map(String::toLong)

private val previewStats =
    WalletDetailStats(
        inflow = Money(rupiah = 10_200_000L),
        outflow = Money(rupiah = 5_300_000L),
        count = 18,
        perDay = PER_DAY_SAMPLE,
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
