package com.meqlause.fintrack.feature.dashboard

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.abs

// FR-040, FR-041, S-03: the Ringkasan drawer; Kekayaan bersih on top (BR-053), then the month, Aset and Utang
@Composable
internal fun HomeSummaryDrawer(
    state: HomeUiState.Success,
    onClose: () -> Unit,
    onOpenWallets: () -> Unit,
) {
    HomeInfoDrawer(content = summaryContent(state), onClose = onClose, onAction = onOpenWallets)
}

@Composable
private fun summaryContent(state: HomeUiState.Success): HomeInfoContent {
    val show = Shown(hidden = state.hidden, mask = stringResource(R.string.home_amount_hidden))
    val worth = state.netWorth
    return HomeInfoContent(
        title = stringResource(R.string.home_summary_title),
        heroLabel = stringResource(R.string.home_summary_net_worth),
        heroValue = show.plain(worth.total),
        heroNote = stringResource(R.string.home_summary_net_note),
        sections = summarySections(state, show),
        actionLabel = stringResource(R.string.home_summary_action),
    )
}

// S-03: the month gap, the assets and the debts, in the order the drawer shows them
@Composable
private fun summarySections(
    state: HomeUiState.Success,
    show: Shown,
): List<HomeInfoSection> {
    val worth = state.netWorth
    val difference = state.period.income - state.period.expense
    val month =
        state.period.startDate.month
            .getDisplayName(TextStyle.FULL, Locale.forLanguageTag("id"))
    return listOf(
        HomeInfoSection(
            title = stringResource(R.string.home_summary_month, month),
            total = stringResource(R.string.home_summary_gap, show.signed(difference, plus = true)),
            tone = if (difference.rupiah >= 0L) HomeTone.INCOME else HomeTone.EXPENSE,
            rows = monthRows(state, show),
        ),
        HomeInfoSection(
            title = stringResource(R.string.home_summary_assets),
            total = show.plain(worth.assets),
            tone = HomeTone.INCOME,
            rows = assetRows(state, show),
        ),
        HomeInfoSection(
            title = stringResource(R.string.home_summary_debts),
            total = show.signed(worth.debts, plus = false),
            tone = HomeTone.EXPENSE,
            rows = debtRows(state, show),
        ),
    )
}

@Composable
private fun monthRows(
    state: HomeUiState.Success,
    show: Shown,
): List<HomeInfoRow> =
    listOf(
        HomeInfoRow(
            stringResource(R.string.home_summary_income),
            stringResource(R.string.home_summary_income_sub),
            "+" + show.plain(state.period.income),
            HomeTone.INCOME,
            "down",
            "green",
        ),
        HomeInfoRow(
            stringResource(R.string.home_summary_expense),
            stringResource(R.string.home_summary_expense_sub),
            show.signed(state.period.expense, plus = false),
            HomeTone.EXPENSE,
            "up",
            "rose",
        ),
    )

// the prototype lists the biggest asset first
@Composable
private fun assetRows(
    state: HomeUiState.Success,
    show: Shown,
): List<HomeInfoRow> {
    val worth = state.netWorth
    val rows =
        listOf(
            worth.saldo to saldoRow(show, worth.saldo),
            worth.investasi to investmentRow(show, worth.investasi),
            worth.piutang to receivableRow(show, worth.piutang),
        )
    return rows.sortedByDescending { it.first.rupiah }.map { it.second }
}

@Composable
private fun saldoRow(
    show: Shown,
    amount: Money,
): HomeInfoRow =
    HomeInfoRow(
        stringResource(R.string.home_summary_saldo),
        stringResource(R.string.home_summary_saldo_sub),
        show.plain(amount),
        HomeTone.NORMAL,
        "wallet",
        "teal",
    )

@Composable
private fun investmentRow(
    show: Shown,
    amount: Money,
): HomeInfoRow =
    HomeInfoRow(
        stringResource(R.string.home_summary_investment),
        stringResource(R.string.home_summary_investment_sub),
        show.plain(amount),
        HomeTone.NORMAL,
        "trend",
        "indigo",
    )

@Composable
private fun receivableRow(
    show: Shown,
    amount: Money,
): HomeInfoRow =
    HomeInfoRow(
        stringResource(R.string.home_summary_receivable),
        stringResource(R.string.home_summary_receivable_sub),
        show.plain(amount),
        HomeTone.NORMAL,
        "person",
        "cyan",
    )

@Composable
private fun debtRows(
    state: HomeUiState.Success,
    show: Shown,
): List<HomeInfoRow> {
    val worth = state.netWorth
    val rows =
        listOf(
            worth.utangKartu to
                HomeInfoRow(
                    stringResource(R.string.home_summary_card_debt),
                    stringResource(R.string.home_summary_card_debt_sub),
                    show.signed(worth.utangKartu, plus = false),
                    HomeTone.EXPENSE,
                    "card",
                    "slate",
                ),
            worth.hutang to
                HomeInfoRow(
                    stringResource(R.string.home_summary_debt),
                    stringResource(R.string.home_summary_debt_sub),
                    show.signed(worth.hutang, plus = false),
                    HomeTone.EXPENSE,
                    "person",
                    "orange",
                ),
        )
    return rows.sortedByDescending { it.first.rupiah }.map { it.second }
}

// FR-040: formats an amount, or the privacy mask while amounts are hidden
private class Shown(
    private val hidden: Boolean,
    private val mask: String,
) {
    fun plain(money: Money): String = if (hidden) mask else MoneyFormatter.format(money)

    fun signed(
        money: Money,
        plus: Boolean,
    ): String {
        if (hidden) return mask
        val sign = if (plus && money.rupiah >= 0L) "+" else "−"
        return sign + MoneyFormatter.format(Money(abs(money.rupiah)))
    }
}
