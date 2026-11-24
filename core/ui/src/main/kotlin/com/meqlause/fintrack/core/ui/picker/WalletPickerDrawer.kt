package com.meqlause.fintrack.core.ui.picker

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.meqlause.fintrack.core.designsystem.component.PickerDrawer
import com.meqlause.fintrack.core.model.Appearance
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
    choices: WalletChoices,
    actions: PickerActions<WalletId>,
    modifier: Modifier = Modifier,
) {
    val allowCredit = choices.allowCredit
    val allowInvestment = choices.allowInvestment
    PickerDrawer(title = title, onClose = actions.onClose, modifier = modifier) {
        WalletPickerGroup.entries
            .filter { it.isAllowed(allowCredit = allowCredit, allowInvestment = allowInvestment) }
            .forEach { group ->
                val groupRows = choices.rows.filter { it.type in group.types }
                if (groupRows.isNotEmpty()) {
                    WalletPickerSection(group = group, rows = groupRows, onPick = actions.onPick)
                }
            }
    }
}

@Composable
internal fun WalletPickerDrawerSample() {
    val limit = MoneyFormatter.formatShort(Money(rupiah = 5_800_000L))
    WalletPickerDrawer(
        title = stringResource(R.string.picker_sample_title_pay),
        choices = WalletChoices(rows = sampleRows(limit), allowCredit = true, allowInvestment = true),
        actions = PickerActions(onPick = {}, onClose = {}),
    )
}

@Composable
private fun sampleRows(limit: String): List<WalletPickerRow> {
    val limitLabel = stringResource(R.string.picker_limit_format, limit)
    val samples = sampleWallets() + creditSamples(limitLabel)
    return samples.mapIndexed { index, sample -> sample.toRow(rowId = index + 1L) }
}

private fun sampleWallets(): List<SampleWallet> =
    listOf(
        SampleWallet(R.string.picker_sample_cash, WalletType.CASH, "Rp250rb", Appearance("cash", "green")),
        SampleWallet(
            R.string.picker_sample_bca,
            WalletType.BANK,
            "Rp10jt",
            Appearance("bank", "blue"),
            isSelected = true,
        ),
        SampleWallet(R.string.picker_sample_gopay, WalletType.EWALLET, "Rp222rb", Appearance("ewallet", "teal")),
        SampleWallet(
            R.string.picker_sample_invest,
            WalletType.INVESTMENT,
            "Rp1,5jt",
            Appearance("trend", "indigo"),
        ),
    )

private fun creditSamples(limitLabel: String): List<SampleWallet> =
    listOf(
        SampleWallet(
            R.string.picker_sample_card,
            WalletType.CREDIT_CARD,
            limitLabel,
            Appearance("card", "slate"),
            isBlocked = true,
        ),
        SampleWallet(
            R.string.picker_sample_paylater,
            WalletType.PAYLATER,
            limitLabel,
            Appearance("clock", "orange"),
        ),
    )

private data class SampleWallet(
    val nameRes: Int,
    val type: WalletType,
    val shownText: String,
    val appearance: Appearance,
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
        mark = PickerMark(isSelected = isSelected, isBlocked = isBlocked),
        appearance = appearance,
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
