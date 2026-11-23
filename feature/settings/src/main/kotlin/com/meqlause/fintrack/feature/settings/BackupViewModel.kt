package com.meqlause.fintrack.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meqlause.fintrack.core.domain.backup.BackupException
import com.meqlause.fintrack.core.domain.usecase.ExportBackupUseCase
import com.meqlause.fintrack.core.domain.usecase.ReadBackupUseCase
import com.meqlause.fintrack.core.domain.usecase.RestoreBackupUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// FR-110, FR-111: drives the backup and restore dialogs of S-12; the file itself is picked by the screen
@HiltViewModel
internal class BackupViewModel
    @Inject
    constructor(
        private val exportBackup: ExportBackupUseCase,
        private val readBackup: ReadBackupUseCase,
        private val restoreBackup: RestoreBackupUseCase,
    ) : ViewModel() {
        private val mutable = MutableStateFlow(BackupUiState())
        private var exportPassword: String? = null

        val state: StateFlow<BackupUiState> = mutable.asStateFlow()

        // FR-110: "Buat cadangan" opens the password dialog first; the file picker comes after it
        fun startExport() {
            mutable.update { it.copy(step = BackupStep.ExportPassword, outcome = null) }
        }

        // FR-110: the password is kept only until the picker returns a file
        fun onExportPassword(password: String) {
            exportPassword = password
            mutable.update { it.copy(step = null) }
        }

        // FR-110: writes the backup to the picked file; a cancelled picker just forgets the password
        fun onExportTarget(uri: String?) {
            val password = exportPassword
            exportPassword = null
            if (uri == null || password == null) return
            runOperation {
                exportBackup(uri, password)
                BackupOutcome.Saved
            }
        }

        // FR-111: "Pulihkan" hands the picked file to the password dialog
        fun onImportPicked(uri: String?) {
            if (uri == null) return
            mutable.update { it.copy(step = BackupStep.ImportPassword(uri), outcome = null) }
        }

        // FR-111: decrypts and validates the file; the summary dialog shows before anything is replaced
        fun onImportPassword(password: String) {
            val step = mutable.value.step as? BackupStep.ImportPassword ?: return
            viewModelScope.launch {
                mutable.update { it.copy(busy = true) }
                try {
                    val snapshot = readBackup(step.uri, password)
                    mutable.update { it.copy(step = BackupStep.ConfirmRestore(snapshot), busy = false) }
                } catch (error: BackupException) {
                    mutable.update { it.copy(step = null, outcome = BackupOutcome.Failed(error.failure), busy = false) }
                }
            }
        }

        // FR-111: the user confirmed the summary; all data is replaced in one transaction
        fun confirmRestore() {
            val step = mutable.value.step as? BackupStep.ConfirmRestore ?: return
            runOperation {
                restoreBackup(step.snapshot)
                BackupOutcome.Restored
            }
        }

        // S-12: closing a backup dialog without going on changes nothing
        fun dismissStep() {
            exportPassword = null
            mutable.update { it.copy(step = null) }
        }

        // S-12: the result dialog is closed
        fun dismissOutcome() {
            mutable.update { it.copy(outcome = null) }
        }

        private fun runOperation(operation: suspend () -> BackupOutcome) {
            viewModelScope.launch {
                mutable.update { it.copy(step = null, busy = true) }
                val outcome =
                    try {
                        operation()
                    } catch (error: BackupException) {
                        BackupOutcome.Failed(error.failure)
                    }
                mutable.update { it.copy(outcome = outcome, busy = false) }
            }
        }
    }
