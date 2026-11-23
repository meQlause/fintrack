package com.meqlause.fintrack.feature.settings

import com.meqlause.fintrack.core.domain.backup.BackupFailure
import com.meqlause.fintrack.core.model.BackupSnapshot
import java.time.Instant
import java.time.LocalDate

private const val DEFAULT_REMINDER_DAYS = 7

// FR-110, FR-111, FR-112, FR-113: the dialog the backup flow is showing; null when no backup dialog is open
internal sealed interface BackupStep {
    data object ExportPassword : BackupStep

    data class ImportPassword(
        val fileUri: String,
    ) : BackupStep

    data class ConfirmRestore(
        val snapshot: BackupSnapshot,
    ) : BackupStep

    data class CsvRange(
        val range: ClosedRange<LocalDate>,
    ) : BackupStep

    data object ReminderDays : BackupStep
}

// FR-110, FR-111, FR-113: how the last backup or export ended, shown once in a result dialog
internal sealed interface BackupOutcome {
    data object Saved : BackupOutcome

    data object Restored : BackupOutcome

    data object CsvSaved : BackupOutcome

    data class Failed(
        val failure: BackupFailure,
    ) : BackupOutcome
}

// FR-110..FR-113: what the backup rows and dialogs render; busy is true while a file is being written or read
internal data class BackupUiState(
    val step: BackupStep? = null,
    val outcome: BackupOutcome? = null,
    val busy: Boolean = false,
    val lastBackupAt: Instant? = null,
    val reminderDays: Int = DEFAULT_REMINDER_DAYS,
)
