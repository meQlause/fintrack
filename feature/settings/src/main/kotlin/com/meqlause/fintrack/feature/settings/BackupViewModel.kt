package com.meqlause.fintrack.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meqlause.fintrack.core.domain.backup.BackupException
import com.meqlause.fintrack.core.domain.period.PeriodCalculator
import com.meqlause.fintrack.core.domain.repository.SettingsRepository
import com.meqlause.fintrack.core.domain.usecase.ExportBackupUseCase
import com.meqlause.fintrack.core.domain.usecase.ExportCsvUseCase
import com.meqlause.fintrack.core.domain.usecase.ReadBackupUseCase
import com.meqlause.fintrack.core.domain.usecase.RestoreBackupUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

private const val STOP_TIMEOUT_MS = 5_000L

// FR-110..FR-113: drives the backup rows and dialogs of S-12; the files themselves are picked by the screen
@HiltViewModel
internal class BackupViewModel
    @Inject
    constructor(
        private val exportBackup: ExportBackupUseCase,
        private val readBackup: ReadBackupUseCase,
        private val restoreBackup: RestoreBackupUseCase,
        private val exportCsv: ExportCsvUseCase,
        private val settings: SettingsRepository,
    ) : ViewModel() {
        private val mutable = MutableStateFlow(BackupUiState())
        private var exportPassword: String? = null
        private var pendingCsv: ClosedRange<LocalDate>? = null

        // S-12: the dialog state, plus the stored backup time and reminder length shown in the rows
        val state: StateFlow<BackupUiState> =
            combine(mutable, settings.observe()) { ui, stored ->
                ui.copy(lastBackupAt = stored.backup.lastBackupAt, reminderDays = stored.backup.backupReminderDays)
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), BackupUiState())

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
        fun onExportTarget(fileUri: String?) {
            val password = exportPassword
            exportPassword = null
            if (fileUri == null || password == null) return
            runOperation {
                exportBackup(fileUri, password)
                BackupOutcome.Saved
            }
        }

        // FR-111: "Pulihkan" hands the picked file to the password dialog
        fun onImportPicked(fileUri: String?) {
            if (fileUri == null) return
            mutable.update { it.copy(step = BackupStep.ImportPassword(fileUri), outcome = null) }
        }

        // FR-111: decrypts and validates the file; the summary dialog shows before anything is replaced
        fun onImportPassword(password: String) {
            val step = mutable.value.step as? BackupStep.ImportPassword ?: return
            viewModelScope.launch {
                mutable.update { it.copy(busy = true) }
                try {
                    val snapshot = readBackup(step.fileUri, password)
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

        // FR-113: "Ekspor CSV" opens the date range dialog, prefilled with the current budget period
        fun startCsvExport() {
            viewModelScope.launch {
                val startDay =
                    settings
                        .observe()
                        .first()
                        .preferences.periodStartDay
                val period = PeriodCalculator.periodContaining(LocalDate.now(), startDay)
                mutable.update { it.copy(step = BackupStep.CsvRange(period.startDate..period.endDate), outcome = null) }
            }
        }

        // FR-113: the range is kept until the save picker returns a file
        fun onCsvRangePicked(range: ClosedRange<LocalDate>) {
            pendingCsv = range
            mutable.update { it.copy(step = null) }
        }

        // FR-113: writes the CSV to the picked file; a cancelled picker just forgets the range
        fun onCsvTarget(fileUri: String?) {
            val range = pendingCsv
            pendingCsv = null
            if (fileUri == null || range == null) return
            runOperation {
                exportCsv(fileUri, range.start, range.endInclusive)
                BackupOutcome.CsvSaved
            }
        }

        // FR-112: "Pengingat cadangan" opens the day choice; the stored length changes only on a pick
        fun startReminderPick() {
            mutable.update { it.copy(step = BackupStep.ReminderDays, outcome = null) }
        }

        // FR-112: the chosen length is stored at once; 0 turns the reminder off
        fun onReminderDaysPicked(days: Int) {
            mutable.update { it.copy(step = null) }
            viewModelScope.launch {
                settings.update { stored -> stored.copy(backup = stored.backup.copy(backupReminderDays = days)) }
            }
        }

        // S-12: closing a backup dialog without going on changes nothing
        fun dismissStep() {
            exportPassword = null
            pendingCsv = null
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
