package com.meqlause.fintrack.core.datastore

import androidx.datastore.preferences.core.MutablePreferences
import com.meqlause.fintrack.core.model.Backup
import com.meqlause.fintrack.core.model.LastUsed
import com.meqlause.fintrack.core.model.PinLock
import com.meqlause.fintrack.core.model.Preferences
import com.meqlause.fintrack.core.model.Security
import com.meqlause.fintrack.core.model.Settings
import androidx.datastore.preferences.core.Preferences as StoredPreferences

// DOC-02: stores every Settings value; enums by name, instants as epoch milliseconds, absent nullables removed
internal fun MutablePreferences.writeSettings(settings: Settings) {
    writePreferences(settings.preferences)
    writeLastUsed(settings.lastUsed)
    writeSecurity(settings.security)
    writeBackup(settings.backup)
}

private fun MutablePreferences.writePreferences(value: Preferences) {
    this[SettingsKeys.onboardingCompleted] = value.onboardingCompleted
    this[SettingsKeys.themeMode] = value.themeMode.name
    this[SettingsKeys.periodStartDay] = value.periodStartDay
    this[SettingsKeys.hideAmountsByDefault] = value.hideAmountsByDefault
    this[SettingsKeys.headsUpForInbox] = value.headsUpForInbox
    this[SettingsKeys.investReminder] = value.investReminder
    this[SettingsKeys.histView] = value.histView.name
}

private fun MutablePreferences.writeLastUsed(value: LastUsed) {
    putOrRemove(SettingsKeys.lastUsedWalletId, value.walletId?.value)
    this[SettingsKeys.lastUsedTransactionType] = value.transactionType.name
}

private fun MutablePreferences.writeSecurity(value: Security) {
    writePinLock(value.pinLock)
    this[SettingsKeys.biometricEnabled] = value.biometricEnabled
    this[SettingsKeys.lockTimeoutSeconds] = value.lockTimeoutSeconds
    this[SettingsKeys.hideFromRecents] = value.hideFromRecents
}

private fun MutablePreferences.writePinLock(value: PinLock) {
    putOrRemove(SettingsKeys.pinHash, value.pinHash)
    putOrRemove(SettingsKeys.pinSalt, value.pinSalt)
    this[SettingsKeys.failedPinAttempts] = value.failedPinAttempts
    this[SettingsKeys.lockedUntil] = value.lockedUntil.toEpochMilli()
}

private fun MutablePreferences.writeBackup(value: Backup) {
    putOrRemove(SettingsKeys.lastBackupAt, value.lastBackupAt?.toEpochMilli())
    this[SettingsKeys.backupReminderDays] = value.backupReminderDays
}

private fun <T : Any> MutablePreferences.putOrRemove(
    storedKey: StoredPreferences.Key<T>,
    value: T?,
) {
    if (value == null) remove(storedKey) else this[storedKey] = value
}
