package com.meqlause.fintrack.feature.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meqlause.fintrack.core.domain.repository.CategoryRepository
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.Category
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.CategoryKind
import com.meqlause.fintrack.core.model.CategoryProfile
import com.meqlause.fintrack.core.model.CategoryState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

// FR-020..023: drives S-10; the repository is the only source of categories
@HiltViewModel
internal class CategoryViewModel
    @Inject
    constructor(
        private val categories: CategoryRepository,
    ) : ViewModel() {
        private val tab = MutableStateFlow(CategoryKind.EXPENSE)
        private val editor = MutableStateFlow<CategoryEditor?>(null)
        private val archiveOffer = MutableStateFlow<Category?>(null)

        // S-10: Loading until the first list arrives, then Empty or Success for the selected tab
        val state: StateFlow<CategoryUiState> =
            combine(categories.observeAll(), tab, editor, archiveOffer) { all, kind, draft, offer ->
                val ofKind = all.filter { it.profile.kind == kind }
                if (ofKind.isEmpty()) {
                    CategoryUiState.Empty(kind, draft)
                } else {
                    CategoryUiState.Success(kind, ofKind, draft, offer)
                }
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), CategoryUiState.Loading)

        // S-10: switching the tab closes any open editor
        fun selectKind(kind: CategoryKind) {
            tab.value = kind
            editor.value = null
        }

        // FR-021: opens the editor for a new category of the selected kind
        fun startCreate() {
            editor.value = CategoryEditor.forNew(tab.value)
        }

        // FR-021: opens the editor for an existing category
        fun startEdit(category: Category) {
            editor.value = CategoryEditor.of(category)
        }

        fun changeName(name: String) = editor.update { it?.copy(name = name) }

        fun changeIcon(iconKey: String) = editor.update { it?.copy(iconKey = iconKey) }

        fun changeColor(colorKey: String) = editor.update { it?.copy(colorKey = colorKey) }

        fun dismissEditor() {
            editor.value = null
        }

        // FR-021: saves a new or edited category; a blank name keeps the editor open
        fun saveEditor() {
            val draft = editor.value ?: return
            if (draft.name.isBlank()) return
            viewModelScope.launch {
                val all = categories.observeAll().first()
                val now = Instant.now()
                val existing = all.firstOrNull { it.categoryId == draft.categoryId }
                if (existing == null) {
                    categories.insert(newCategory(draft, nextSortOrder(all), now))
                } else {
                    categories.update(existing.withDraft(draft, now))
                }
                editor.value = null
            }
        }

        // FR-021: archives or restores a category; archived ones stay valid for history (BR-007)
        fun setArchived(
            category: Category,
            archived: Boolean,
        ) {
            viewModelScope.launch {
                categories.update(category.copy(state = category.state.copy(isArchived = archived)))
            }
        }

        // FR-022, BR-008: deletes an unused category; a used one is offered for archive instead
        fun requestDelete(category: Category) {
            viewModelScope.launch {
                if (categories.isInUse(category.categoryId)) {
                    archiveOffer.value = category
                } else {
                    categories.delete(category)
                }
            }
        }

        fun confirmArchiveOffer() {
            val offer = archiveOffer.value ?: return
            archiveOffer.value = null
            setArchived(offer, archived = true)
        }

        fun dismissArchiveOffer() {
            archiveOffer.value = null
        }

        // FR-023: moves a category one place up or down within its own kind by swapping sort orders
        fun move(
            category: Category,
            up: Boolean,
        ) {
            viewModelScope.launch {
                val ofKind = categories.observeAll().first().filter { it.profile.kind == category.profile.kind }
                val index = ofKind.indexOfFirst { it.categoryId == category.categoryId }
                val target = ofKind.getOrNull(if (up) index - 1 else index + 1) ?: return@launch
                categories.update(category.withOrder(target.profile.sortOrder))
                categories.update(target.withOrder(category.profile.sortOrder))
            }
        }

        private fun nextSortOrder(all: List<Category>): Int = (all.maxOfOrNull { it.profile.sortOrder } ?: -1) + 1

        private fun newCategory(
            draft: CategoryEditor,
            sortOrder: Int,
            now: Instant,
        ): Category =
            Category(
                categoryId = CategoryId(0L),
                profile = draft.toProfile(sortOrder),
                state = CategoryState(isDefault = false, isArchived = false),
                audit = Audit(createdAt = now, updatedAt = now),
            )

        private fun Category.withDraft(
            draft: CategoryEditor,
            now: Instant,
        ): Category =
            copy(
                profile = draft.toProfile(profile.sortOrder),
                audit = audit.copy(updatedAt = now),
            )

        private fun Category.withOrder(sortOrder: Int): Category = copy(profile = profile.copy(sortOrder = sortOrder))

        private fun CategoryEditor.toProfile(sortOrder: Int): CategoryProfile =
            CategoryProfile(
                name = name.trim(),
                kind = kind,
                appearance = Appearance(iconKey = iconKey, colorKey = colorKey),
                sortOrder = sortOrder,
            )

        private companion object {
            const val STOP_TIMEOUT_MS = 5_000L
        }
    }
