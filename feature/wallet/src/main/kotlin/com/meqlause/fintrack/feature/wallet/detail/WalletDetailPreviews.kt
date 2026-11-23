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

private fun previewWallet(): WalletItem {
    val wallet =
        Wallet(
            walletId = WalletId(1L),
            kind = WalletKind(type = WalletType.BANK, investKind = null),
            terms = WalletTerms(initialBalance = Money(0L), credit = null),
            profile =
                WalletProfile(
                    name = "BCA",
                    appearance = Appearance(iconKey = "wallet", colorKey = "gray"),
                    sortOrder = 0,
                ),
            status = WalletStatus(),
            audit = previewAudit,
        )
    return WalletItem(wallet = wallet, balance = Money(5_000_000L))
}

private fun previewRows(count: Int): List<WalletDetailRow> =
    (1..count).map { index ->
        WalletDetailRow(
            transactionId = index.toLong(),
            title = if (index % 2 == 0) "Makan siang" else "",
            subtitle = "$index Nov 2026",
            amount = Money(25_000L * index),
            type = if (index % 3 == 0) TransactionType.INCOME else TransactionType.EXPENSE,
        )
    }

private fun successState(
    rowCount: Int,
    expanded: Boolean,
): WalletDetailUiState.Success =
    WalletDetailUiState.Success(item = previewWallet(), rows = previewRows(rowCount), expanded = expanded)

@Composable
private fun ScreenSample(state: WalletDetailUiState) {
    WalletDetailScreen(state = state, callbacks = WalletDetailCallbacks())
}

@Preview(name = "Wallet detail light", showBackground = true, heightDp = 720)
@Composable
internal fun WalletDetailLightPreview() {
    FintrackTheme(darkTheme = false) { ScreenSample(successState(rowCount = 10, expanded = false)) }
}

@Preview(name = "Wallet detail dark", showBackground = true, heightDp = 720)
@Composable
internal fun WalletDetailDarkPreview() {
    FintrackTheme(darkTheme = true) { ScreenSample(successState(rowCount = 10, expanded = false)) }
}

@Preview(name = "Wallet detail expanded light", showBackground = true, heightDp = 720)
@Composable
internal fun WalletDetailExpandedLightPreview() {
    FintrackTheme(darkTheme = false) { ScreenSample(successState(rowCount = 10, expanded = true)) }
}

@Preview(name = "Wallet detail expanded dark", showBackground = true, heightDp = 720)
@Composable
internal fun WalletDetailExpandedDarkPreview() {
    FintrackTheme(darkTheme = true) { ScreenSample(successState(rowCount = 10, expanded = true)) }
}
