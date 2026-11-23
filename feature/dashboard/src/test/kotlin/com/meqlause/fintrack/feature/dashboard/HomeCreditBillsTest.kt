package com.meqlause.fintrack.feature.dashboard

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.meqlause.fintrack.core.testing.repository.FakeTransactionRepository
import com.meqlause.fintrack.core.testing.repository.FakeWalletRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeCreditBillsTest : HomeViewModelTestBase() {
    @Test
    fun givenCreditWalletWhenOpenedThenBillsListItAndChipIsCredit() =
        runTest {
            val transactions = FakeTransactionRepository()
            val wallets = FakeWalletRepository(transactions)
            wallets.insert(creditWallet("BCA Kredit"))
            val viewModel = viewModel(wallets, transactions)
            val collector = backgroundScope.launch { viewModel.state.collect {} }
            advanceUntilIdle()
            val state = viewModel.state.value as HomeUiState.Success
            assertThat(state.bills.bills.map { it.name }).isEqualTo(listOf("BCA Kredit"))
            assertThat(state.wallets.single().isCredit).isEqualTo(true)
            collector.cancel()
        }
}
