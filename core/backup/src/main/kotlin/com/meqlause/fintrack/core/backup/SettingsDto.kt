package com.meqlause.fintrack.core.backup

import com.meqlause.fintrack.core.model.Preferences
import com.meqlause.fintrack.core.model.ThemeMode
import kotlinx.serialization.Serializable

// DOC-11: non-secret settings only; the PIN, lock state and lastBackupAt never leave the device
@Serializable
internal data class SettingsDto(
    val themeMode: String,
    val periodStartDay: Int,
    val backupReminderDays: Int,
    val flags: FlagsDto,
) {
    fun toPreferences(): Preferences =
        Preferences(
            onboardingCompleted = flags.onboardingCompleted,
            themeMode = BackupValues.enumNamed<ThemeMode>(themeMode),
            periodStartDay = periodStartDay,
            hideAmountsByDefault = flags.hideAmountsByDefault,
            headsUpForInbox = flags.headsUpForInbox,
            investReminder = flags.investReminder,
        )

    @Serializable
    internal data class FlagsDto(
        val onboardingCompleted: Boolean,
        val hideAmountsByDefault: Boolean,
        val headsUpForInbox: Boolean,
        val investReminder: Boolean,
    )

    companion object {
        fun from(
            preferences: Preferences,
            backupReminderDays: Int,
        ): SettingsDto =
            SettingsDto(
                themeMode = preferences.themeMode.name,
                periodStartDay = preferences.periodStartDay,
                backupReminderDays = backupReminderDays,
                flags =
                    FlagsDto(
                        preferences.onboardingCompleted,
                        preferences.hideAmountsByDefault,
                        preferences.headsUpForInbox,
                        preferences.investReminder,
                    ),
            )
    }
}
