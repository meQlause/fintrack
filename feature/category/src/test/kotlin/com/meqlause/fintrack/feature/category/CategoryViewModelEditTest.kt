package com.meqlause.fintrack.feature.category

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import assertk.assertions.isNull
import assertk.assertions.isTrue
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

// FR-021..023, BR-007, BR-008: the editing, archiving and ordering paths of CategoryViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class CategoryViewModelEditTest {
    private val dispatcher = StandardTestDispatcher()
    private val repository = FakeCategoryRepository()

    @Before
    fun setMainDispatcher() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun resetMainDispatcher() {
        Dispatchers.resetMain()
    }

    private suspend fun stored(
        name: String,
        kind: CategoryKind = CategoryKind.EXPENSE,
        sortOrder: Int = 0,
    ): Category {
        val category =
            Category(
                categoryId = CategoryId(0L),
                profile = CategoryProfile(name, kind, Appearance("food", "orange"), sortOrder),
                state = CategoryState(isDefault = false, isArchived = false),
                audit = Audit(Instant.EPOCH, Instant.EPOCH),
            )
        return category.copy(categoryId = repository.insert(category))
    }

    /** FR-021: editing an existing category changes its name, icon and colour and keeps its id. */
    @Test
    fun `given a stored category when it is edited and saved then it is updated in place (FR-021)`() =
        runTest(dispatcher) {
            val food = stored("Makan")
            val viewModel = CategoryViewModel(repository)
            viewModel.startEdit(food)
            viewModel.changeName("Makan siang")
            viewModel.changeIcon("cart")
            viewModel.changeColor("blue")
            viewModel.saveEditor()
            advanceUntilIdle()
            val updated = repository.observeAll().first().single()
            assertThat(updated.categoryId).isEqualTo(food.categoryId)
            assertThat(updated.profile.name).isEqualTo("Makan siang")
            assertThat(updated.profile.appearance).isEqualTo(Appearance("cart", "blue"))
        }

    /** S-10: switching the tab shows the other kind and closes the editor; dismissing also closes it. */
    @Test
    fun `given an open editor when the tab changes or the editor is dismissed then it closes (S-10)`() =
        runTest(dispatcher) {
            stored("Gaji", CategoryKind.INCOME)
            val viewModel = CategoryViewModel(repository)
            val collector = backgroundScope.launch { viewModel.state.collect {} }
            viewModel.startCreate()
            viewModel.selectKind(CategoryKind.INCOME)
            advanceUntilIdle()
            val income = viewModel.state.value as CategoryUiState.Success
            assertThat(income.kind).isEqualTo(CategoryKind.INCOME)
            assertThat(income.editor).isNull()
            viewModel.startCreate()
            viewModel.dismissEditor()
            advanceUntilIdle()
            assertThat((viewModel.state.value as CategoryUiState.Success).editor).isNull()
            collector.cancel()
        }

    /** BR-007: a used category offered for archive is archived on confirm, and left alone on dismiss. */
    @Test
    fun `given a used category when the offer is confirmed or dismissed then it is archived or kept (BR-007)`() =
        runTest(dispatcher) {
            val used = stored("Makan")
            repository.markInUse(used.categoryId)
            val viewModel = CategoryViewModel(repository)
            backgroundScope.launch { viewModel.state.collect {} }
            viewModel.requestDelete(used)
            advanceUntilIdle()
            viewModel.dismissArchiveOffer()
            advanceUntilIdle()
            assertThat(
                repository
                    .observeAll()
                    .first()
                    .single()
                    .state.isArchived,
            ).isFalse()
            viewModel.requestDelete(used)
            advanceUntilIdle()
            viewModel.confirmArchiveOffer()
            advanceUntilIdle()
            assertThat(
                repository
                    .observeAll()
                    .first()
                    .single()
                    .state.isArchived,
            ).isTrue()
            viewModel.confirmArchiveOffer()
        }

    /** FR-021: an archived category can be restored. */
    @Test
    fun `given an archived category when it is restored then it is active again (FR-021)`() =
        runTest(dispatcher) {
            val food = stored("Makan")
            val viewModel = CategoryViewModel(repository)
            viewModel.setArchived(food, archived = true)
            advanceUntilIdle()
            viewModel.setArchived(food, archived = false)
            advanceUntilIdle()
            assertThat(
                repository
                    .observeAll()
                    .first()
                    .single()
                    .state.isArchived,
            ).isFalse()
        }

    /** FR-023: moving down swaps with the next one; moving past the end changes nothing. */
    @Test
    fun `given two categories when the first moves down and the last moves down then only the first swaps (FR-023)`() =
        runTest(dispatcher) {
            val first = stored("A", sortOrder = 0)
            stored("B", sortOrder = 1)
            val viewModel = CategoryViewModel(repository)
            viewModel.move(first, upward = false)
            advanceUntilIdle()
            val orders = repository.observeAll().first().associate { it.profile.name to it.profile.sortOrder }
            assertThat(orders).isEqualTo(mapOf("A" to 1, "B" to 0))
            viewModel.move(first, upward = false)
            advanceUntilIdle()
            val again = repository.observeAll().first().associate { it.profile.name to it.profile.sortOrder }
            assertThat(again).isEqualTo(orders)
        }

    /** FR-020: the route callbacks reach the ViewModel actions. */
    @Test
    fun `given the route callbacks when they are invoked then the view model reacts (FR-020)`() =
        runTest(dispatcher) {
            val food = stored("Makan")
            val viewModel = CategoryViewModel(repository)
            val collector = backgroundScope.launch { viewModel.state.collect {} }
            var left = false
            val callbacks = categoryCallbacks(viewModel = viewModel, onBack = { left = true })
            callbacks.onBack()
            callbacks.onAdd()
            callbacks.editor.onNameChange("Baru")
            callbacks.editor.onIconChange("cart")
            callbacks.editor.onColorChange("blue")
            advanceUntilIdle()
            assertThat(left).isTrue()
            assertThat((viewModel.state.value as CategoryUiState.Success).editor?.name).isEqualTo("Baru")
            callbacks.editor.onSave()
            callbacks.onSelectKind(CategoryKind.INCOME)
            callbacks.list.onEdit(food)
            callbacks.editor.onDismissEditor()
            callbacks.list.onSetArchived(food, true)
            callbacks.list.onMoveUp(food)
            callbacks.list.onMoveDown(food)
            callbacks.list.onDelete(food)
            callbacks.archiveOffer.onDismiss()
            callbacks.archiveOffer.onConfirm()
            advanceUntilIdle()
            assertThat(viewModel.state.value).isInstanceOf(CategoryUiState.Empty::class)
            collector.cancel()
        }

    /** FR-021: edits and saves do nothing while no editor is open. */
    @Test
    fun `given no editor when fields change and save is pressed then nothing is stored (FR-021)`() =
        runTest(dispatcher) {
            val viewModel = CategoryViewModel(repository)
            viewModel.changeName("Tanpa editor")
            viewModel.changeIcon("cart")
            viewModel.changeColor("blue")
            viewModel.saveEditor()
            advanceUntilIdle()
            assertThat(repository.observeAll().first().size).isEqualTo(0)
        }

    /** FR-023: the sort order of a new category is one past the largest stored one, or zero when none exist. */
    @Test
    fun `given stored categories when a new one is saved then it goes after them and the first one at zero (FR-023)`() =
        runTest(dispatcher) {
            assertThat(CategoryDrafts.nextSortOrder(emptyList())).isEqualTo(0)
            val older = stored("Makan", sortOrder = 4)
            val viewModel = CategoryViewModel(repository)
            viewModel.startCreate()
            viewModel.changeName("Kopi")
            viewModel.saveEditor()
            advanceUntilIdle()
            val kopi = repository.observeAll().first().single { it.profile.name == "Kopi" }
            assertThat(kopi.profile.sortOrder).isEqualTo(CategoryDrafts.nextSortOrder(listOf(older)))
            viewModel.move(kopi, upward = true)
            advanceUntilIdle()
            viewModel.move(older, upward = true)
            advanceUntilIdle()
            assertThat(repository.observeAll().first().size).isEqualTo(2)
        }
}
