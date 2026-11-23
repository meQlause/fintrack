package com.meqlause.fintrack.feature.settings

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.time.LocalDate
import java.time.YearMonth

// DOC-03, FR-100, FR-110, FR-111, FR-120, FR-121: stateful entry of S-12; the only composable touching its view models
@Composable
internal fun SettingsRoute(
    links: SettingsLinks,
    pins: SettingsSecurityCallbacks,
) {
    val viewModel: SettingsViewModel = hiltViewModel()
    val backup: BackupViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val backupState by backup.state.collectAsStateWithLifecycle()
    val launchers = rememberBackupLaunchers(backup)
    val context = LocalContext.current
    val callbacks =
        settingsCallbacks(viewModel, backup, launchers).copy(
            links = links,
            security = pins.copy(onToggleBiometric = viewModel::toggleBiometric),
        )
    SettingsScreen(
        state = state,
        callbacks = callbacks,
        backup = backupState,
        version = remember { readVersionName(context) },
    )
    BackupFlow(
        state = backupState,
        callbacks = backupFlowCallbacks(backup, launchers),
        onDismissStep = backup::dismissStep,
        onDismissOutcome = backup::dismissOutcome,
    )
}

// FR-110, FR-111, FR-113: the system file pickers behind the backup and CSV rows
private class BackupLaunchers(
    val create: ManagedActivityResultLauncher<String, Uri?>,
    val open: ManagedActivityResultLauncher<Array<String>, Uri?>,
    val csvPicker: ManagedActivityResultLauncher<String, Uri?>,
)

@Composable
private fun rememberBackupLaunchers(backup: BackupViewModel): BackupLaunchers {
    val create =
        rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument(BACKUP_MIME_TYPE)) { uri ->
            backup.onExportTarget(uri?.toString())
        }
    val open =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            backup.onImportPicked(uri?.toString())
        }
    val csvPicker =
        rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument(CSV_MIME_TYPE)) { uri ->
            backup.onCsvTarget(uri?.toString())
        }
    return BackupLaunchers(create = create, open = open, csvPicker = csvPicker)
}

// FR-120, FR-121: the callbacks of the settings screen, each one a link, a ViewModel call or a picker
private fun settingsCallbacks(
    viewModel: SettingsViewModel,
    backup: BackupViewModel,
    launchers: BackupLaunchers,
): SettingsCallbacks =
    SettingsCallbacks(
        preferences =
            SettingsPreferenceCallbacks(
                onOpenSheet = viewModel::openSheet,
                onPickThemeMode = viewModel::pickThemeMode,
                onPickPeriodStartDay = viewModel::pickPeriodStartDay,
                onDismissSheet = viewModel::dismissSheet,
                onToggleHideFromRecents = viewModel::toggleHideFromRecents,
            ),
        backup =
            SettingsBackupCallbacks(
                onBackupCreate = backup::startExport,
                onBackupRestore = { launchers.open.launch(arrayOf(BACKUP_OPEN_MIME_TYPE)) },
                onBackupReminder = backup::startReminderPick,
                onBackupCsv = backup::startCsvExport,
            ),
    )

// FR-110, FR-113: the password and file steps of the backup and CSV export, each one ends in the system picker
private fun backupFlowCallbacks(
    backup: BackupViewModel,
    launchers: BackupLaunchers,
): BackupFlowCallbacks =
    BackupFlowCallbacks(
        onPasswordForExport = { password ->
            backup.onExportPassword(password)
            launchers.create.launch(BACKUP_FILE_NAME)
        },
        onPasswordForImport = backup::onImportPassword,
        onConfirmRestore = backup::confirmRestore,
        onCsvRangePicked = { range ->
            backup.onCsvRangePicked(range)
            launchers.csvPicker.launch(csvFileName(range.start))
        },
        onReminderDaysPicked = backup::onReminderDaysPicked,
    )

// FR-113: the file name offered by the CSV save picker, for example fintrack-2026-11.csv
private fun csvFileName(start: LocalDate): String = "fintrack-${YearMonth.from(start)}.csv"

// FR-113: the CSV is plain text, so the picker offers CSV files
private const val CSV_MIME_TYPE = "text/csv"

// FR-110: the file name offered by the save picker; the user may rename it
private const val BACKUP_FILE_NAME = "fintrack-backup.fintrack"

// FR-110: backups are opaque binary files, so the picker offers every type and the app checks the header
private const val BACKUP_MIME_TYPE = "application/octet-stream"

// FR-111: any file may be picked; a file that is not a backup is rejected by its header
private const val BACKUP_OPEN_MIME_TYPE = "*/*"
