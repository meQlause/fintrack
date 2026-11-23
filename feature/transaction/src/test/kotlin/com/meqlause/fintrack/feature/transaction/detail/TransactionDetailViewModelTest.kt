package com.meqlause.fintrack.feature.transaction.detail

import androidx.lifecycle.SavedStateHandle
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
import com.meqlause.fintrack.core.testing.coroutine.MainDispatcherRule
import com.meqlause.fintrack.core.testing.repository.FakeCategoryRepository
import com.meqlause.fintrack.core.testing.repository.FakeTransactionRepository
import com.meqlause.fintrack.core.testing.repository.FakeWalletRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

// FR-034, FR-035, FR-038, S-06: the detail ViewModel over fakes; the clock is fixed on 23 Nov 2026 in Jakarta
@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class TransactionDetailViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val clock: Clock = Clock.fixed(Instant.parse("2026-11-23T03:00:00Z"), ZoneId.of("Asia/Jakarta"))
    private val transactions = FakeTransactionRepository()
    private val wallets = FakeWalletRepository(transactions)
    private val categories = FakeCategoryRepository()

    private fun viewModel(rowId: Long): TransactionDetailViewModel =
        TransactionDetailViewModel(
            savedStateHandle = SavedStateHandle(mapOf("transactionId" to rowId)),
            transactions = transactions,
            wallets = wallets,
            categories = categories,
            clock = clock,
        )

    /** FR-034: the stored expense is shown with its merchant and amount. */
    @Test
    fun givenStoredExpenseWhenOpenedThenDetailShowsMerchantAndAmount() =
        runTest {
            val id = transactions.insert(expense(merchant = "Warung Bu Tini")).value
            val state = viewModel(id).state.first { it.isLoaded }
            assertEquals("Warung Bu Tini", state.detail?.title)
            assertEquals(Money(25_000L), state.detail?.amount)
        }

    /** S-06: an id that no stored row carries leaves the detail empty. */
    @Test
    fun givenUnknownIdWhenOpenedThenDetailIsMissing() =
        runTest {
            val state = viewModel(99L).state.first { it.isLoaded }
            assertNull(state.detail)
        }

    /** FR-035: Undo inside the window keeps the transaction and closes nothing. */
    @Test
    fun givenDeleteRequestWhenUndoneThenTransactionIsKept() =
        runTest {
            val id = transactions.insert(expense(merchant = "Warung")).value
            val vm = viewModel(id)
            vm.requestDelete()
            vm.undoDelete()
            advanceUntilIdle()
            assertEquals(1, transactions.observeAll().first().size)
            assertTrue(!vm.state.first { it.isLoaded }.isClosed)
        }

    /** FR-035: once the undo window passes, the transaction is removed and the screen closes. */
    @Test
    fun givenDeleteRequestWhenWindowPassesThenTransactionIsRemovedAndClosed() =
        runTest {
            val id = transactions.insert(expense(merchant = "Warung")).value
            val vm = viewModel(id)
            vm.requestDelete()
            advanceUntilIdle()
            assertTrue(transactions.observeAll().first().isEmpty())
            assertTrue(vm.state.first { it.isLoaded }.isClosed)
        }

    /** FR-038: a duplicate is a manual copy dated now; the original stays as it was. */
    @Test
    fun givenExpenseWhenDuplicatedThenCopyIsDatedNowAsManualEntry() =
        runTest {
            val id = transactions.insert(expense(merchant = "Warung")).value
            viewModel(id).duplicate()
            advanceUntilIdle()
            val rows = transactions.observeAll().first()
            assertEquals(2, rows.size)
            assertEquals(Instant.parse("2026-11-23T03:00:00Z"), rows.last().timing.occurredAt)
            assertEquals(TransactionSource.MANUAL, rows.last().origin.source)
        }

    private fun expense(merchant: String): Transaction {
        val created = Instant.parse("2026-11-23T02:00:00Z")
        return Transaction(
            transactionId = TransactionId(0L),
            movement = Movement(type = TransactionType.EXPENSE, amount = Money(25_000L)),
            routing =
                Routing(
                    walletId = WalletId(1L),
                    targetWalletId = null,
                    categoryId = CategoryId(1L),
                    loanId = null,
                ),
            memo = Memo(note = "Makan siang", merchant = merchant),
            origin = Origin(source = TransactionSource.MANUAL, inboxItemId = null),
            timing = Timing(occurredAt = created, audit = Audit(createdAt = created, updatedAt = created)),
        )
    }
}
