package com.meqlause.fintrack.feature.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meqlause.fintrack.core.domain.repository.CategoryRepository
import com.meqlause.fintrack.core.domain.usecase.ObserveBudgetStatusUseCase
import com.meqlause.fintrack.core.domain.usecase.SetBudgetLimitUseCase
import com.meqlause.fintrack.core.model.CategoryKind
import com.meqlause.fintrack.core.model.Money
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// FR-070..074: drives S-11; spending comes from the domain use case, the picker from the category list
@HiltViewModel
internal class BudgetViewModel
    @Inject
    constructor(
        observeBudgets: ObserveBudgetStatusUseCase,
        categories: CategoryRepository,
        private val setLimit: SetBudgetLimitUseCase,
    ) : ViewModel() {
        private val editor = MutableStateFlow<BudgetEditor?>(null)

        // S-11: Loading until the first budget and category lists arrive; then the overall card and the rows
        val state: StateFlow<BudgetUiState> =
            combine(observeBudgets(), categories.observeAll(), editor) { statuses, all, draft ->
                val names = all.associate { it.categoryId to it.profile.name }
                val rows =
                    statuses.map { status ->
                        val categoryId = status.budget.categoryId
                        BudgetRow(BudgetTarget(categoryId, categoryId?.let(names::get)), status)
                    }
                BudgetUiState(
                    loaded = true,
                    overall = rows.firstOrNull { it.target.categoryId == null },
                    rows = rows.filter { it.target.categoryId != null },
                    categories = all.filter { it.profile.kind == CategoryKind.EXPENSE && !it.state.isArchived },
                    editor = draft,
                )
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), BudgetUiState())

        // FR-070: opens the target picker for a new limit
        fun startAdd() {
            editor.value = BudgetEditor.Pick
        }

        // FR-070: opens the amount keypad for an existing budget, prefilled with its current limit
        fun startEdit(row: BudgetRow) {
            editor.value =
                BudgetEditor.Amount(
                    row.target,
                    row.status.budget.limitAmount.rupiah
                        .toString(),
                )
        }

        // FR-070: a target chosen in the picker moves on to the amount keypad
        fun pick(target: BudgetTarget) {
            editor.value = BudgetEditor.Amount(target, digits = "")
        }

        // FR-070: appends one digit to the amount, up to the 12 digits of BR-001
        fun typeDigit(digit: String) {
            editor.update { current ->
                if (current is BudgetEditor.Amount && current.digits.length < MAX_DIGITS) {
                    current.copy(digits = current.digits + digit)
                } else {
                    current
                }
            }
        }

        // FR-070: removes the last digit of the amount
        fun backspace() {
            editor.update { current ->
                if (current is BudgetEditor.Amount) current.copy(digits = current.digits.dropLast(1)) else current
            }
        }

        // FR-070, FR-074: stores the limit; a blank or zero amount keeps the keypad open
        fun save() {
            val draft = editor.value as? BudgetEditor.Amount ?: return
            val limit = Money(draft.digits.toLongOrNull() ?: 0L)
            if (!limit.isTransactionAmount()) return
            viewModelScope.launch {
                setLimit(draft.target.categoryId, limit)
                editor.value = null
            }
        }

        fun dismissEditor() {
            editor.value = null
        }

        private companion object {
            const val STOP_TIMEOUT_MS = 5_000L
            const val MAX_DIGITS = 12
        }
    }
