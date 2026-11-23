package com.meqlause.fintrack.feature.dashboard

import com.meqlause.fintrack.core.domain.bills.BillsCard
import com.meqlause.fintrack.core.domain.networth.NetWorth
import com.meqlause.fintrack.core.domain.period.Period
import com.meqlause.fintrack.core.domain.period.PeriodCalculator
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Category
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Transaction
import com.meqlause.fintrack.core.model.TransactionSource
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.Wallet
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.model.WalletType
import com.meqlause.fintrack.core.ui.format.DateFormatter
import com.meqlause.fintrack.core.ui.transaction.RowAppearance
import java.time.Clock
import java.time.Instant
import java.time.LocalDate

// FR-040..043, BR-010: derives every Beranda block from one snapshot; pure, so the clock is the only input
internal object HomeCalculator {
    private const val RECENT_COUNT = 5
    private const val PERCENT = 100L

    // FR-040..043: the state for one snapshot; hideOverride wins over the stored hide-by-default setting
    fun success(
        snapshot: HomeSnapshot,
        card: BillsCard,
        worth: NetWorth,
        hideOverride: Boolean?,
        drawer: HomeDrawer?,
        clock: Clock,
    ): HomeUiState.Success {
        val startDay = snapshot.settings.preferences.periodStartDay
        val period = PeriodCalculator.periodContaining(LocalDate.now(clock), startDay)
        val names = snapshot.categories.associate { it.categoryId to it.profile.name }
        val appearances = snapshot.categories.associate { it.categoryId to it.profile.appearance }
        val walletNames = snapshot.wallets.associate { it.walletId to it.profile.name }
        val active = snapshot.wallets.filterNot { it.status.isArchived }
        val inPeriod =
            snapshot.transactions.filter {
                dateOf(it.timing.occurredAt, clock) in
                    period.startDate..period.endDate
            }
        return HomeUiState.Success(
            hidden = hideOverride ?: snapshot.settings.preferences.hideAmountsByDefault,
            total = worth.saldo,
            netWorth = worth,
            wallets =
                active.map {
                    HomeWallet(
                        walletId = it.walletId,
                        name = it.profile.name,
                        balance = balanceOf(it, snapshot.moneyByWallet),
                        isCredit = isCreditWallet(it),
                        appearance = it.profile.appearance,
                    )
                },
            period = periodOf(period, inPeriod),
            categories = categoryShares(inPeriod, names, appearances),
            recent = recentOf(snapshot.transactions, names, appearances, walletNames, clock),
            drawer = drawer,
            bills = HomeBillsMapper.map(card, clock),
        )
    }

    // FR-042: a share is a whole-number percent of the whole; zero when the whole is zero
    fun percentOf(
        part: Money,
        whole: Money,
    ): Int = if (whole.isZero) 0 else (part.rupiah * PERCENT / whole.rupiah).toInt()

    private fun isCreditWallet(wallet: Wallet): Boolean =
        wallet.kind.type == WalletType.CREDIT_CARD || wallet.kind.type == WalletType.PAYLATER

    private fun balanceOf(
        wallet: Wallet,
        balances: Map<WalletId, Money>,
    ): Money = balances[wallet.walletId] ?: wallet.terms.initialBalance

    private fun periodOf(
        period: Period,
        rows: List<Transaction>,
    ): HomePeriod =
        HomePeriod(
            startDate = period.startDate,
            endDate = period.endDate,
            income = totalOfType(rows, TransactionType.INCOME),
            expense = totalOfType(rows, TransactionType.EXPENSE),
        )

    private fun totalOfType(
        rows: List<Transaction>,
        type: TransactionType,
    ): Money =
        rows
            .filter { it.movement.type == type }
            .fold(Money(0L)) { running, row -> running + row.movement.amount }

    // FR-042: expense groups by category, largest first; expenses without a category form their own group
    private fun categoryShares(
        rows: List<Transaction>,
        names: Map<CategoryId, String>,
        appearances: Map<CategoryId, Appearance>,
    ): List<HomeCategoryShare> =
        rows
            .filter { it.movement.type == TransactionType.EXPENSE }
            .groupBy { it.routing.categoryId }
            .map { (categoryId, group) ->
                HomeCategoryShare(
                    name = categoryId?.let { names[it] },
                    amount = group.fold(Money(0L)) { running, row -> running + row.movement.amount },
                    count = group.size,
                    colorKey = categoryId?.let { appearances[it]?.colorKey } ?: "gray",
                )
            }.sortedByDescending { it.amount }

    // FR-043: the five newest transactions, newest first
    private fun recentOf(
        rows: List<Transaction>,
        names: Map<CategoryId, String>,
        appearances: Map<CategoryId, Appearance>,
        walletNames: Map<WalletId, String>,
        clock: Clock,
    ): List<HomeRecent> =
        rows
            .sortedByDescending { it.timing.occurredAt }
            .take(RECENT_COUNT)
            .map { row ->
                HomeRecent(
                    transactionId = row.transactionId,
                    title = titleOf(row, row.routing.categoryId?.let { names[it] }),
                    walletName = walletNames[row.routing.walletId].orEmpty(),
                    whenLabel = whenLabelOf(row.timing.occurredAt, clock),
                    amount = row.movement.amount,
                    type = row.movement.type,
                    appearance = RowAppearance.of(row.movement.type, row.routing.categoryId?.let(appearances::get)),
                    auto = row.origin.source == TransactionSource.NOTIFICATION,
                )
            }

    private fun titleOf(
        transaction: Transaction,
        categoryName: String?,
    ): String? =
        listOf(transaction.memo.merchant, transaction.memo.note, categoryName.orEmpty())
            .firstOrNull { it.isNotBlank() }

    private fun whenLabelOf(
        instant: Instant,
        clock: Clock,
    ): String {
        val local = instant.atZone(clock.zone)
        return "${DateFormatter.dayLabel(local.toLocalDate(), clock)}, ${DateFormatter.timeLabel(local.toLocalTime())}"
    }

    private fun dateOf(
        instant: Instant,
        clock: Clock,
    ): LocalDate = instant.atZone(clock.zone).toLocalDate()
}
