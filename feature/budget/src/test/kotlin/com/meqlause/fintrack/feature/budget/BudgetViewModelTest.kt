package com.meqlause.fintrack.feature.budget

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import assertk.assertions.isTrue
import com.meqlause.fintrack.core.domain.usecase.ObserveBudgetStatusUseCase
import com.meqlause.fintrack.core.domain.usecase.SetBudgetLimitUseCase
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.testing.repository.FakeBudgetRepository
import com.meqlause.fintrack.core.testing.repository.FakeCategoryRepository
import com.meqlause.fintrack.core.testing.repository.FakeSettingsRepository
import com.meqlause.fintrack.core.testing.repository.FakeTransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

// FR-070..074: BudgetViewModel against the fake repositories
@OptIn(ExperimentalCoroutinesApi::class)
class BudgetViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val budgets = FakeBudgetRepository()
    private val categories = FakeCategoryRepository()
    private val clock = Clock.fixed(Instant.parse("2026-11-15T10:00:00Z"), ZoneId.of("UTC"))

    @Before
    fun setMainDispatcher() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun resetMainDispatcher() {
        Dispatchers.resetMain()
    }

    private fun newViewModel(): BudgetViewModel =
        BudgetViewModel(
            observeBudgets =
                ObserveBudgetStatusUseCase(budgets, FakeTransactionRepository(), FakeSettingsRepository(), clock),
            categories = categories,
            setLimit = SetBudgetLimitUseCase(budgets, clock),
        )

    @Test
    fun `given no budget when the screen opens then it is loaded and has no overall card (FR-074)`() =
        runTest(dispatcher) {
            val viewModel = newViewModel()
            val collector = launch { viewModel.state.collect {} }
            advanceUntilIdle()
            assertThat(viewModel.state.value.loaded).isTrue()
            assertThat(viewModel.state.value.overall).isNull()
            collector.cancel()
        }

    @Test
    fun `given the keypad when 500000 is typed and saved then the overall limit is stored (FR-070)`() =
        runTest(dispatcher) {
            val viewModel = newViewModel()
            val collector = launch { viewModel.state.collect {} }
            viewModel.startAdd()
            viewModel.pick(BudgetTarget(categoryId = null, categoryName = null))
            listOf("5", "0", "0", "0", "0", "0").forEach(viewModel::typeDigit)
            viewModel.save()
            advanceUntilIdle()
            assertThat(
                viewModel.state.value.overall
                    ?.status
                    ?.budget
                    ?.limitAmount,
            ).isEqualTo(Money(500_000L))
            assertThat(viewModel.state.value.editor).isNull()
            collector.cancel()
        }

    @Test
    fun `given a zero amount when saved then nothing is stored and the keypad stays open (BR-001)`() =
        runTest(dispatcher) {
            val viewModel = newViewModel()
            val collector = launch { viewModel.state.collect {} }
            viewModel.pick(BudgetTarget(CategoryId(CATEGORY_ID), "Makanan"))
            viewModel.save()
            advanceUntilIdle()
            assertThat(budgets.observeAll().first()).isEmpty()
            assertThat(viewModel.state.value.editor).isNotNull()
            collector.cancel()
        }

    private companion object {
        const val CATEGORY_ID = 10L
    }
}
