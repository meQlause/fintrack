package com.meqlause.fintrack.feature.budget

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import assertk.assertions.isTrue
import com.meqlause.fintrack.core.domain.usecase.ObserveBudgetStatusUseCase
import com.meqlause.fintrack.core.domain.usecase.SetBudgetLimitUseCase
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.Category
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.CategoryKind
import com.meqlause.fintrack.core.model.CategoryProfile
import com.meqlause.fintrack.core.model.CategoryState
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

    /** FR-070: editing an existing budget opens the keypad with its limit already typed. */
    @Test
    fun `given a stored limit when it is edited then the keypad starts with its digits (FR-070)`() =
        runTest(dispatcher) {
            val viewModel = newViewModel()
            val collector = launch { viewModel.state.collect {} }
            viewModel.pick(BudgetTarget(categoryId = null, categoryName = null))
            "250000".forEach { viewModel.typeDigit(it.toString()) }
            viewModel.save()
            advanceUntilIdle()
            val overall = requireNotNull(viewModel.state.value.overall)
            viewModel.startEdit(overall)
            advanceUntilIdle()
            val editor = viewModel.state.value.editor as BudgetEditor.Amount
            assertThat(editor.digits).isEqualTo("250000")
            collector.cancel()
        }

    /** FR-070: backspace erases the last digit, and digits stop at the twelve of BR-001. */
    @Test
    fun `given the keypad when digits are erased and overflowed then the amount stays within bounds (BR-001)`() =
        runTest(dispatcher) {
            val viewModel = newViewModel()
            val collector = launch { viewModel.state.collect {} }
            viewModel.pick(BudgetTarget(categoryId = null, categoryName = null))
            "1234".forEach { viewModel.typeDigit(it.toString()) }
            viewModel.backspace()
            advanceUntilIdle()
            assertThat((viewModel.state.value.editor as BudgetEditor.Amount).digits).isEqualTo("123")
            repeat(MORE_THAN_MAX) { viewModel.typeDigit("9") }
            advanceUntilIdle()
            assertThat((viewModel.state.value.editor as BudgetEditor.Amount).digits.length).isEqualTo(MAX_DIGITS)
            collector.cancel()
        }

    /** FR-070: digits, backspace and save do nothing before an amount keypad is open. */
    @Test
    fun `given no amount keypad when keys are pressed then nothing changes and dismiss closes the picker (FR-070)`() =
        runTest(dispatcher) {
            val viewModel = newViewModel()
            val collector = launch { viewModel.state.collect {} }
            viewModel.typeDigit("5")
            viewModel.backspace()
            viewModel.save()
            advanceUntilIdle()
            assertThat(viewModel.state.value.editor).isNull()
            viewModel.startAdd()
            viewModel.typeDigit("5")
            viewModel.backspace()
            advanceUntilIdle()
            assertThat(viewModel.state.value.editor).isEqualTo(BudgetEditor.Pick)
            viewModel.dismissEditor()
            advanceUntilIdle()
            assertThat(viewModel.state.value.editor).isNull()
            collector.cancel()
        }

    /** FR-072: a category limit is listed by the category name; the picker offers live expense categories only. */
    @Test
    fun `given categories when a limit is stored then rows carry names and the picker hides others (FR-072)`() =
        runTest(dispatcher) {
            val food = categories.insert(category("Makanan", CategoryKind.EXPENSE))
            categories.insert(category("Gaji", CategoryKind.INCOME))
            categories.insert(category("Lama", CategoryKind.EXPENSE, archived = true))
            val viewModel = newViewModel()
            val collector = launch { viewModel.state.collect {} }
            viewModel.pick(BudgetTarget(food, "Makanan"))
            "100000".forEach { viewModel.typeDigit(it.toString()) }
            viewModel.save()
            advanceUntilIdle()
            val state = viewModel.state.value
            assertThat(state.rows).hasSize(1)
            assertThat(
                state.rows
                    .single()
                    .target.categoryName,
            ).isEqualTo("Makanan")
            assertThat(state.categories).hasSize(1)
            collector.cancel()
        }

    private fun category(
        name: String,
        kind: CategoryKind,
        archived: Boolean = false,
    ): Category =
        Category(
            categoryId = CategoryId(0L),
            profile = CategoryProfile(name, kind, Appearance("food", "orange"), sortOrder = 0),
            state = CategoryState(isDefault = false, isArchived = archived),
            audit = Audit(Instant.EPOCH, Instant.EPOCH),
        )

    private companion object {
        const val CATEGORY_ID = 10L
        const val MAX_DIGITS = 12
        const val MORE_THAN_MAX = 20
    }
}
