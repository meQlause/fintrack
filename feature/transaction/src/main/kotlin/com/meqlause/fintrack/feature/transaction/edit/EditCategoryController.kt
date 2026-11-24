package com.meqlause.fintrack.feature.transaction.edit

import com.meqlause.fintrack.core.domain.category.CategoryResult
import com.meqlause.fintrack.core.domain.usecase.CreateCategoryUseCase
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.CategoryProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// FR-021, FR-030, FR-031, S-04: the "Tambah kategori" form of the category drawer; a saved category is picked at once
internal class EditCategoryController(
    private val createCategory: CreateCategoryUseCase,
    private val state: MutableStateFlow<EditTransactionUiState>,
    private val scope: CoroutineScope,
) {
    fun handle(change: CategoryChange) {
        when (change) {
            CategoryChange.AddStarted -> edit { CategoryForm() }
            is CategoryChange.NameChanged -> edit { copy(name = change.name, errors = emptySet()) }
            is CategoryChange.IconChanged -> edit { copy(iconKey = change.iconKey) }
            is CategoryChange.ColorChanged -> edit { copy(colorKey = change.colorKey) }
            CategoryChange.SaveRequested -> save()
        }
    }

    // FR-021: the rules of the use case decide; a rejected name keeps the form open with its message
    private fun save() {
        val current = state.value
        val form = current.newCategory ?: return
        val kind = current.form.type.categoryKind() ?: return
        scope.launch {
            val profile = CategoryProfile(form.name, kind, Appearance(form.iconKey, form.colorKey), sortOrder = 0)
            when (val result = createCategory(profile)) {
                is CategoryResult.Created -> state.update { it.withCategory(result.categoryId).showing(null) }
                is CategoryResult.Rejected -> edit { copy(errors = result.errors) }
            }
        }
    }

    private fun edit(transform: CategoryForm.() -> CategoryForm) {
        state.update { current ->
            current.withExtras { copy(newCategory = (newCategory ?: CategoryForm()).transform()) }
        }
    }
}
