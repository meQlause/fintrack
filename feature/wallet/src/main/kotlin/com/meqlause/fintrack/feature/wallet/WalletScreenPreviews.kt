package com.meqlause.fintrack.feature.wallet

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.domain.networth.NetWorth
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Wallet
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.model.WalletKind
import com.meqlause.fintrack.core.model.WalletProfile
import com.meqlause.fintrack.core.model.WalletStatus
import com.meqlause.fintrack.core.model.WalletTerms
import com.meqlause.fintrack.core.model.WalletType
import java.time.Instant

// S-09: previews of every state, light and dark
private val previewAudit = Audit(createdAt = Instant.EPOCH, updatedAt = Instant.EPOCH)

private fun previewItem(
    itemId: Long,
    name: String,
    type: WalletType,
    balance: Long,
    look: Pair<String, String> = "wallet" to "gray",
    archived: Boolean = false,
): WalletItem {
    val wallet =
        Wallet(
            walletId = WalletId(itemId),
            kind = WalletKind(type = type, investKind = null),
            terms = WalletTerms(initialBalance = Money(balance), credit = null),
            profile =
                WalletProfile(
                    name = name,
                    appearance = Appearance(iconKey = look.first, colorKey = look.second),
                    sortOrder = itemId.toInt(),
                ),
            status = WalletStatus(isArchived = archived),
            audit = previewAudit,
        )
    return WalletItem(wallet = wallet, balance = Money(balance))
}

// design/screens/wallets.png: the prototype seed on 2026-11-22
private fun successState(editor: WalletEditor? = null): WalletUiState.Success {
    val cash = previewItem(1, "Tunai", WalletType.CASH, 223_000L, "cash" to "green")
    val bca = previewItem(2, "BCA", WalletType.BANK, 9_976_500L, "bank" to "blue")
    val saving = previewItem(5, "Tabungan", WalletType.BANK, 2_700_000L, "bank" to "amber")
    val gopay = previewItem(3, "GoPay", WalletType.EWALLET, 222_000L, "ewallet" to "teal")
    val dana = previewItem(4, "DANA", WalletType.EWALLET, 295_000L, "ewallet" to "indigo")
    val card = previewItem(6, "BCA Card", WalletType.CREDIT_CARD, 3_500_000L, "card" to "slate")
    val later = previewItem(7, "SPayLater", WalletType.PAYLATER, 1_090_000L, "clock" to "orange")
    val fund = previewItem(8, "Reksa Dana", WalletType.INVESTMENT, 4_200_000L, "trend" to "green")
    return WalletUiState.Success(
        netWorth =
            NetWorth(
                saldo = Money(13_416_500L),
                investasi = Money(4_200_000L),
                piutang = Money(200_000L),
                utangKartu = Money(4_590_000L),
                hutang = Money(500_000L),
            ),
        sections =
            listOf(
                WalletSection(WalletGroupKey.CASH, listOf(cash)),
                WalletSection(WalletGroupKey.BANK, listOf(bca, saving)),
                WalletSection(WalletGroupKey.EWALLET, listOf(gopay, dana)),
                WalletSection(WalletGroupKey.CREDIT, listOf(card, later)),
                WalletSection(WalletGroupKey.INVESTMENT, listOf(fund)),
            ),
        archived = listOf(previewItem(9, "Dompet lama", WalletType.OTHER, 0L, archived = true)),
        archivedExpanded = false,
        editor = editor,
        picker = null,
    )
}

@Composable
private fun ScreenSample(state: WalletUiState) {
    WalletScreen(state = state, callbacks = WalletCallbacks())
}

@Preview(name = "Dompet success light", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
internal fun WalletSuccessLightPreview() {
    FintrackTheme(darkTheme = false) { ScreenSample(successState()) }
}

@Preview(name = "Dompet success dark", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
internal fun WalletSuccessDarkPreview() {
    FintrackTheme(darkTheme = true) { ScreenSample(successState()) }
}

@Preview(name = "Dompet empty light", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
internal fun WalletEmptyLightPreview() {
    FintrackTheme(darkTheme = false) { ScreenSample(WalletUiState.Empty(editor = null, picker = null)) }
}

@Preview(name = "Dompet empty dark", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
internal fun WalletEmptyDarkPreview() {
    FintrackTheme(darkTheme = true) { ScreenSample(WalletUiState.Empty(editor = null, picker = null)) }
}

@Preview(name = "Dompet editor light", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
internal fun WalletEditorLightPreview() {
    FintrackTheme(darkTheme = false) { ScreenSample(successState(editor = WalletEditor.forNew())) }
}

@Preview(name = "Dompet editor dark", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
internal fun WalletEditorDarkPreview() {
    FintrackTheme(darkTheme = true) { ScreenSample(successState(editor = WalletEditor.forNew())) }
}

// NFR-010: font scale 2.0 variant for the screenshot tests
@Preview(name = "Dompet font 200", showBackground = true, heightDp = 1200, fontScale = 2f)
@Composable
internal fun WalletFontScalePreview() {
    FintrackTheme(darkTheme = false) { ScreenSample(successState()) }
}
