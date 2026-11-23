package com.meqlause.fintrack.feature.transaction.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meqlause.fintrack.core.domain.repository.CategoryRepository
import com.meqlause.fintrack.core.domain.repository.QuickEntryRepository
import com.meqlause.fintrack.core.domain.repository.SettingsRepository
import com.meqlause.fintrack.core.domain.repository.TransactionRepository
import com.meqlause.fintrack.core.domain.repository.WalletRepository
import com.meqlause.fintrack.core.domain.transaction.SaveTransactionResult
import com.meqlause.fintrack.core.domain.usecase.SaveQuickEntryUseCase
import com.meqlause.fintrack.core.model.LastUsed
import com.meqlause.fintrack.core.model.Transaction
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.WalletId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.Instant
import javax.inject.Inject

private const val MAX_DIGITS = 15
private const val MAX_NOTE_LENGTH = 120

// FR-030..FR-034, S-04: drives the add and edit sheet; validation and storage live in SaveTransactionUseCase
@HiltViewModel
internal class EditTransactionViewModel
    @Inject
    constructor(
        private val transactionRepository: TransactionRepository,
        private val walletRepository: WalletRepository,
        private val categoryRepository: CategoryRepository,
        private val settingsRepository: SettingsRepository,
        private val instalments: EditInstalmentController,
        private val loans: EditLoanController,
        quickEntries: QuickEntryRepository,
        saveQuickEntry: SaveQuickEntryUseCase,
        private val clock: Clock,
    ) : ViewModel() {
        private val mutableState = MutableStateFlow(EditTransactionUiState())
        private val shortcuts = EditShortcutController(quickEntries, saveQuickEntry, mutableState, viewModelScope)

        // S-04: the form, the catalogs and the open drawer, emitted again after every change
        val state: StateFlow<EditTransactionUiState> = mutableState.asStateFlow()

        init {
            viewModelScope.launch {
                shortcuts.observe().collect { list -> mutableState.update { it.copy(shortcuts = list) } }
            }
            viewModelScope.launch {
                combine(
                    walletRepository.observeAll(),
                    walletRepository.observeBalances(),
                    categoryRepository.observeAll(),
                ) { wallets, balances, categories -> EditCatalog(wallets, balances, categories) }
                    .collect { catalog -> mutableState.update { it.copy(catalog = catalog) } }
            }
        }

        // FR-033, FR-034: a new sheet starts from the last used type and wallet; an existing transaction is loaded
        fun open(
            transactionId: Long?,
            quickEntryId: Long? = null,
        ) {
            viewModelScope.launch {
                val existing = transactionId?.let { findTransaction(it) }
                val shortcut = quickEntryId?.let { shortcuts.find(it) }
                val form = existing?.toForm() ?: shortcut?.toForm() ?: newForm()
                val applied = shortcut != null
                mutableState.update {
                    it.copy(
                        form = form,
                        record = recordFor(existing, clock),
                        appliedShortcut = applied,
                    )
                }
            }
        }

        fun onEvent(event: EditTransactionEvent) {
            when (event) {
                is EditTransactionEvent.DigitTyped -> {
                    editActiveField {
                        (it + event.digit)
                            .trimStart(
                                '0',
                            ).take(MAX_DIGITS)
                    }
                }

                EditTransactionEvent.BackspacePressed -> {
                    editActiveField { it.dropLast(1) }
                }

                is EditTransactionEvent.TypeSelected -> {
                    selectType(event.type)
                }

                is EditTransactionEvent.FieldFocused -> {
                    mutableState.update { it.copy(keypadField = event.field) }
                }

                is EditTransactionEvent.SheetOpened -> {
                    mutableState.update { it.copy(openSheet = event.sheet) }
                }

                EditTransactionEvent.SheetDismissed -> {
                    mutableState.update { it.copy(openSheet = null) }
                }

                is EditTransactionEvent.WalletPicked -> {
                    pickWallet(event.walletId)
                }

                is EditTransactionEvent.CategoryPicked -> {
                    updateForm { it.copy(categoryId = event.categoryId) }
                }

                EditTransactionEvent.WalletsSwapped -> {
                    updateForm {
                        it.copy(walletId = it.targetWalletId, targetWalletId = it.walletId)
                    }
                }

                is EditTransactionEvent.NoteChanged -> {
                    updateForm { it.copy(note = event.note.take(MAX_NOTE_LENGTH)) }
                }

                is EditTransactionEvent.ShortcutPicked -> {
                    shortcuts.pick(event.quickEntryId)
                }

                is EditTransactionEvent.ShortcutDeleted -> {
                    shortcuts.delete(event.quickEntryId)
                }

                EditTransactionEvent.ShortcutRestored -> {
                    shortcuts.restore()
                }

                is EditTransactionEvent.ShortcutToggled -> {
                    mutableState.update { it.copy(keepAsShortcut = event.keep) }
                }

                is EditTransactionEvent.InstalmentToggled -> {
                    mutableState.update { instalments.toggled(it, event.enabled) }
                }

                is EditTransactionEvent.InstalmentTenorPicked -> {
                    mutableState.update { instalments.tenorPicked(it, event.months) }
                }

                is EditTransactionEvent.LoanEdited -> {
                    mutableState.update { loans.reduce(it, event.change) }
                }

                EditTransactionEvent.SaveRequested -> {
                    save()
                }
            }
        }

        private suspend fun findTransaction(transactionId: Long): Transaction? =
            transactionRepository.observeAll().first().firstOrNull { it.transactionId.value == transactionId }

        // FR-033: the last used wallet is preselected when it still exists, otherwise the first active one
        private suspend fun newForm(): TransactionForm {
            val lastUsed: LastUsed = settingsRepository.observe().first().lastUsed
            val wallets = walletRepository.observeAll().first().filterNot { it.status.isArchived }
            val remembered: WalletId? = lastUsed.walletId?.takeIf { id -> wallets.any { it.walletId == id } }
            return TransactionForm(
                type = lastUsed.transactionType,
                walletId = remembered ?: wallets.firstOrNull()?.walletId,
            )
        }

        private fun updateForm(transform: (TransactionForm) -> TransactionForm) {
            mutableState.update { it.copy(form = transform(it.form), errors = emptySet()) }
        }

        private fun editActiveField(transform: (String) -> String) {
            mutableState.update { it.withEditedField(transform) }
        }

        // S-04: a transfer keeps a target wallet; a category is kept only while its kind still fits the type
        private fun selectType(type: TransactionType) {
            mutableState.update { current ->
                val kind = type.categoryKind()
                val form = current.form
                val fitsKind =
                    current.catalog.categories.any {
                        it.categoryId == form.categoryId &&
                            it.profile.kind == kind
                    }
                val edited =
                    form.copy(
                        type = type,
                        targetWalletId = form.targetWalletId.takeIf { type == TransactionType.TRANSFER },
                        categoryId = form.categoryId.takeIf { fitsKind },
                    )
                current.copy(form = edited, errors = emptySet(), loan = null)
            }
        }

        // S-04: the drawer decides which side of the sheet the picked wallet belongs to
        private fun pickWallet(walletId: WalletId) {
            val sheet = mutableState.value.openSheet
            updateForm { form ->
                if (sheet ==
                    EditSheet.TARGET_WALLET
                ) {
                    form.copy(targetWalletId = walletId)
                } else {
                    form.copy(walletId = walletId)
                }
            }
            mutableState.update { it.copy(openSheet = null) }
        }

        private fun save() {
            viewModelScope.launch {
                val current = mutableState.value
                if (!shortcuts.storeIfKept(current, Instant.now(clock))) return@launch
                when (val result = if (current.loan != null) loans.store(current) else instalments.store(current)) {
                    is SaveTransactionResult.Saved -> {
                        rememberLastUsed(current.form)
                        mutableState.update { it.copy(isSaved = true) }
                    }

                    is SaveTransactionResult.Rejected -> {
                        mutableState.update { it.copy(errors = result.errors) }
                    }
                }
            }
        }

        // FR-033: the wallet and type of the saved transaction are preselected next time
        private suspend fun rememberLastUsed(form: TransactionForm) {
            settingsRepository.update { settings ->
                settings.copy(lastUsed = settings.lastUsed.copy(walletId = form.walletId, transactionType = form.type))
            }
        }
    }
