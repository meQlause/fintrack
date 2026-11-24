package com.meqlause.fintrack.feature.transaction.edit

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import com.meqlause.fintrack.core.domain.category.CategoryError
import com.meqlause.fintrack.core.domain.usecase.CreateCategoryUseCase
import com.meqlause.fintrack.core.domain.usecase.SaveInstalmentPurchaseUseCase
import com.meqlause.fintrack.core.domain.usecase.SaveLoanUseCase
import com.meqlause.fintrack.core.domain.usecase.SaveQuickEntryUseCase
import com.meqlause.fintrack.core.domain.usecase.SaveTransactionUseCase
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.Category
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.CategoryKind
import com.meqlause.fintrack.core.model.CategoryProfile
import com.meqlause.fintrack.core.model.CategoryState
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.testing.coroutine.MainDispatcherRule
import com.meqlause.fintrack.core.testing.repository.FakeCategoryRepository
import com.meqlause.fintrack.core.testing.repository.FakeInstallmentPlanRepository
import com.meqlause.fintrack.core.testing.repository.FakeLoanRepository
import com.meqlause.fintrack.core.testing.repository.FakeQuickEntryRepository
import com.meqlause.fintrack.core.testing.repository.FakeSettingsRepository
import com.meqlause.fintrack.core.testing.repository.FakeTransactionRepository
import com.meqlause.fintrack.core.testing.repository.FakeWalletRepository
import com.meqlause.fintrack.core.testing.repository.PassthroughAtomicRunner
import com.meqlause.fintrack.core.testing.time.FakeClock
import com.meqlause.fintrack.feature.transaction.TransactionSources
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.time.Instant

// FR-021, FR-030, FR-031, S-04: the category drawer lists, picks and adds categories of the open type
@OptIn(ExperimentalCoroutinesApi::class)
class EditCategoryDrawerTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val clock = FakeClock(Instant.parse("2026-11-24T07:05:00Z"))
    private val transactions = FakeTransactionRepository()
    private val wallets = FakeWalletRepository(transactions)
    private val categories = FakeCategoryRepository()
    private val quick = FakeQuickEntryRepository()
    private val saveTransaction = SaveTransactionUseCase(transactions, wallets, categories, clock)

    private fun viewModel(): EditTransactionViewModel =
        EditTransactionViewModel(
            sources = TransactionSources(transactions, wallets, categories, FakeSettingsRepository()),
            instalments =
                EditInstalmentController(
                    saveTransaction,
                    SaveInstalmentPurchaseUseCase(
                        saveTransaction,
                        wallets,
                        categories,
                        FakeInstallmentPlanRepository(),
                        PassthroughAtomicRunner(),
                        clock,
                    ),
                ),
            loans =
                EditLoanController(
                    SaveLoanUseCase(FakeLoanRepository(), transactions, PassthroughAtomicRunner(), clock),
                    clock,
                ),
            shortcutDeps = EditShortcutDeps(quick, SaveQuickEntryUseCase(quick, categories, wallets)),
            createCategory = CreateCategoryUseCase(categories, clock),
            clock = clock,
        )

    private suspend fun seed(
        name: String,
        kind: CategoryKind,
        order: Int,
    ): CategoryId =
        categories.insert(
            Category(
                categoryId = CategoryId(0L),
                profile = CategoryProfile(name, kind, Appearance("food", "orange"), order),
                state = CategoryState(isDefault = true, isArchived = false),
                audit = Audit(clock.instant(), clock.instant()),
            ),
        )

    private suspend fun openedSheet(): EditTransactionViewModel {
        seed("Makan", CategoryKind.EXPENSE, 1)
        seed("Gaji", CategoryKind.INCOME, 0)
        seed("Bensin", CategoryKind.EXPENSE, 0)
        val model = viewModel()
        model.open(null)
        model.onEvent(EditTransactionEvent.SheetOpened(EditSheet.CATEGORY))
        return model
    }

    /** S-04: the drawer lists only the categories of the open type, in sort order. */
    @Test
    fun givenExpenseAndIncomeWhenDrawerIsOpenThenOnlyTheOpenTypeIsListedInOrder() =
        runTest {
            val model = openedSheet()
            advanceUntilIdle()
            assertThat(model.state.value.openSheet).isEqualTo(EditSheet.CATEGORY)
            assertThat(
                model.state.value.categoryChoices
                    .map { it.profile.name },
            ).containsExactly("Bensin", "Makan")
            model.onEvent(EditTransactionEvent.TypeSelected(TransactionType.INCOME))
            assertThat(
                model.state.value.categoryChoices
                    .map { it.profile.name },
            ).containsExactly("Gaji")
        }

    /** FR-030: picking a category sets it and closes the drawer. */
    @Test
    fun givenOpenDrawerWhenPickingThenCategoryIsSetAndDrawerCloses() =
        runTest {
            val model = openedSheet()
            advanceUntilIdle()
            val picked =
                model.state.value.categoryChoices
                    .first()
                    .categoryId
            model.onEvent(EditTransactionEvent.CategoryPicked(picked))
            assertThat(model.state.value.form.categoryId).isEqualTo(picked)
            assertThat(model.state.value.openSheet).isNull()
        }

    /** FR-021: a saved new category is created for the open type, picked and the drawer closes. */
    @Test
    fun givenAddFormWhenSavingThenCategoryIsCreatedPickedAndDrawerCloses() =
        runTest {
            val model = openedSheet()
            advanceUntilIdle()
            model.onEvent(EditTransactionEvent.CategoryEdited(CategoryChange.AddStarted))
            model.onEvent(EditTransactionEvent.CategoryEdited(CategoryChange.NameChanged("Kopi")))
            model.onEvent(EditTransactionEvent.CategoryEdited(CategoryChange.IconChanged("home")))
            model.onEvent(EditTransactionEvent.CategoryEdited(CategoryChange.ColorChanged("blue")))
            model.onEvent(EditTransactionEvent.CategoryEdited(CategoryChange.SaveRequested))
            advanceUntilIdle()
            val created =
                model.state.value.catalog.categories
                    .first { it.profile.name == "Kopi" }
            assertThat(created.profile.kind).isEqualTo(CategoryKind.EXPENSE)
            assertThat(created.profile.appearance).isEqualTo(Appearance("home", "blue"))
            assertThat(created.state.isDefault).isEqualTo(false)
            assertThat(model.state.value.form.categoryId).isEqualTo(created.categoryId)
            assertThat(model.state.value.openSheet).isNull()
        }

    /** FR-021: a blank or duplicate name keeps the form open with the broken rule and stores nothing. */
    @Test
    fun givenBlankOrDuplicateNameWhenSavingThenFormStaysOpenWithTheError() =
        runTest {
            val model = openedSheet()
            advanceUntilIdle()
            model.onEvent(EditTransactionEvent.CategoryEdited(CategoryChange.AddStarted))
            model.onEvent(EditTransactionEvent.CategoryEdited(CategoryChange.SaveRequested))
            advanceUntilIdle()
            assertThat(
                model.state.value.newCategory
                    ?.errors,
            ).isEqualTo(setOf(CategoryError.NAME_REQUIRED))
            model.onEvent(EditTransactionEvent.CategoryEdited(CategoryChange.NameChanged("makan")))
            assertThat(
                model.state.value.newCategory
                    ?.errors,
            ).isEqualTo(emptySet<CategoryError>())
            model.onEvent(EditTransactionEvent.CategoryEdited(CategoryChange.SaveRequested))
            advanceUntilIdle()
            assertThat(
                model.state.value.newCategory
                    ?.errors,
            ).isEqualTo(setOf(CategoryError.NAME_TAKEN))
            assertThat(model.state.value.openSheet).isEqualTo(EditSheet.CATEGORY)
            assertThat(model.state.value.catalog.categories.size).isEqualTo(3)
        }

    /** S-04: closing the drawer drops a half-filled form. */
    @Test
    fun givenHalfFilledFormWhenDrawerIsDismissedThenFormIsDropped() =
        runTest {
            val model = openedSheet()
            advanceUntilIdle()
            model.onEvent(EditTransactionEvent.CategoryEdited(CategoryChange.AddStarted))
            assertThat(model.state.value.newCategory).isNotNull()
            model.onEvent(EditTransactionEvent.SheetDismissed)
            assertThat(model.state.value.newCategory).isNull()
        }
}
