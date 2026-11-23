package com.meqlause.fintrack.feature.transaction.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.meqlause.fintrack.core.domain.repository.CategoryRepository
import com.meqlause.fintrack.core.domain.repository.TransactionRepository
import com.meqlause.fintrack.core.domain.repository.WalletRepository
import com.meqlause.fintrack.core.model.Transaction
import com.meqlause.fintrack.core.model.TransactionId
import com.meqlause.fintrack.feature.transaction.detail.navigation.TransactionDetailDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.Instant
import javax.inject.Inject

private const val UNDO_WINDOW_MILLIS = 5_000L
private const val STOP_TIMEOUT_MILLIS = 5_000L

// FR-034, FR-035, FR-038, S-06: drives the detail; a delete is stored only after the 5 s undo window passes
@HiltViewModel
internal class TransactionDetailViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        private val transactions: TransactionRepository,
        private val wallets: WalletRepository,
        private val categories: CategoryRepository,
        private val clock: Clock,
    ) : ViewModel() {
        private val transactionId =
            TransactionId(savedStateHandle.toRoute<TransactionDetailDestination>().transactionId)
        private val deletePending = MutableStateFlow(false)
        private val closed = MutableStateFlow(false)
        private var deleteJob: Job? = null

        // S-06: the detail of the route's transaction; detail is null when no stored row carries the id
        val state: StateFlow<TransactionDetailUiState> =
            combine(
                transactions.observeAll(),
                wallets.observeAll(),
                categories.observeAll(),
                deletePending,
                closed,
            ) { rows, walletList, categoryList, pending, isClosed ->
                TransactionDetailUiState(
                    isLoaded = true,
                    detail =
                        rows
                            .firstOrNull { it.transactionId == transactionId }
                            ?.toDetail(
                                walletNames = walletList.associate { it.walletId to it.profile.name },
                                categoryNames = categoryList.associate { it.categoryId to it.profile.name },
                                clock = clock,
                            ),
                    isDeletePending = pending,
                    isClosed = isClosed,
                )
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), TransactionDetailUiState())

        // FR-035: the row stays visible under the Undo bar; it is removed only when the window passes untouched
        fun requestDelete() {
            if (deleteJob != null) return
            deletePending.value = true
            deleteJob =
                viewModelScope.launch {
                    delay(UNDO_WINDOW_MILLIS)
                    transactions.delete(transactionId)
                    closed.value = true
                }
        }

        // FR-035: Undo within the window keeps the transaction exactly as it was
        fun undoDelete() {
            deleteJob?.cancel()
            deleteJob = null
            deletePending.value = false
        }

        // FR-038: stores a copy dated now; loan-linked rows are not copied because a copy would double the loan
        fun duplicate() {
            viewModelScope.launch {
                val row = findRow() ?: return@launch
                if (row.routing.loanId != null) return@launch
                transactions.insert(row.asDuplicateAt(Instant.now(clock)))
                closed.value = true
            }
        }

        private suspend fun findRow(): Transaction? =
            transactions.observeAll().first().firstOrNull { it.transactionId == transactionId }
    }
