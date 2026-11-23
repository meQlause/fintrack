package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.domain.period.Period
import com.meqlause.fintrack.core.domain.period.PeriodCalculator
import com.meqlause.fintrack.core.domain.statistics.CategoryChange
import com.meqlause.fintrack.core.domain.statistics.PeriodStatistics
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Transaction
import com.meqlause.fintrack.core.model.TransactionType
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject

/** FR-060..062: totals, category ranking, daily sums and the previous-period comparison of one transaction type. */
public class BuildPeriodStatisticsUseCase
    @Inject
    constructor() {
        /** FR-051: [period] is the one [startDay] produces; transfers and loans are never counted. */
        public operator fun invoke(
            transactions: List<Transaction>,
            type: TransactionType,
            period: Period,
            startDay: Int,
            zone: ZoneId,
        ): PeriodStatistics {
            val previous = PeriodCalculator.previousPeriod(period, startDay)
            val ofType = transactions.filter { it.movement.type == type }
            val current = ofType.during(period, zone)
            val before = ofType.during(previous, zone)
            return PeriodStatistics(
                type = type,
                total = sumOf(current),
                previousTotal = sumOf(before),
                categories = rankCategories(current, before),
                dailyTotals = dailyTotals(current, period, zone),
            )
        }

        // FR-060: categories with money in the period, largest first; each carries its previous-period amount
        private fun rankCategories(
            current: List<Transaction>,
            before: List<Transaction>,
        ): List<CategoryChange> {
            val previousByCategory = amountsByCategory(before)
            return amountsByCategory(current)
                .map { (categoryId, amount) ->
                    CategoryChange(categoryId, amount, previousByCategory[categoryId] ?: Money(0L))
                }.sortedByDescending { it.amount.rupiah }
        }

        // FR-061: the sum of each day of the period, the first day of the period at index 0
        private fun dailyTotals(
            current: List<Transaction>,
            period: Period,
            zone: ZoneId,
        ): List<Money> {
            val days = ChronoUnit.DAYS.between(period.startDate, period.endDate).toInt() + 1
            val sums = MutableList(days) { Money(0L) }
            current.forEach { transaction ->
                val index = ChronoUnit.DAYS.between(period.startDate, transaction.localDate(zone)).toInt()
                sums[index] = sums[index] + transaction.movement.amount
            }
            return sums
        }
    }

private fun List<Transaction>.during(
    period: Period,
    zone: ZoneId,
): List<Transaction> = filter { it.localDate(zone) in period.startDate..period.endDate }

private fun Transaction.localDate(zone: ZoneId): LocalDate = timing.occurredAt.atZone(zone).toLocalDate()

private fun sumOf(transactions: List<Transaction>): Money =
    transactions.fold(Money(0L)) { sum, transaction -> sum + transaction.movement.amount }

private fun amountsByCategory(transactions: List<Transaction>): Map<CategoryId?, Money> =
    transactions
        .groupBy { it.routing.categoryId }
        .mapValues { (_, items) -> sumOf(items) }
