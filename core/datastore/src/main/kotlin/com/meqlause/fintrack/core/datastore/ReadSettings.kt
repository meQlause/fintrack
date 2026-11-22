package com.meqlause.fintrack.core.datastore

import com.meqlause.fintrack.core.model.Backup
import com.meqlause.fintrack.core.model.LastUsed
import com.meqlause.fintrack.core.model.PinLock
import com.meqlause.fintrack.core.model.Preferences
import com.meqlause.fintrack.core.model.Security
import com.meqlause.fintrack.core.model.Settings
import com.meqlause.fintrack.core.model.WalletId
import java.time.Instant
import androidx.datastore.preferences.core.Preferences as StoredPreferences

// DOC-02: maps stored values to Settings; every absent or unreadable value falls back to the DOC-02 default
internal fun StoredPreferences.toSettings(): Settings =
    Settings(
        preferences = readPreferences(),
        lastUsed = toLastUsed(),
        security = toSecurity(),
        backup = toBackup(),
    )

private fun StoredPreferences.readPreferences(): Preferences {
    val base = Preferences()
    return Preferences(
        onboardingCompleted = this[SettingsKeys.onboardingCompleted] ?: base.onboardingCompleted,
        themeMode = this[SettingsKeys.themeMode].toEnumOr(base.themeMode),
        periodStartDay =
            this[SettingsKeys.periodStartDay]?.takeIf { it in 1..MAX_PERIOD_START_DAY } ?: base.periodStartDay,
        hideAmountsByDefault = this[SettingsKeys.hideAmountsByDefault] ?: base.hideAmountsByDefault,
        headsUpForInbox = this[SettingsKeys.headsUpForInbox] ?: base.headsUpForInbox,
        investReminder = this[SettingsKeys.investReminder] ?: base.investReminder,
    )
}

private fun StoredPreferences.toLastUsed(): LastUsed {
    val base = LastUsed()
    return LastUsed(
        walletId = this[SettingsKeys.lastUsedWalletId]?.let(::WalletId),
        transactionType = this[SettingsKeys.lastUsedTransactionType].toEnumOr(base.transactionType),
        histView = this[SettingsKeys.histView].toEnumOr(base.histView),
    )
}

private fun StoredPreferences.toSecurity(): Security {
    val base = Security()
    return Security(
        pinLock = toPinLock(),
        biometricEnabled = this[SettingsKeys.biometricEnabled] ?: base.biometricEnabled,
        lockTimeoutSeconds = this[SettingsKeys.lockTimeoutSeconds] ?: base.lockTimeoutSeconds,
        hideFromRecents = this[SettingsKeys.hideFromRecents] ?: base.hideFromRecents,
    )
}

private fun StoredPreferences.toPinLock(): PinLock {
    val base = PinLock()
    return PinLock(
        pinHash = this[SettingsKeys.pinHash],
        pinSalt = this[SettingsKeys.pinSalt],
        failedPinAttempts = this[SettingsKeys.failedPinAttempts] ?: base.failedPinAttempts,
        lockedUntil = this[SettingsKeys.lockedUntil]?.let(Instant::ofEpochMilli) ?: base.lockedUntil,
    )
}

private fun StoredPreferences.toBackup(): Backup {
    val base = Backup()
    return Backup(
        lastBackupAt = this[SettingsKeys.lastBackupAt]?.let(Instant::ofEpochMilli),
        backupReminderDays = this[SettingsKeys.backupReminderDays] ?: base.backupReminderDays,
    )
}

private inline fun <reified T : Enum<T>> String?.toEnumOr(default: T): T =
    enumValues<T>().firstOrNull { it.name == this } ?: default

private const val MAX_PERIOD_START_DAY = 28
