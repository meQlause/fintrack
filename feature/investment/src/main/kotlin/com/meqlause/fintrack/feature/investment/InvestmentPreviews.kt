package com.meqlause.fintrack.feature.investment

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.domain.investment.ActivityKind
import com.meqlause.fintrack.core.domain.investment.InvestmentAccount
import com.meqlause.fintrack.core.domain.investment.InvestmentActivity
import com.meqlause.fintrack.core.domain.investment.InvestmentDetail
import com.meqlause.fintrack.core.domain.investment.InvestmentPortfolio
import com.meqlause.fintrack.core.domain.investment.InvestmentValuation
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.InvestKind
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Wallet
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.model.WalletKind
import com.meqlause.fintrack.core.model.WalletProfile
import com.meqlause.fintrack.core.model.WalletStatus
import com.meqlause.fintrack.core.model.WalletTerms
import com.meqlause.fintrack.core.model.WalletType
import com.meqlause.fintrack.feature.investment.detail.InvestmentDetailCallbacks
import com.meqlause.fintrack.feature.investment.detail.InvestmentDetailScreen
import com.meqlause.fintrack.feature.investment.detail.InvestmentDetailUiState
import java.time.Instant

// S-08, S-20: previews of the Investasi tab and the investment detail, light and dark
private val previewAudit = Audit(createdAt = Instant.EPOCH, updatedAt = Instant.EPOCH)
private val previewTime: Instant = Instant.parse("2026-11-20T09:00:00Z")

private fun previewWallet(
    walletKey: Long,
    name: String,
    kind: InvestKind,
    colorKey: String = "green",
): Wallet =
    Wallet(
        walletId = WalletId(walletKey),
        kind = WalletKind(type = WalletType.INVESTMENT, investKind = kind),
        terms = WalletTerms(initialBalance = Money(PREVIEW_BALANCE), credit = null),
        profile = WalletProfile(name = name, appearance = Appearance("trend", colorKey), sortOrder = walletKey.toInt()),
        status = WalletStatus(),
        audit = previewAudit,
    )

private fun previewAccount(
    wallet: Wallet,
    current: Long,
    valued: Instant = previewTime,
): InvestmentAccount =
    InvestmentAccount(
        wallet = wallet,
        valuation =
            InvestmentValuation(
                modal = Money(PREVIEW_MODAL),
                currentValue = Money(current),
                lastValuedAt = valued,
            ),
    )

private const val BIBIT_KEY = 1L
private const val EMAS_KEY = 2L
private const val BIBIT_VALUE = 2_080_000L
private const val EMAS_VALUE = 2_150_000L
private const val BIBIT_PUT_IN = 1_000_000L
private const val EMAS_PUT_IN = 2_000_000L

// design/screens/invest.png: the prototype seed on 2026-11-22
private fun previewPortfolio(): InvestmentPortfolio {
    val bibit =
        previewAccount(
            previewWallet(BIBIT_KEY, "Bibit", InvestKind.MUTUAL_FUND),
            BIBIT_VALUE,
            Instant.parse("2026-11-20T09:00:00Z"),
        )
    val emas =
        previewAccount(
            previewWallet(EMAS_KEY, "Emas Pegadaian", InvestKind.GOLD, "amber"),
            EMAS_VALUE,
            Instant.parse("2026-11-10T09:00:00Z"),
        )
    val recent =
        listOf(
            InvestmentActivity(WalletId(BIBIT_KEY), bibit.valuation.lastValuedAt!!, ActivityKind.VALUATION, Money(BIBIT_VALUE)),
            InvestmentActivity(WalletId(EMAS_KEY), emas.valuation.lastValuedAt!!, ActivityKind.VALUATION, Money(EMAS_VALUE)),
            InvestmentActivity(WalletId(BIBIT_KEY), Instant.parse("2026-11-02T09:00:00Z"), ActivityKind.PUT_IN, Money(BIBIT_PUT_IN)),
            InvestmentActivity(WalletId(EMAS_KEY), Instant.parse("2026-10-15T09:00:00Z"), ActivityKind.PUT_IN, Money(EMAS_PUT_IN)),
        )
    return InvestmentPortfolio(accounts = listOf(bibit, emas), recent = recent)
}

private fun previewCallbacks(): InvestmentCallbacks =
    InvestmentCallbacks(
        onAdd = {},
        onOpenAccount = {},
        onNameChange = {},
        onOpenKindPicker = {},
        onPickKind = {},
        sheet = AmountSheetCallbacks(onDigit = {}, onBackspace = {}, onSave = {}, onDismiss = {}),
    )

@Composable
private fun TabSample(state: InvestmentUiState) {
    InvestmentScreen(state = state, callbacks = previewCallbacks())
}

@Preview(name = "Investasi success light", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
internal fun InvestmentSuccessLightPreview() {
    FintrackTheme(darkTheme = false) { TabSample(InvestmentUiState(portfolio = previewPortfolio())) }
}

@Preview(name = "Investasi success dark", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
internal fun InvestmentSuccessDarkPreview() {
    FintrackTheme(darkTheme = true) { TabSample(InvestmentUiState(portfolio = previewPortfolio())) }
}

@Preview(name = "Investasi empty light", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
internal fun InvestmentEmptyLightPreview() {
    FintrackTheme(darkTheme = false) {
        TabSample(InvestmentUiState(portfolio = InvestmentPortfolio(accounts = emptyList(), recent = emptyList())))
    }
}

@Preview(name = "Investasi detail light", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
internal fun InvestmentDetailLightPreview() {
    FintrackTheme(darkTheme = false) { DetailSample() }
}

@Preview(name = "Investasi detail dark", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
internal fun InvestmentDetailDarkPreview() {
    FintrackTheme(darkTheme = true) { DetailSample() }
}

@Composable
private fun DetailSample() {
    val account = previewAccount(previewWallet(BIBIT_KEY, "Bibit", InvestKind.MUTUAL_FUND), BIBIT_VALUE)
    val activity = listOf(InvestmentActivity(WalletId(BIBIT_KEY), previewTime, ActivityKind.PUT_IN, Money(BIBIT_PUT_IN)))
    val state = InvestmentDetailUiState(loaded = true, detail = InvestmentDetail(account, activity))
    InvestmentDetailScreen(state = state, callbacks = detailCallbacks())
}

private fun detailCallbacks(): InvestmentDetailCallbacks =
    InvestmentDetailCallbacks(
        onBack = {},
        onOpenValuation = {},
        onTransfer = {},
        valuation = AmountSheetCallbacks(onDigit = {}, onBackspace = {}, onSave = {}, onDismiss = {}),
    )

private const val PREVIEW_BALANCE = 1_000_000L
private const val PREVIEW_MODAL = 2_000_000L
