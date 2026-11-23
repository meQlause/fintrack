package com.meqlause.fintrack.feature.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.time.LocalDate
import java.time.YearMonth

// DOC-03, FR-100, FR-110, FR-111, FR-120, FR-121: stateful entry of S-12; the only composable touching its view models
@Composable
internal fun SettingsRoute(
    onBack: () -> Unit,
    onOpenCategories: () -> Unit,
    onOpenBudgets: () -> Unit,
    onOpenMonitoredApps: () -> Unit,
    onOpenPinSetup: () -> Unit,
    onOpenPinChange: () -> Unit,
    onOpenPinRemove: () -> Unit,
) {
    val viewModel: SettingsViewModel = hiltViewModel()
    val backup: BackupViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val backupState by backup.state.collectAsStateWithLifecycle()
    val createLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument(BACKUP_MIME_TYPE)) { uri ->
            backup.onExportTarget(uri?.toString())
        }
    val openLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            backup.onImportPicked(uri?.toString())
        }
    val csvLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument(CSV_MIME_TYPE)) { uri ->
            backup.onCsvTarget(uri?.toString())
        }
    SettingsScreen(
        state = state,
        backup = backupState,
        callbacks =
            SettingsCallbacks(
                onBack = onBack,
                onOpenSheet = viewModel::openSheet,
                onOpenCategories = onOpenCategories,
                onOpenBudgets = onOpenBudgets,
                onOpenMonitoredApps = onOpenMonitoredApps,
                onOpenPinSetup = onOpenPinSetup,
                onOpenPinChange = onOpenPinChange,
                onOpenPinRemove = onOpenPinRemove,
                onPickThemeMode = viewModel::pickThemeMode,
                onPickPeriodStartDay = viewModel::pickPeriodStartDay,
                onDismissSheet = viewModel::dismissSheet,
                onBackupCreate = backup::startExport,
                onBackupRestore = { openLauncher.launch(arrayOf(BACKUP_OPEN_MIME_TYPE)) },
                onToggleBiometric = viewModel::toggleBiometric,
                onToggleHideFromRecents = viewModel::toggleHideFromRecents,
                onBackupReminder = backup::startReminderPick,
                onBackupCsv = backup::startCsvExport,
            ),
    )
    BackupFlow(
        state = backupState,
        onPasswordForExport = { password ->
            backup.onExportPassword(password)
            createLauncher.launch(BACKUP_FILE_NAME)
        },
        onPasswordForImport = backup::onImportPassword,
        onConfirmRestore = backup::confirmRestore,
        onCsvRangePicked = { range ->
            backup.onCsvRangePicked(range)
            csvLauncher.launch(csvFileName(range.start))
        },
        onReminderDaysPicked = backup::onReminderDaysPicked,
        onDismissStep = backup::dismissStep,
        onDismissOutcome = backup::dismissOutcome,
    )
}

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
