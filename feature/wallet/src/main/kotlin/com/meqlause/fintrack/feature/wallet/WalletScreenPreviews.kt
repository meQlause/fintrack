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
    id: Long,
    name: String,
    type: WalletType,
    balance: Long,
    archived: Boolean = false,
): WalletItem {
    val wallet =
        Wallet(
            walletId = WalletId(id),
            kind = WalletKind(type = type, investKind = null),
            terms = WalletTerms(initialBalance = Money(balance), credit = null),
            profile =
                WalletProfile(
                    name = name,
                    appearance = Appearance(iconKey = "wallet", colorKey = "gray"),
                    sortOrder = id.toInt(),
                ),
            status = WalletStatus(isArchived = archived),
            audit = previewAudit,
        )
    return WalletItem(wallet = wallet, balance = Money(balance))
}

private fun successState(editor: WalletEditor? = null): WalletUiState.Success {
    val cash = previewItem(1, "Dompet tunai", WalletType.CASH, 250_000L)
    val bank = previewItem(2, "BCA", WalletType.BANK, 5_000_000L)
    val card = previewItem(3, "BCA Card", WalletType.CREDIT_CARD, 1_200_000L)
    return WalletUiState.Success(
        netWorth =
            NetWorth(
                saldo = Money(cash.balance.rupiah + bank.balance.rupiah),
                investasi = Money(900_000L),
                piutang = Money(200_000L),
                utangKartu = Money(1_200_000L),
                hutang = Money(500_000L),
            ),
        sections =
            listOf(
                WalletSection(WalletGroupKey.CASH, listOf(cash)),
                WalletSection(WalletGroupKey.BANK, listOf(bank)),
                WalletSection(WalletGroupKey.CREDIT, listOf(card)),
            ),
        archived = listOf(previewItem(4, "Dompet lama", WalletType.OTHER, 0L, archived = true)),
        archivedExpanded = false,
        editor = editor,
        picker = null,
    )
}

@Composable
private fun ScreenSample(state: WalletUiState) {
    WalletScreen(state = state, callbacks = WalletCallbacks())
}

@Preview(name = "Dompet success light", showBackground = true, heightDp = 720)
@Composable
internal fun WalletSuccessLightPreview() {
    FintrackTheme(darkTheme = false) { ScreenSample(successState()) }
}

@Preview(name = "Dompet success dark", showBackground = true, heightDp = 720)
@Composable
internal fun WalletSuccessDarkPreview() {
    FintrackTheme(darkTheme = true) { ScreenSample(successState()) }
}

@Preview(name = "Dompet empty light", showBackground = true, heightDp = 720)
@Composable
internal fun WalletEmptyLightPreview() {
    FintrackTheme(darkTheme = false) { ScreenSample(WalletUiState.Empty(editor = null, picker = null)) }
}

@Preview(name = "Dompet empty dark", showBackground = true, heightDp = 720)
@Composable
internal fun WalletEmptyDarkPreview() {
    FintrackTheme(darkTheme = true) { ScreenSample(WalletUiState.Empty(editor = null, picker = null)) }
}

@Preview(name = "Dompet editor light", showBackground = true, heightDp = 720)
@Composable
internal fun WalletEditorLightPreview() {
    FintrackTheme(darkTheme = false) { ScreenSample(successState(editor = WalletEditor.forNew())) }
}

@Preview(name = "Dompet editor dark", showBackground = true, heightDp = 720)
@Composable
internal fun WalletEditorDarkPreview() {
    FintrackTheme(darkTheme = true) { ScreenSample(successState(editor = WalletEditor.forNew())) }
}
