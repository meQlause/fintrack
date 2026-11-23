package com.meqlause.fintrack.feature.settings

import com.meqlause.fintrack.core.domain.backup.BackupFailure
import com.meqlause.fintrack.core.model.BackupSnapshot

// FR-110, FR-111: the dialog the backup flow is showing; null when no backup dialog is open
internal sealed interface BackupStep {
    data object ExportPassword : BackupStep

    data class ImportPassword(
        val uri: String,
    ) : BackupStep

    data class ConfirmRestore(
        val snapshot: BackupSnapshot,
    ) : BackupStep
}

// FR-110, FR-111: how the last backup operation ended, shown once in a result dialog
internal sealed interface BackupOutcome {
    data object Saved : BackupOutcome

    data object Restored : BackupOutcome

    data class Failed(
        val failure: BackupFailure,
    ) : BackupOutcome
}

// FR-110, FR-111: what the backup dialogs render; busy is true while a file is being written or read
internal data class BackupUiState(
    val step: BackupStep? = null,
    val outcome: BackupOutcome? = null,
    val busy: Boolean = false,
)
