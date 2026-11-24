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

// S-09: which sample wallet to build, its kind and its icon and colour
private data class PreviewWallet(
    val itemId: Long,
    val name: String,
    val type: WalletType,
    val look: Pair<String, String> = "wallet" to "gray",
)

private fun previewItem(
    spec: PreviewWallet,
    balance: Long,
    archived: Boolean = false,
): WalletItem {
    val itemId = spec.itemId
    val look = spec.look
    val wallet =
        Wallet(
            walletId = WalletId(itemId),
            kind = WalletKind(type = spec.type, investKind = null),
            terms = WalletTerms(initialBalance = Money(balance), credit = null),
            profile =
                WalletProfile(
                    name = spec.name,
                    appearance = Appearance(iconKey = look.first, colorKey = look.second),
                    sortOrder = itemId.toInt(),
                ),
            status = WalletStatus(isArchived = archived),
            audit = previewAudit,
        )
    return WalletItem(wallet = wallet, balance = Money(balance))
}

// design/screens/wallets.png: the prototype seed on 2026-11-22
// S-09: the prototype's wallets in display order, each with its balance
private data class SampleWallet(
    val spec: PreviewWallet,
    val balance: Long,
)

private val SAMPLE_WALLETS =
    listOf(
        SampleWallet(
            PreviewWallet(itemId = 1, name = "Tunai", type = WalletType.CASH, look = "cash" to "green"),
            balance = 223_000L,
        ),
        SampleWallet(
            PreviewWallet(itemId = 2, name = "BCA", type = WalletType.BANK, look = "bank" to "blue"),
            balance = 9_976_500L,
        ),
        SampleWallet(
            PreviewWallet(itemId = 5, name = "Tabungan", type = WalletType.BANK, look = "bank" to "amber"),
            balance = 2_700_000L,
        ),
        SampleWallet(
            PreviewWallet(itemId = 3, name = "GoPay", type = WalletType.EWALLET, look = "ewallet" to "teal"),
            balance = 222_000L,
        ),
        SampleWallet(
            PreviewWallet(itemId = 4, name = "DANA", type = WalletType.EWALLET, look = "ewallet" to "indigo"),
            balance = 295_000L,
        ),
        SampleWallet(
            PreviewWallet(
                itemId = 6,
                name = "BCA Card",
                type = WalletType.CREDIT_CARD,
                look =
                    "card" to "slate",
            ),
            balance = 3_500_000L,
        ),
        SampleWallet(
            PreviewWallet(
                itemId = 7,
                name = "SPayLater",
                type = WalletType.PAYLATER,
                look =
                    "clock" to "orange",
            ),
            balance = 1_090_000L,
        ),
        SampleWallet(
            PreviewWallet(
                itemId = 8,
                name = "Reksa Dana",
                type = WalletType.INVESTMENT,
                look =
                    "trend" to "green",
            ),
            balance = 4_200_000L,
        ),
    )

// design/screens/wallets.png: the prototype seed on 2026-11-22
private fun successState(
    editor: WalletEditor? = null,
    picker: WalletPicker? = null,
): WalletUiState.Success {
    val items = SAMPLE_WALLETS.map { previewItem(it.spec, it.balance) }
    return WalletUiState.Success(
        netWorth =
            NetWorth(
                saldo = Money(rupiah = 13_416_500L),
                investasi = Money(rupiah = 4_200_000L),
                piutang = Money(rupiah = 200_000L),
                utangKartu = Money(rupiah = 4_590_000L),
                hutang = Money(rupiah = 500_000L),
            ),
        sections = WalletSection.group(items),
        archived =
            listOf(
                previewItem(
                    PreviewWallet(itemId = 9, name = "Dompet lama", type = WalletType.OTHER),
                    balance = 0L,
                    archived = true,
                ),
            ),
        archivedExpanded = false,
        editor = editor,
        picker = picker,
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

@Preview(name = "Dompet type picker light", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
internal fun WalletTypePickerLightPreview() {
    FintrackTheme(darkTheme = false) {
        ScreenSample(successState(editor = WalletEditor.forNew(), picker = WalletPicker.TYPE))
    }
}

@Preview(name = "Dompet type picker dark", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
internal fun WalletTypePickerDarkPreview() {
    FintrackTheme(darkTheme = true) {
        ScreenSample(successState(editor = WalletEditor.forNew(), picker = WalletPicker.TYPE))
    }
}

// NFR-010: font scale 2.0 variant for the screenshot tests
@Preview(name = "Dompet font 200", showBackground = true, heightDp = 1200, fontScale = 2f)
@Composable
internal fun WalletFontScalePreview() {
    FintrackTheme(darkTheme = false) { ScreenSample(successState()) }
}
