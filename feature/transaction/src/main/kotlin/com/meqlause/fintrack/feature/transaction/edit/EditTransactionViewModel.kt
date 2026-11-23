package com.meqlause.fintrack.feature.transaction.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meqlause.fintrack.core.domain.transaction.SaveTransactionResult
import com.meqlause.fintrack.core.model.LastUsed
import com.meqlause.fintrack.core.model.Transaction
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.feature.transaction.TransactionSources
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

// FR-030..FR-034, S-04: drives the add and edit sheet; validation and storage live in SaveTransactionUseCase
@HiltViewModel
internal class EditTransactionViewModel
    @Inject
    constructor(
        private val sources: TransactionSources,
        private val instalments: EditInstalmentController,
        private val loans: EditLoanController,
        shortcutDeps: EditShortcutDeps,
        private val clock: Clock,
    ) : ViewModel() {
        private val mutableState = MutableStateFlow(EditTransactionUiState())
        private val shortcuts =
            EditShortcutController(shortcutDeps.quickEntries, shortcutDeps.saveQuickEntry, mutableState, viewModelScope)

        // S-04: the form, the catalogs and the open drawer, emitted again after every change
        val state: StateFlow<EditTransactionUiState> = mutableState.asStateFlow()

        init {
            viewModelScope.launch {
                shortcuts.observe().collect { list ->
                    mutableState.update { it.withShortcuts { copy(shortcuts = list) } }
                }
            }
            viewModelScope.launch {
                combine(
                    sources.wallets.observeAll(),
                    sources.wallets.observeBalances(),
                    sources.categories.observeAll(),
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
                    it
                        .copy(form = form, record = recordFor(existing, clock))
                        .withShortcuts { copy(appliedShortcut = applied) }
                }
            }
        }

        fun onEvent(event: EditTransactionEvent) {
            if (handleKeypad(event) || handleForm(event)) return
            if (handleShortcuts(event)) return
            handleExtras(event)
        }

        // FR-030: the keypad types into, or erases from, the field that has the focus
        private fun handleKeypad(event: EditTransactionEvent): Boolean =
            when (event) {
                is EditTransactionEvent.DigitTyped -> {
                    editActiveField { (it + event.digit).trimStart('0').take(MAX_DIGITS) }
                    true
                }

                EditTransactionEvent.BackspacePressed -> {
                    editActiveField { it.dropLast(1) }
                    true
                }

                is EditTransactionEvent.FieldFocused -> {
                    mutableState.update { it.withStatus { copy(keypadField = event.field) } }
                    true
                }

                else -> {
                    false
                }
            }

        // FR-030..FR-032: the type, the drawers, the wallets, the category and the note of the draft
        private fun handleForm(event: EditTransactionEvent): Boolean {
            when (event) {
                is EditTransactionEvent.TypeSelected -> {
                    mutableState.update { it.withType(event.type) }
                }

                is EditTransactionEvent.SheetOpened -> {
                    mutableState.update { it.showing(event.sheet) }
                }

                EditTransactionEvent.SheetDismissed -> {
                    mutableState.update { it.showing(null) }
                }

                is EditTransactionEvent.WalletPicked -> {
                    mutableState.update { it.withPickedWallet(event.walletId) }
                }

                is EditTransactionEvent.CategoryPicked -> {
                    mutableState.update { it.withCategory(event.categoryId) }
                }

                EditTransactionEvent.WalletsSwapped -> {
                    mutableState.update { it.withSwappedWallets() }
                }

                is EditTransactionEvent.NoteChanged -> {
                    mutableState.update { it.withNote(event.note) }
                }

                else -> {
                    return false
                }
            }
            return true
        }

        // FR-039: the shortcut list, its undo and the "Simpan sebagai pintasan" checkbox
        private fun handleShortcuts(event: EditTransactionEvent): Boolean {
            when (event) {
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
                    mutableState.update { it.withShortcuts { copy(keepAsShortcut = event.keep) } }
                }

                else -> {
                    return false
                }
            }
            return true
        }

        // FR-140, FR-158: the instalment and Pinjaman parts, and the save
        private fun handleExtras(event: EditTransactionEvent) {
            when (event) {
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

                else -> {
                    Unit
                }
            }
        }

        private suspend fun findTransaction(transactionId: Long): Transaction? =
            sources.transactions
                .observeAll()
                .first()
                .firstOrNull { it.transactionId.value == transactionId }

        // FR-033: the last used wallet is preselected when it still exists, otherwise the first active one
        private suspend fun newForm(): TransactionForm {
            val lastUsed: LastUsed =
                sources.settings
                    .observe()
                    .first()
                    .lastUsed
            val wallets =
                sources.wallets
                    .observeAll()
                    .first()
                    .filterNot { it.status.isArchived }
            val remembered: WalletId? = lastUsed.walletId?.takeIf { id -> wallets.any { it.walletId == id } }
            return TransactionForm(
                type = lastUsed.transactionType,
                route = FormRoute(walletId = remembered ?: wallets.firstOrNull()?.walletId),
            )
        }

        private fun editActiveField(transform: (String) -> String) {
            mutableState.update { it.withEditedField(transform) }
        }

        private fun save() {
            viewModelScope.launch {
                val current = mutableState.value
                if (!shortcuts.storeIfKept(current, Instant.now(clock))) return@launch
                when (val result = if (current.loan != null) loans.store(current) else instalments.store(current)) {
                    is SaveTransactionResult.Saved -> {
                        rememberLastUsed(current.form)
                        mutableState.update { it.withStatus { copy(isSaved = true) } }
                    }

                    is SaveTransactionResult.Rejected -> {
                        mutableState.update { it.withStatus { copy(errors = result.errors) } }
                    }
                }
            }
        }

        // FR-033: the wallet and type of the saved transaction are preselected next time
        private suspend fun rememberLastUsed(form: TransactionForm) {
            sources.settings.update { settings ->
                settings.copy(lastUsed = settings.lastUsed.copy(walletId = form.walletId, transactionType = form.type))
            }
        }
    }
