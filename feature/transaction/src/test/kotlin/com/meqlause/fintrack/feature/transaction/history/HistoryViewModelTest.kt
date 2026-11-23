package com.meqlause.fintrack.feature.transaction.history

import com.meqlause.fintrack.core.model.Audit
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
import com.meqlause.fintrack.core.testing.coroutine.MainDispatcherRule
import com.meqlause.fintrack.core.testing.repository.FakeCategoryRepository
import com.meqlause.fintrack.core.testing.repository.FakeSettingsRepository
import com.meqlause.fintrack.core.testing.repository.FakeTransactionRepository
import com.meqlause.fintrack.core.testing.repository.FakeWalletRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.YearMonth
import java.time.ZoneId

// FR-036, FR-037, S-05: the Riwayat ViewModel over fakes; the clock is fixed on 20 Nov 2026 in Jakarta
class HistoryViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val zone: ZoneId = ZoneId.of("Asia/Jakarta")
    private val clock: Clock = Clock.fixed(Instant.parse("2026-11-20T03:00:00Z"), zone)
    private val transactions = FakeTransactionRepository()
    private val wallets = FakeWalletRepository(transactions)
    private val categories = FakeCategoryRepository()
    private val settings = FakeSettingsRepository()

    private fun viewModel(): HistoryViewModel = HistoryViewModel(transactions, wallets, categories, settings, clock)

    /** FR-036: a November expense is listed under its day with the day's net and the month's expense total. */
    @Test
    fun givenExpenseInNovemberWhenOpenedThenRowAndTotalsAreShown() =
        runTest {
            transactions.insert(expense(note = "Makan siang", merchant = "Warung Bu Tini", day = 18))
            val state = viewModel().state.first { it.isLoaded }
            assertEquals(1, state.groups.size)
            assertEquals(
                "Warung Bu Tini",
                state.groups
                    .single()
                    .rows
                    .single()
                    .title,
            )
            assertEquals(Money(25_000L), state.totals.expense)
        }

    /** FR-037: the search matches note and merchant text, ignoring case. */
    @Test
    fun givenTwoExpensesWhenSearchedByMerchantThenOnlyMatchingRowIsShown() =
        runTest {
            transactions.insert(expense(note = "Kopi", merchant = "Kopi Kenangan", day = 18))
            transactions.insert(expense(note = "Bensin", merchant = "Pertamina", day = 19))
            val model = viewModel()
            model.onEvent(HistoryEvent.SearchToggled)
            model.onEvent(HistoryEvent.QueryChanged("KOPI"))
            val state = model.state.first { it.filter.query == "KOPI" && it.isLoaded }
            assertEquals(
                1,
                state.groups
                    .single()
                    .rows.size,
            )
        }

    /** FR-037: a wallet filter keeps transfers that touch the chosen wallet on either side. */
    @Test
    fun givenTransferToWalletWhenWalletFilterSetThenTransferIsShown() =
        runTest {
            transactions.insert(transfer(from = 1L, to = 2L, day = 18))
            transactions.insert(expense(note = "Lain", merchant = "Toko", day = 18, wallet = 3L))
            val model = viewModel()
            model.onEvent(HistoryEvent.WalletPicked(WalletId(2L)))
            val state = model.state.first { it.filter.walletId == WalletId(2L) && it.isLoaded }
            assertEquals(
                1,
                state.groups
                    .single()
                    .rows.size,
            )
        }

    /** S-05: a horizontal swipe is modelled as NextMonth and moves the picked month and its period forward. */
    @Test
    fun givenNovemberWhenNextMonthThenDecemberIsPickedAndPeriodFollows() =
        runTest {
            val model = viewModel()
            model.onEvent(HistoryEvent.NextMonth)
            val state = model.state.first { it.month == YearMonth.of(2026, 12) && it.isLoaded }
            assertEquals(YearMonth.of(2026, 12), state.period?.startDate?.let { YearMonth.from(it) })
        }

    /** FR-036: a month with no transactions has no groups and no records, which drives the empty state. */
    @Test
    fun givenNoTransactionsWhenOpenedThenListIsEmptyWithoutRecords() =
        runTest {
            val state = viewModel().state.first { it.isLoaded }
            assertTrue(state.groups.isEmpty())
            assertTrue(!state.hasRecords)
        }

    /** FR-037: choosing the same day twice clears the day filter. */
    @Test
    fun givenDayPickedTwiceThenDayFilterIsCleared() {
        val picked = HistorySelection(month = YearMonth.of(2026, 11)).reduce(HistoryEvent.DayPicked(18))
        val cleared = picked.reduce(HistoryEvent.DayPicked(18))
        assertEquals(18, picked.filter.day)
        assertEquals(null, cleared.filter.day)
    }

    private suspend fun expense(
        note: String,
        merchant: String,
        day: Int,
        wallet: Long = 1L,
    ): Transaction =
        Transaction(
            transactionId = TransactionId(0L),
            movement = Movement(type = TransactionType.EXPENSE, amount = Money(25_000L)),
            routing = Routing(walletId = WalletId(wallet), targetWalletId = null, categoryId = null, loanId = null),
            memo = Memo(note = note, merchant = merchant),
            origin = Origin(source = TransactionSource.MANUAL, inboxItemId = null),
            timing = Timing(occurredAt = at(day), audit = audit()),
        )

    private fun transfer(
        from: Long,
        to: Long,
        day: Int,
    ): Transaction =
        Transaction(
            transactionId = TransactionId(0L),
            movement = Movement(type = TransactionType.TRANSFER, amount = Money(100_000L)),
            routing =
                Routing(
                    walletId = WalletId(from),
                    targetWalletId = WalletId(to),
                    categoryId = null,
                    loanId = null,
                ),
            memo = Memo(),
            origin = Origin(source = TransactionSource.MANUAL, inboxItemId = null),
            timing = Timing(occurredAt = at(day), audit = audit()),
        )

    private fun at(day: Int): Instant = Instant.parse("2026-11-%02dT05:00:00Z".format(day))

    private fun audit(): Audit = Audit(createdAt = at(1), updatedAt = at(1))
}
