package com.meqlause.fintrack.feature.category

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNull
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.Category
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.CategoryKind
import com.meqlause.fintrack.core.model.CategoryProfile
import com.meqlause.fintrack.core.model.CategoryState
import com.meqlause.fintrack.core.testing.repository.FakeCategoryRepository
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
import java.time.Instant

// FR-020..023, BR-008: CategoryViewModel against the fake repository
@OptIn(ExperimentalCoroutinesApi::class)
class CategoryViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setMainDispatcher() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun resetMainDispatcher() {
        Dispatchers.resetMain()
    }

    // FR-020: with no category of the tab, the state is Empty for that kind
    @Test
    fun givenNoCategoriesWhenScreenOpensThenStateIsEmpty() =
        runTest {
            val repository = FakeCategoryRepository()
            val viewModel = CategoryViewModel(repository)
            val collector = backgroundScope.launch { viewModel.state.collect {} }
            advanceUntilIdle()

            assertThat(viewModel.state.value).isInstanceOf(CategoryUiState.Empty::class)
            collector.cancel()
        }

    // FR-021: saving a new category with a name stores it under the selected kind
    @Test
    fun givenNewDraftWhenSavedThenCategoryIsStored() =
        runTest {
            val repository = FakeCategoryRepository()
            val viewModel = CategoryViewModel(repository)
            viewModel.startCreate()
            viewModel.changeName("  Kopi ")

            viewModel.saveEditor()
            advanceUntilIdle()

            val stored = repository.observeAll().first()
            assertThat(stored.map { it.profile.name }).isEqualTo(listOf("Kopi"))
            assertThat(stored.first().profile.kind).isEqualTo(CategoryKind.EXPENSE)
        }

    // FR-021: a blank name keeps the editor open and stores nothing
    @Test
    fun givenBlankNameWhenSavedThenNothingIsStored() =
        runTest {
            val repository = FakeCategoryRepository()
            val viewModel = CategoryViewModel(repository)
            viewModel.startCreate()
            viewModel.changeName("   ")

            viewModel.saveEditor()
            advanceUntilIdle()

            assertThat(repository.observeAll().first()).isEqualTo(emptyList())
        }

    // BR-008: a category no row refers to is deleted outright
    @Test
    fun givenUnusedCategoryWhenDeleteRequestedThenItIsRemoved() =
        runTest {
            val repository = FakeCategoryRepository()
            val id = repository.insert(sample("Hiburan"))
            val viewModel = CategoryViewModel(repository)

            viewModel.requestDelete(sample("Hiburan").copy(categoryId = id))
            advanceUntilIdle()

            assertThat(repository.observeAll().first()).isEqualTo(emptyList())
        }

    // BR-008, FR-022: a referenced category is kept and the archive offer is raised instead
    @Test
    fun givenUsedCategoryWhenDeleteRequestedThenArchiveOfferIsRaised() =
        runTest {
            val repository = FakeCategoryRepository()
            val id = repository.insert(sample("Makan"))
            repository.markInUse(id)
            val viewModel = CategoryViewModel(repository)
            val collector = backgroundScope.launch { viewModel.state.collect {} }
            val used = sample("Makan").copy(categoryId = id)

            viewModel.requestDelete(used)
            advanceUntilIdle()

            assertThat(repository.observeAll().first().size).isEqualTo(1)
            assertThat((viewModel.state.value as CategoryUiState.Success).archiveOffer).isEqualTo(used)
            viewModel.confirmArchiveOffer()
            advanceUntilIdle()
            assertThat(
                repository
                    .observeAll()
                    .first()
                    .first()
                    .state.isArchived,
            ).isEqualTo(true)
            assertThat((viewModel.state.value as? CategoryUiState.Success)?.archiveOffer).isNull()
            collector.cancel()
        }

    // FR-023: moving a category up swaps its sort order with the one above it
    @Test
    fun givenTwoCategoriesWhenMovedUpThenTheyAreSwapped() =
        runTest {
            val repository = FakeCategoryRepository()
            repository.insert(sample("Satu", order = 0))
            val second = repository.insert(sample("Dua", order = 1))
            val viewModel = CategoryViewModel(repository)

            viewModel.move(sample("Dua", order = 1).copy(categoryId = second), upward = true)
            advanceUntilIdle()

            assertThat(repository.observeAll().first().map { it.profile.name }).isEqualTo(listOf("Dua", "Satu"))
        }

    private fun sample(
        name: String,
        order: Int = 0,
    ): Category =
        Category(
            categoryId = CategoryId(0L),
            profile =
                CategoryProfile(
                    name = name,
                    kind = CategoryKind.EXPENSE,
                    appearance = Appearance(iconKey = "food", colorKey = "orange"),
                    sortOrder = order,
                ),
            state = CategoryState(isDefault = false, isArchived = false),
            audit = Audit(createdAt = Instant.EPOCH, updatedAt = Instant.EPOCH),
        )
}
