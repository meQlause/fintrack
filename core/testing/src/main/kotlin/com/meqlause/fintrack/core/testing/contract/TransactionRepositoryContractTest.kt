package com.meqlause.fintrack.core.testing.contract

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Origin
import com.meqlause.fintrack.core.model.Transaction
import com.meqlause.fintrack.core.model.TransactionSource
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.testing.data.aCategory
import com.meqlause.fintrack.core.testing.data.aLoan
import com.meqlause.fintrack.core.testing.data.aTransaction
import com.meqlause.fintrack.core.testing.data.aWallet
import com.meqlause.fintrack.core.testing.data.anInboxItem
import com.meqlause.fintrack.core.testing.data.anInstant
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Test

/** DOC-03, FR-030: the contract of TransactionRepository, run against the fake and the Local implementation. */
abstract class TransactionRepositoryContractTest {
    private val fixture: RepositoryFixture by lazy { createFixture() }

    /** ADR-007: a fresh, empty store for one test. */
    protected abstract fun createFixture(): RepositoryFixture

    /** ADR-007: releases the store after each test. */
    @After
    fun closeFixture() {
        fixture.close()
    }

    /** DOC-02: a fresh store has no transactions. */
    @Test
    fun givenNoTransactionsWhenObservedThenTheListIsEmpty() =
        runTest {
            assertThat(fixture.transactions.observeAll().first()).isEmpty()
        }

    /** BR-001, BR-002: an expense created from an inbox item keeps every field. */
    @Test
    fun givenExpenseFromInboxWhenObservedThenEveryFieldIsKept() =
        runTest {
            val walletId = fixture.wallets.insert(aWallet())
            val categoryId = fixture.categories.insert(aCategory())
            val inboxItemId = fixture.inbox.insert(anInboxItem("fp-transaction-1"))
            val expense =
                aTransaction(TransactionType.EXPENSE, walletId, amount = 25_000L, categoryId = categoryId).let {
                    it.copy(origin = Origin(TransactionSource.NOTIFICATION, inboxItemId))
                }

            val transactionId = fixture.transactions.insert(expense)

            assertThat(
                fixture.transactions.observeAll().first(),
            ).isEqualTo(listOf(expense.copy(transactionId = transactionId)))
        }

    /** BR-003, BR-004: a transfer keeps its target wallet and its fee. */
    @Test
    fun givenTransferWhenObservedThenTargetWalletAndFeeAreKept() =
        runTest {
            val source = fixture.wallets.insert(aWallet(name = "Tunai"))
            val target = fixture.wallets.insert(aWallet(name = "Bank"))
            val transfer =
                aTransaction(TransactionType.TRANSFER, source, amount = 100_000L).let {
                    it.copy(
                        movement = it.movement.copy(feeAmount = Money(rupiah = 2_500L)),
                        routing = it.routing.copy(targetWalletId = target),
                    )
                }

            val transactionId = fixture.transactions.insert(transfer)

            assertThat(
                fixture.transactions.observeAll().first(),
            ).isEqualTo(listOf(transfer.copy(transactionId = transactionId)))
        }

    /** BR-041: a loan repayment keeps the loan it belongs to. */
    @Test
    fun givenLoanRepaymentWhenObservedThenItsLoanIsKept() =
        runTest {
            val walletId = fixture.wallets.insert(aWallet())
            val loanId =
                fixture.loans.insert(
                    aLoan(direction = com.meqlause.fintrack.core.model.LoanDirection.BORROWED),
                )
            val repayment =
                aTransaction(TransactionType.REPAY, walletId, amount = 20_000L).let {
                    it.copy(routing = it.routing.copy(loanId = loanId))
                }

            val transactionId = fixture.transactions.insert(repayment)

            assertThat(
                fixture.transactions.observeAll().first(),
            ).isEqualTo(listOf(repayment.copy(transactionId = transactionId)))
        }

    /** DOC-02: transactions come back in the order they happened, whatever the insert order. */
    @Test
    fun givenTransactionsInsertedOutOfOrderWhenObservedThenOccurredAtDecides() =
        runTest {
            val walletId = fixture.wallets.insert(aWallet())
            fixture.transactions.insert(incomeAt(walletId, offsetMillis = 2_000L))
            fixture.transactions.insert(incomeAt(walletId, offsetMillis = 1_000L))

            val occurrences =
                fixture.transactions
                    .observeAll()
                    .first()
                    .map { it.timing.occurredAt }

            assertThat(
                occurrences,
            ).isEqualTo(listOf(anInstant(offsetMillis = 1_000L), anInstant(offsetMillis = 2_000L)))
        }

    private fun incomeAt(
        walletId: WalletId,
        offsetMillis: Long,
    ): Transaction =
        aTransaction(TransactionType.INCOME, walletId).let {
            it.copy(timing = it.timing.copy(occurredAt = anInstant(offsetMillis = offsetMillis)))
        }
}
