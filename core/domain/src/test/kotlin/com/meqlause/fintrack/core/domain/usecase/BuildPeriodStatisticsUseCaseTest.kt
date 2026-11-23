package com.meqlause.fintrack.core.domain.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.meqlause.fintrack.core.domain.period.Period
import com.meqlause.fintrack.core.domain.statistics.StatisticsRequest
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.Memo
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Movement
import com.meqlause.fintrack.core.model.Origin
import com.meqlause.fintrack.core.model.Routing
import com.meqlause.fintrack.core.model.Timing
import com.meqlause.fintrack.core.model.Transaction
import com.meqlause.fintrack.core.model.TransactionId
import com.meqlause.fintrack.core.model.TransactionSource
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.WalletId
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

// FR-060..062: statistics math over the November period, which starts on day 1

/** Build period statistics use case tests. */
class BuildPeriodStatisticsUseCaseTest {
    private val zone: ZoneId = ZoneId.of("Asia/Jakarta")
    private val november = Period(LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 30))
    private val useCase = BuildPeriodStatisticsUseCase()

    /** FR-060: only expenses are totalled and the categories are ranked largest first */
    @Test
    fun givenExpensesAndIncomeWhenBuiltThenOnlyExpensesAreRankedLargestFirst() {
        val transactions =
            listOf(
                transaction(TransactionType.EXPENSE, 20_000L, LocalDate.of(2026, 11, 3), 1L),
                transaction(TransactionType.EXPENSE, 40_000L, LocalDate.of(2026, 11, 9), 1L),
                transaction(TransactionType.EXPENSE, 50_000L, LocalDate.of(2026, 11, 5), 2L),
                transaction(TransactionType.INCOME, 9_000_000L, LocalDate.of(2026, 11, 4), 3L),
            )
        val stats = build(transactions, TransactionType.EXPENSE, november)
        assertThat(stats.total).isEqualTo(Money(110_000L))
        assertThat(stats.categories.map { it.categoryId }).isEqualTo(listOf(CategoryId(1L), CategoryId(2L)))
        assertThat(stats.categories.first().amount).isEqualTo(Money(60_000L))
    }

    /** FR-062: each category and the total are compared with the previous period */
    @Test
    fun givenPreviousMonthExpensesWhenBuiltThenChangesAreComparedPerCategory() {
        val transactions =
            listOf(
                transaction(TransactionType.EXPENSE, 150_000L, LocalDate.of(2026, 11, 3), 1L),
                transaction(TransactionType.EXPENSE, 100_000L, LocalDate.of(2026, 10, 3), 1L),
            )
        val stats = build(transactions, TransactionType.EXPENSE, november)
        assertThat(stats.previousTotal).isEqualTo(Money(100_000L))
        assertThat(stats.totalPercentChange).isEqualTo(50L)
        assertThat(stats.categories.single().percentChange).isEqualTo(50L)
    }

    /** FR-062: a category without an amount in the previous period has no percent change */
    @Test
    fun givenCategoryNewThisPeriodWhenBuiltThenPercentChangeIsNull() {
        val transactions =
            listOf(
                transaction(TransactionType.EXPENSE, 30_000L, LocalDate.of(2026, 11, 3), 7L),
                transaction(TransactionType.EXPENSE, 10_000L, LocalDate.of(2026, 10, 3), 8L),
            )
        val stats = build(transactions, TransactionType.EXPENSE, november)
        assertThat(stats.categories.single { it.categoryId == CategoryId(7L) }.percentChange).isNull()
    }

    /** FR-061: one sum per day of the period, with zero for days without money */
    @Test
    fun givenExpensesOnTwoDaysWhenBuiltThenDailyTotalsCoverEveryDay() {
        val transactions =
            listOf(
                transaction(TransactionType.EXPENSE, 20_000L, LocalDate.of(2026, 11, 3), null),
                transaction(TransactionType.EXPENSE, 50_000L, LocalDate.of(2026, 11, 5), null),
            )
        val daily = build(transactions, TransactionType.EXPENSE, november).dailyTotals
        assertThat(daily.size).isEqualTo(30)
        assertThat(daily[2]).isEqualTo(Money(20_000L))
        assertThat(daily[4]).isEqualTo(Money(50_000L))
        assertThat(daily[0]).isEqualTo(Money(0L))
    }

    private fun build(
        transactions: List<Transaction>,
        type: TransactionType,
        period: Period,
    ) = useCase(
        transactions = transactions,
        request = StatisticsRequest(type = type, period = period, startDay = 1, zone = zone),
    )

    private fun transaction(
        type: TransactionType,
        amount: Long,
        date: LocalDate,
        categoryId: Long?,
    ): Transaction =
        Transaction(
            transactionId = TransactionId(0L),
            movement = Movement(type = type, amount = Money(amount)),
            routing =
                Routing(
                    walletId = WalletId(1L),
                    targetWalletId = null,
                    categoryId = categoryId?.let { CategoryId(it) },
                    loanId = null,
                ),
            memo = Memo(),
            origin = Origin(source = TransactionSource.MANUAL, inboxItemId = null),
            timing = Timing(occurredAt = date.atTime(5, 0).atZone(zone).toInstant(), audit = audit()),
        )

    private fun audit(): Audit = Audit(createdAt = Instant.EPOCH, updatedAt = Instant.EPOCH)
}
