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

// S-18: wallet picker drawer; sections Tunai, Debit, Kartu kredit, Paylater, Investasi; picking closes it via the caller
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
    return listOf(
        sampleRow(1L, R.string.picker_sample_cash, WalletType.CASH, "Rp250.000", isSelected = true),
        sampleRow(2L, R.string.picker_sample_bca, WalletType.BANK, "Rp4.800.000"),
        sampleRow(3L, R.string.picker_sample_gopay, WalletType.EWALLET, "Rp120.000"),
        sampleRow(4L, R.string.picker_sample_card, WalletType.CREDIT_CARD, limitLabel, isBlocked = true),
        sampleRow(5L, R.string.picker_sample_paylater, WalletType.PAYLATER, limitLabel),
        sampleRow(6L, R.string.picker_sample_invest, WalletType.INVESTMENT, "Rp1.500.000"),
    )
}

@Composable
private fun sampleRow(
    id: Long,
    nameRes: Int,
    type: WalletType,
    balance: String,
    isSelected: Boolean = false,
    isBlocked: Boolean = false,
): WalletPickerRow =
    WalletPickerRow(
        walletId = WalletId(id),
        name = stringResource(nameRes),
        type = type,
        balanceLabel = balance,
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
