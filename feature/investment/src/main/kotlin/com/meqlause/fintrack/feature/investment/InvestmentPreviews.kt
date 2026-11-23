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
): Wallet =
    Wallet(
        walletId = WalletId(walletKey),
        kind = WalletKind(type = WalletType.INVESTMENT, investKind = kind),
        terms = WalletTerms(initialBalance = Money(1_000_000L), credit = null),
        profile = WalletProfile(name = name, appearance = Appearance("trend", "gray"), sortOrder = walletKey.toInt()),
        status = WalletStatus(),
        audit = previewAudit,
    )

private fun previewAccount(
    wallet: Wallet,
    current: Long,
): InvestmentAccount =
    InvestmentAccount(
        wallet = wallet,
        valuation =
            InvestmentValuation(
                modal = Money(2_000_000L),
                currentValue = Money(current),
                lastValuedAt = previewTime,
            ),
    )

private fun previewPortfolio(): InvestmentPortfolio {
    val bibit = previewAccount(previewWallet(1L, "Bibit", InvestKind.MUTUAL_FUND), 2_080_000L)
    val emas = previewAccount(previewWallet(2L, "Emas Pegadaian", InvestKind.GOLD), 1_850_000L)
    val recent =
        listOf(
            InvestmentActivity(WalletId(1L), previewTime, ActivityKind.VALUATION, Money(2_080_000L)),
            InvestmentActivity(WalletId(2L), previewTime, ActivityKind.PUT_IN, Money(500_000L)),
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
        onDigit = {},
        onBackspace = {},
        onSave = {},
        onDismiss = {},
    )

@Composable
private fun TabSample(state: InvestmentUiState) {
    InvestmentScreen(state = state, callbacks = previewCallbacks())
}

@Preview(name = "Investasi success light", showBackground = true, heightDp = 720)
@Composable
internal fun InvestmentSuccessLightPreview() {
    FintrackTheme(darkTheme = false) { TabSample(InvestmentUiState(portfolio = previewPortfolio())) }
}

@Preview(name = "Investasi success dark", showBackground = true, heightDp = 720)
@Composable
internal fun InvestmentSuccessDarkPreview() {
    FintrackTheme(darkTheme = true) { TabSample(InvestmentUiState(portfolio = previewPortfolio())) }
}

@Preview(name = "Investasi empty light", showBackground = true, heightDp = 720)
@Composable
internal fun InvestmentEmptyLightPreview() {
    FintrackTheme(darkTheme = false) {
        TabSample(InvestmentUiState(portfolio = InvestmentPortfolio(accounts = emptyList(), recent = emptyList())))
    }
}

@Preview(name = "Investasi detail light", showBackground = true, heightDp = 720)
@Composable
internal fun InvestmentDetailLightPreview() {
    FintrackTheme(darkTheme = false) { DetailSample() }
}

@Preview(name = "Investasi detail dark", showBackground = true, heightDp = 720)
@Composable
internal fun InvestmentDetailDarkPreview() {
    FintrackTheme(darkTheme = true) { DetailSample() }
}

@Composable
private fun DetailSample() {
    val account = previewAccount(previewWallet(1L, "Bibit", InvestKind.MUTUAL_FUND), 2_080_000L)
    val activity = listOf(InvestmentActivity(WalletId(1L), previewTime, ActivityKind.PUT_IN, Money(500_000L)))
    val state = InvestmentDetailUiState(loaded = true, detail = InvestmentDetail(account, activity))
    InvestmentDetailScreen(state = state, callbacks = detailCallbacks())
}

private fun detailCallbacks(): InvestmentDetailCallbacks =
    InvestmentDetailCallbacks(
        onBack = {},
        onOpenValuation = {},
        onDigit = {},
        onBackspace = {},
        onSaveValuation = {},
        onDismissValuation = {},
        onTransfer = {},
    )
