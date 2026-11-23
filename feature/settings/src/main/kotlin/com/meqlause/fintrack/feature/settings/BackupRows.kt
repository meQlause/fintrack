package com.meqlause.fintrack.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.ScreenRow
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import com.meqlause.fintrack.core.designsystem.R as DesignR

// FR-110..FR-113: "Terakhir" with its round backup button, then restore, the reminder and the CSV export
@Composable
internal fun BackupRows(
    backup: BackupUiState,
    callbacks: SettingsCallbacks,
) {
    LastBackupRow(lastBackupAt = backup.lastBackupAt, onCreate = callbacks.backup.onBackupCreate)
    SettingsRow(
        label = stringResource(R.string.settings_backup_restore_label),
        value = "",
        onClick = callbacks.backup.onBackupRestore,
        showDivider = true,
    )
    SettingsRow(
        label = stringResource(R.string.settings_backup_reminder_label),
        value = reminderValue(backup.reminderDays),
        onClick = callbacks.backup.onBackupReminder,
        showDivider = true,
    )
    SettingsRow(
        label = stringResource(R.string.settings_backup_csv_label),
        value = "",
        onClick = callbacks.backup.onBackupCsv,
        showDivider = true,
    )
}

// FR-110, FR-112: the time of the last backup at the right, and the round button that starts a new one
@Composable
private fun LastBackupRow(
    lastBackupAt: Instant?,
    onCreate: () -> Unit,
) {
    ScreenRow(
        leading = {
            Icon(
                painter = painterResource(DesignR.drawable.ic_download),
                contentDescription = null,
                tint = FintrackTheme.colors.texts.onSurfaceVariant,
                modifier = Modifier.size(FintrackTheme.sizes.categoryGlyph),
            )
        },
        trailing = {
            Text(
                text = lastBackupLabel(lastBackupAt),
                style = FintrackScreenText.style(size = 12f, weight = 500),
                color = FintrackTheme.colors.texts.onSurfaceVariant,
            )
            BackupButton(onClick = onCreate)
        },
    ) {
        Text(
            text = stringResource(R.string.settings_backup_last_label),
            style = FintrackScreenText.style(size = 14f, weight = 500),
            color = FintrackTheme.colors.texts.onSurface,
        )
    }
}

// FR-110: the 34 px round button that starts a backup now
@Composable
private fun BackupButton(onClick: () -> Unit) {
    Box(
        modifier =
            Modifier
                .size(FintrackScreenSizes.roundAction)
                .clip(CircleShape)
                .background(FintrackTheme.colors.brands.primary)
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(DesignR.drawable.ic_download),
            contentDescription = stringResource(R.string.settings_backup_create_label),
            tint = FintrackTheme.colors.brands.onPrimary,
            modifier = Modifier.size(FintrackScreenSizes.roundActionGlyph),
        )
    }
}

// FR-112: "7 hari" or "Nonaktif" for the stored reminder length
@Composable
private fun reminderValue(days: Int): String =
    if (days == 0) {
        stringResource(R.string.settings_backup_reminder_off)
    } else {
        stringResource(R.string.settings_backup_reminder_value, days)
    }

// FR-112: "3 hari", "Hari ini" or "Belum" for the age of the last export
@Composable
private fun lastBackupLabel(lastBackupAt: Instant?): String {
    if (lastBackupAt == null) return stringResource(R.string.settings_backup_last_never)
    val zone = ZoneId.systemDefault()
    val days = ChronoUnit.DAYS.between(lastBackupAt.atZone(zone).toLocalDate(), LocalDate.now(zone))
    return if (days <= 0) {
        stringResource(R.string.settings_backup_last_today)
    } else {
        stringResource(R.string.settings_backup_reminder_value, days.toInt())
    }
}
