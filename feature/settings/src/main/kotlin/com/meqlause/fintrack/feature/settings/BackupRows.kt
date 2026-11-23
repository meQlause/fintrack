package com.meqlause.fintrack.feature.settings

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

// FR-110..FR-113: the rows of the "Cadangan & Ekspor" section of S-12
@Composable
internal fun BackupRows(
    backup: BackupUiState,
    callbacks: SettingsCallbacks,
) {
    SettingsRow(
        label = stringResource(R.string.settings_backup_create_label),
        value = "",
        onClick = callbacks.onBackupCreate,
    )
    SettingsRow(
        showDivider = true,
        label = stringResource(R.string.settings_backup_restore_label),
        value = "",
        onClick = callbacks.onBackupRestore,
    )
    SettingsRow(
        showDivider = true,
        label = stringResource(R.string.settings_backup_reminder_label),
        value = reminderValue(backup.reminderDays),
        onClick = callbacks.onBackupReminder,
    )
    SettingsRow(
        showDivider = true,
        label = stringResource(R.string.settings_backup_csv_label),
        value = "",
        onClick = callbacks.onBackupCsv,
    )
    Text(
        text = lastBackupLabel(backup.lastBackupAt),
        style = FintrackTheme.typography.labelLarge,
        color = FintrackTheme.colors.texts.onSurfaceVariant,
    )
}

// FR-112: "7 hari" or "Nonaktif" for the stored reminder length
@Composable
private fun reminderValue(days: Int): String =
    if (days == 0) {
        stringResource(R.string.settings_backup_reminder_off)
    } else {
        stringResource(R.string.settings_backup_reminder_value, days)
    }

// FR-112: "Cadangan terakhir: 3 hari lalu", or "belum pernah" before the first export
@Composable
private fun lastBackupLabel(lastBackupAt: Instant?): String {
    if (lastBackupAt == null) return stringResource(R.string.settings_backup_last_never)
    val zone = ZoneId.systemDefault()
    val days = ChronoUnit.DAYS.between(lastBackupAt.atZone(zone).toLocalDate(), LocalDate.now(zone))
    return if (days <= 0) {
        stringResource(R.string.settings_backup_last_today)
    } else {
        stringResource(R.string.settings_backup_last_days, days)
    }
}
