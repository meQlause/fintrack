package com.meqlause.fintrack.core.ui.picker

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.meqlause.fintrack.core.designsystem.component.PickerDrawer
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.model.WalletType
import com.meqlause.fintrack.core.ui.R
import com.meqlause.fintrack.core.ui.UiPreviewCanvas
import com.meqlause.fintrack.core.ui.format.MoneyFormatter

// S-18: wallet picker drawer with sections Tunai, Debit, Kartu kredit, Paylater, Investasi; a pick closes it
@Composable
fun WalletPickerDrawer(
    title: String,
    rows: List<WalletPickerRow>,
    onPick: (WalletId) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    allowCredit: Boolean = true,
    allowInvestment: Boolean = false,
) {
    PickerDrawer(title = title, onClose = onClose, modifier = modifier) {
        WalletPickerGroup.entries
            .filter { it.isAllowed(allowCredit = allowCredit, allowInvestment = allowInvestment) }
            .forEach { group ->
                val groupRows = rows.filter { it.type in group.types }
                if (groupRows.isNotEmpty()) {
                    WalletPickerSection(group = group, rows = groupRows, onPick = onPick)
                }
            }
    }
}

@Composable
internal fun WalletPickerDrawerSample() {
    val limit = MoneyFormatter.format(Money(rupiah = 2_000_000L))
    WalletPickerDrawer(
        title = stringResource(R.string.picker_sample_title_pay),
        rows = sampleRows(limit),
        onPick = {},
        onClose = {},
        allowCredit = true,
        allowInvestment = true,
    )
}

@Composable
private fun sampleRows(limit: String): List<WalletPickerRow> {
    val limitLabel = stringResource(R.string.picker_limit_format, limit)
    val samples =
        listOf(
            SampleWallet(R.string.picker_sample_cash, WalletType.CASH, "Rp250.000", isSelected = true),
            SampleWallet(R.string.picker_sample_bca, WalletType.BANK, "Rp4.800.000"),
            SampleWallet(R.string.picker_sample_gopay, WalletType.EWALLET, "Rp120.000"),
            SampleWallet(R.string.picker_sample_card, WalletType.CREDIT_CARD, limitLabel, isBlocked = true),
            SampleWallet(R.string.picker_sample_paylater, WalletType.PAYLATER, limitLabel),
            SampleWallet(R.string.picker_sample_invest, WalletType.INVESTMENT, "Rp1.500.000"),
        )
    return samples.mapIndexed { index, sample -> sample.toRow(rowId = index + 1L) }
}

private data class SampleWallet(
    val nameRes: Int,
    val type: WalletType,
    val shownText: String,
    val isSelected: Boolean = false,
    val isBlocked: Boolean = false,
)

@Composable
private fun SampleWallet.toRow(rowId: Long): WalletPickerRow =
    WalletPickerRow(
        walletId = WalletId(rowId),
        name = stringResource(nameRes),
        type = type,
        shownBalanceText = shownText,
        isSelected = isSelected,
        isBlocked = isBlocked,
    )

@Preview(name = "WalletPickerDrawer light", showBackground = true)
@Composable
internal fun WalletPickerDrawerLightPreview() {
    UiPreviewCanvas(darkTheme = false) { WalletPickerDrawerSample() }
}

@Preview(name = "WalletPickerDrawer dark", showBackground = true)
@Composable
internal fun WalletPickerDrawerDarkPreview() {
    UiPreviewCanvas(darkTheme = true) { WalletPickerDrawerSample() }
}
