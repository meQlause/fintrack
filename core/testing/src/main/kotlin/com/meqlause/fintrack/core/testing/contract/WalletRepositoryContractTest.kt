package com.meqlause.fintrack.core.testing.contract

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isGreaterThan
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.CreditTerms
import com.meqlause.fintrack.core.model.LoanId
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Transaction
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.testing.data.aCategory
import com.meqlause.fintrack.core.testing.data.aLoan
import com.meqlause.fintrack.core.testing.data.aTransaction
import com.meqlause.fintrack.core.testing.data.aWallet
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Test

/** DOC-03, FR-010: the contract of WalletRepository, run against the fake and the Local implementation. */
abstract class WalletRepositoryContractTest {
    private val fixture: RepositoryFixture by lazy { createFixture() }

    /** ADR-007: a fresh, empty store for one test. */
    protected abstract fun createFixture(): RepositoryFixture

    /** ADR-007: releases the store after each test. */
    @After
    fun closeFixture() {
        fixture.close()
    }

    /** FR-010: a fresh store has no wallets. */
    @Test
    fun givenNoWalletsWhenObservedThenTheListIsEmpty() =
        runTest {
            assertThat(fixture.wallets.observeAll().first()).isEmpty()
        }

    /** FR-010: an inserted wallet comes back unchanged except for its generated id. */
    @Test
    fun givenInsertedWalletWhenObservedThenItIsReturnedWithItsGeneratedId() =
        runTest {
            val wallet = aWallet(name = "Tunai", initialBalance = 75_000L)

            val walletId = fixture.wallets.insert(wallet)

            assertThat(fixture.wallets.observeAll().first()).isEqualTo(listOf(wallet.copy(walletId = walletId)))
        }

    /** BR-031, BR-032: credit terms and statement days survive the round trip. */
    @Test
    fun givenCreditWalletWhenObservedThenCreditTermsAreKept() =
        runTest {
            val credit = CreditTerms(creditLimit = Money(rupiah = 5_000_000L), statementDay = 10, dueDay = 25)
            val wallet = aWallet(name = "Kartu", initialBalance = -200_000L, credit = credit)

            val walletId = fixture.wallets.insert(wallet)

            assertThat(fixture.wallets.observeAll().first()).isEqualTo(listOf(wallet.copy(walletId = walletId)))
        }

    /** FR-010: wallets come back in manual order, and later inserts get larger ids. */
    @Test
    fun givenWalletsInsertedOutOfOrderWhenObservedThenManualOrderDecides() =
        runTest {
            val second = fixture.wallets.insert(aWallet(name = "Bank", sortOrder = 2))
            val first = fixture.wallets.insert(aWallet(name = "Tunai", sortOrder = 1))

            val names =
                fixture.wallets
                    .observeAll()
                    .first()
                    .map { it.profile.name }

            assertThat(names).isEqualTo(listOf("Tunai", "Bank"))
            assertThat(first.value).isGreaterThan(second.value)
        }

    /** BR-008: a deleted wallet disappears and its id is never handed out again. */
    @Test
    fun givenWalletDeletedWhenObservedThenItIsGoneAndItsIdIsNotReused() =
        runTest {
            val deleted = aWallet(name = "Lama")
            val oldId = fixture.wallets.insert(deleted)
            fixture.wallets.delete(deleted.copy(walletId = oldId))

            val newId = fixture.wallets.insert(aWallet(name = "Baru"))

            assertThat(
                fixture.wallets
                    .observeAll()
                    .first()
                    .map { it.profile.name },
            ).isEqualTo(listOf("Baru"))
            assertThat(newId.value).isGreaterThan(oldId.value)
        }

    /** BR-043: a wallet without transactions shows its opening balance. */
    @Test
    fun givenWalletWithoutTransactionsWhenBalancesObservedThenItShowsItsOpeningBalance() =
        runTest {
            val walletId = fixture.wallets.insert(aWallet(initialBalance = 40_000L))

            assertThat(fixture.wallets.observeBalances().first()).isEqualTo(mapOf(walletId to Money(rupiah = 40_000L)))
        }

    /** BR-004, BR-040: each transaction type moves the balances of its wallets exactly once. */
    @Test
    fun givenTransactionsOfEveryTypeWhenBalancesObservedThenEachMovesItsWalletsOnce() =
        runTest {
            val cash = fixture.wallets.insert(aWallet(name = "Tunai", initialBalance = 1_000_000L, sortOrder = 1))
            val bank = fixture.wallets.insert(aWallet(name = "Bank", initialBalance = 200_000L, sortOrder = 2))
            val category = fixture.categories.insert(aCategory())
            val loan = fixture.loans.insert(aLoan())
            insertMovements(cash, bank, category, loan)

            assertThat(fixture.wallets.observeBalances().first())
                .isEqualTo(mapOf(cash to Money(rupiah = 1_217_500L), bank to Money(rupiah = 300_000L)))
        }

    private suspend fun insertMovements(
        cash: WalletId,
        bank: WalletId,
        category: CategoryId,
        loan: LoanId,
    ) {
        val transactions = fixture.transactions
        transactions.insert(aTransaction(TransactionType.INCOME, cash, amount = 500_000L, categoryId = category))
        transactions.insert(aTransaction(TransactionType.EXPENSE, cash, amount = 150_000L, categoryId = category))
        transactions.insert(transfer(from = cash, target = bank, amount = 100_000L, feeAmount = 2_500L))
        transactions.insert(loanMovement(TransactionType.LEND, cash, amount = 50_000L, loan = loan))
        transactions.insert(loanMovement(TransactionType.BORROW, cash, amount = 30_000L, loan = loan))
        transactions.insert(loanMovement(TransactionType.COLLECT, cash, amount = 10_000L, loan = loan))
        transactions.insert(loanMovement(TransactionType.REPAY, cash, amount = 20_000L, loan = loan))
    }

    private fun transfer(
        from: WalletId,
        target: WalletId,
        amount: Long,
        feeAmount: Long,
    ): Transaction {
        val base = aTransaction(TransactionType.TRANSFER, from, amount)
        return base.copy(
            movement = base.movement.copy(feeAmount = Money(feeAmount)),
            routing = base.routing.copy(targetWalletId = target),
        )
    }

    private fun loanMovement(
        type: TransactionType,
        walletId: WalletId,
        amount: Long,
        loan: LoanId,
    ): Transaction {
        val base = aTransaction(type, walletId, amount)
        return base.copy(routing = base.routing.copy(loanId = loan))
    }
}
