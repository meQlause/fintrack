package com.meqlause.fintrack.feature.transaction.edit

import com.meqlause.fintrack.core.designsystem.component.UndoWindow
import com.meqlause.fintrack.core.domain.quickentry.QuickEntryResult
import com.meqlause.fintrack.core.domain.repository.QuickEntryRepository
import com.meqlause.fintrack.core.domain.usecase.SaveQuickEntryUseCase
import com.meqlause.fintrack.core.model.QuickEntry
import com.meqlause.fintrack.core.model.QuickEntryId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant

// FR-039, BR-047, S-04: the shortcut side of the add sheet; it fills the draft and never saves a transaction itself
internal class EditShortcutController(
    private val quickEntries: QuickEntryRepository,
    private val saveQuickEntry: SaveQuickEntryUseCase,
    private val state: MutableStateFlow<EditTransactionUiState>,
    private val scope: CoroutineScope,
) {
    private var undoJob: Job? = null

    fun observe(): Flow<List<QuickEntry>> = quickEntries.observeAll()

    // FR-039: the shortcut list, its undo, the "Simpan sebagai pintasan" checkbox and its name; false for other events
    fun handle(event: EditTransactionEvent): Boolean {
        when (event) {
            is EditTransactionEvent.ShortcutPicked -> {
                pick(event.quickEntryId)
            }

            is EditTransactionEvent.ShortcutDeleted -> {
                delete(event.quickEntryId)
            }

            EditTransactionEvent.ShortcutRestored -> {
                restore()
            }

            is EditTransactionEvent.ShortcutToggled -> {
                state.update { it.withShortcuts { copy(keepAsShortcut = event.keep) } }
            }

            is EditTransactionEvent.ShortcutNameChanged -> {
                state.update { it.withShortcuts { copy(shortcutName = event.name, shortcutErrors = emptySet()) } }
            }

            else -> {
                return false
            }
        }
        return true
    }

    // FR-039: the shortcut with [quickEntryId], or null when it was deleted before the sheet opened
    suspend fun find(quickEntryId: Long): QuickEntry? =
        quickEntries.observeAll().first().firstOrNull { it.quickEntryId.value == quickEntryId }

    // FR-039: one tap fills the draft and closes the drawer; the draft stays fully editable (BR-047)
    fun pick(quickEntryId: QuickEntryId) {
        val entry = state.value.shortcuts.firstOrNull { it.quickEntryId == quickEntryId } ?: return
        state.update {
            it
                .copy(form = entry.toForm())
                .withStatus { copy(openSheet = null, errors = emptySet()) }
                .withShortcuts { copy(appliedShortcut = true, keepAsShortcut = false, shortcutName = "") }
        }
    }

    // FR-039: the manager deletes at once and keeps the shortcut restorable for the undo window
    fun delete(quickEntryId: QuickEntryId) {
        val entry = state.value.shortcuts.firstOrNull { it.quickEntryId == quickEntryId } ?: return
        scope.launch { quickEntries.delete(quickEntryId) }
        state.update { it.withShortcuts { copy(removedShortcut = entry) } }
        undoJob?.cancel()
        undoJob =
            scope.launch {
                delay(UndoWindow.MILLIS)
                state.update { it.withShortcuts { copy(removedShortcut = null) } }
            }
    }

    // FR-039: Urungkan puts the last deleted shortcut back into the list
    fun restore() {
        val entry = state.value.removedShortcut ?: return
        undoJob?.cancel()
        state.update { it.withShortcuts { copy(removedShortcut = null) } }
        scope.launch { quickEntries.insert(entry) }
    }

    // BR-047: a shortcut is stored only when the sheet asks for it; a rejected title keeps the sheet open
    suspend fun storeIfKept(
        current: EditTransactionUiState,
        currentTime: Instant,
    ): Boolean {
        val draft = current.toShortcut(currentTime) ?: return true
        return when (val result = saveQuickEntry(draft)) {
            is QuickEntryResult.Saved -> {
                true
            }

            is QuickEntryResult.Rejected -> {
                state.update { it.withShortcuts { copy(shortcutErrors = result.errors) } }
                false
            }
        }
    }
}
