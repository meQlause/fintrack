package com.meqlause.fintrack.feature.transaction.history

import com.meqlause.fintrack.core.domain.period.Period
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Transaction
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.ui.format.DateFormatter
import java.time.Clock
import java.time.LocalDate

private typealias DatedTransaction = Pair<Transaction, LocalDate>

// FR-036, FR-037: keeps the transactions of [period] that pass [filter] and groups them by day, newest first
internal fun buildHistoryContent(
    transactions: List<Transaction>,
    filter: HistoryFilter,
    period: Period,
    names: HistoryNames,
    clock: Clock,
): HistoryContent {
    val dated = transactions.map { it to it.dateIn(clock.zone) }.filter { isInPeriod(it.second, period) }
    val shown = dated.filter { (transaction, date) -> transaction.matches(filter, date) }
    return HistoryContent(
        groups = shown.toDayGroups(names, clock),
        totals = shown.toTotals(),
        hasRecords = dated.isNotEmpty(),
    )
}

// FR-036: income and expense sums; transfers and loans are counted in neither
private fun List<DatedTransaction>.toTotals(): HistoryTotals =
    HistoryTotals(
        income = sumOfType(TransactionType.INCOME),
        expense = sumOfType(TransactionType.EXPENSE),
    )

private fun List<DatedTransaction>.sumOfType(type: TransactionType): Money =
    filter { (transaction, _) -> transaction.movement.type == type }
        .fold(Money(0L)) { sum, (transaction, _) -> sum + transaction.movement.amount }

// FR-036: days newest first; each day lists its rows newest first and carries the day's net
private fun List<DatedTransaction>.toDayGroups(
    names: HistoryNames,
    clock: Clock,
): List<HistoryDayGroup> =
    groupBy { (_, date) -> date }
        .entries
        .sortedByDescending { it.key }
        .map { (date, items) -> dayGroup(date, items, names, clock) }

private fun dayGroup(
    date: LocalDate,
    items: List<DatedTransaction>,
    names: HistoryNames,
    clock: Clock,
): HistoryDayGroup =
    HistoryDayGroup(
        label = DateFormatter.dayLabel(date, clock),
        net = items.toTotals().net,
        rows =
            items
                .sortedByDescending { (transaction, _) -> transaction.timing.occurredAt }
                .map { (transaction, _) -> transaction.toRowItem(names, clock.zone) },
    )
