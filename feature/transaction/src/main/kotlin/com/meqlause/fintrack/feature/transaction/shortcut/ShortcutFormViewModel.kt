package com.meqlause.fintrack.feature.transaction.shortcut

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meqlause.fintrack.core.domain.quickentry.QuickEntryResult
import com.meqlause.fintrack.core.domain.repository.QuickEntryRepository
import com.meqlause.fintrack.core.domain.usecase.SaveQuickEntryUseCase
import com.meqlause.fintrack.feature.transaction.TransactionSources
import com.meqlause.fintrack.feature.transaction.edit.EditCatalog
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

// FR-039, BR-047: the shortcut name is 1 to 24 characters, the amount at most 15 digits, the note 0 to 120
private const val MAX_NAME = 24
private const val MAX_DIGITS = 15
private const val MAX_NOTE = 120

// FR-039, S-03: drives the "Pintasan baru" form; it only ever saves a shortcut, never a transaction (BR-047)
@HiltViewModel
internal class ShortcutFormViewModel
    @Inject
    constructor(
        private val sources: TransactionSources,
        private val quickEntries: QuickEntryRepository,
        private val saveQuickEntry: SaveQuickEntryUseCase,
        private val clock: Clock,
    ) : ViewModel() {
        private val mutableState = MutableStateFlow(ShortcutFormUiState())

        // FR-039: the draft, the catalogs and the open drawer, emitted again after every change
        val state: StateFlow<ShortcutFormUiState> = mutableState.asStateFlow()

        init {
            viewModelScope.launch {
                combine(
                    sources.wallets.observeAll(),
                    sources.wallets.observeBalances(),
                    sources.categories.observeAll(),
                ) { wallets, balances, categories -> EditCatalog(wallets, balances, categories) }
                    .collect { catalog -> mutableState.update { it.copy(catalog = catalog).withDefaultWallet() } }
            }
        }

        // FR-039: a blank form for a new shortcut, or the stored shortcut to edit
        fun open(quickEntryId: Long?) {
            viewModelScope.launch {
                val stored = quickEntries.observeAll().first().firstOrNull { it.quickEntryId.value == quickEntryId }
                if (stored != null) {
                    mutableState.update {
                        it.withDraft { stored.toDraft() }.withStatus {
                            copy(
                                editedId = stored.quickEntryId,
                            )
                        }
                    }
                }
            }
        }

        fun onEvent(event: ShortcutFormEvent) {
            when (event) {
                is ShortcutFormEvent.TypeSelected -> mutableState.update { it.withType(event.type) }
                is ShortcutFormEvent.CategoryPicked -> pick { copy(categoryId = event.categoryId) }
                is ShortcutFormEvent.WalletPicked -> pick { copy(walletId = event.walletId) }
                is ShortcutFormEvent.SheetOpened -> openSheet { event.sheet }
                ShortcutFormEvent.SheetDismissed -> openSheet { null }
                ShortcutFormEvent.SaveRequested -> save()
                else -> onTextEvent(event)
            }
        }

        // FR-039: the three typed fields, each capped at its limit
        private fun onTextEvent(event: ShortcutFormEvent) {
            when (event) {
                is ShortcutFormEvent.NameChanged -> edit { copy(name = event.name.take(MAX_NAME)) }
                is ShortcutFormEvent.AmountChanged -> edit { copy(rupiahDigits = digitsOf(event.digits)) }
                is ShortcutFormEvent.NoteChanged -> edit { copy(note = event.note.take(MAX_NOTE)) }
                else -> Unit
            }
        }

        private fun edit(change: ShortcutDraft.() -> ShortcutDraft) {
            mutableState.update { it.withDraft(change).withStatus { copy(errors = emptySet()) } }
        }

        // FR-039: a pick closes its drawer
        private fun pick(change: ShortcutDraft.() -> ShortcutDraft) {
            mutableState.update { it.withDraft(change).withStatus { copy(openSheet = null) } }
        }

        private fun openSheet(sheet: () -> ShortcutSheet?) {
            mutableState.update { it.withStatus { copy(openSheet = sheet()) } }
        }

        // FR-039, BR-047: the use case checks every rule; a rejected shortcut keeps the form open
        private fun save() {
            viewModelScope.launch {
                val draft = mutableState.value.toQuickEntry(Instant.now(clock)) ?: return@launch
                when (val result = saveQuickEntry(draft)) {
                    is QuickEntryResult.Saved -> {
                        mutableState.update { it.withStatus { copy(isSaved = true) } }
                    }

                    is QuickEntryResult.Rejected -> {
                        mutableState.update {
                            it.withStatus {
                                copy(
                                    errors = result.errors,
                                )
                            }
                        }
                    }
                }
            }
        }
    }

// FR-039: only digits are kept, without leading zeros
private fun digitsOf(text: String): String = text.filter(Char::isDigit).trimStart('0').take(MAX_DIGITS)
