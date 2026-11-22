package com.meqlause.fintrack.core.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

/** DOC-02: the DataStore key of every setting; key names are the storage contract and must not change. */
internal object SettingsKeys {
    val onboardingCompleted = booleanPreferencesKey("onboardingCompleted")
    val themeMode = stringPreferencesKey("themeMode")
    val periodStartDay = intPreferencesKey("periodStartDay")
    val hideAmountsByDefault = booleanPreferencesKey("hideAmountsByDefault")
    val headsUpForInbox = booleanPreferencesKey("headsUpForInbox")
    val investReminder = booleanPreferencesKey("investReminder")
    val histView = stringPreferencesKey("histView")
    val lastUsedWalletId = longPreferencesKey("lastUsedWalletId")
    val lastUsedTransactionType = stringPreferencesKey("lastUsedTransactionType")
    val pinHash = stringPreferencesKey("pinHash")
    val pinSalt = stringPreferencesKey("pinSalt")
    val failedPinAttempts = intPreferencesKey("failedPinAttempts")
    val lockedUntil = longPreferencesKey("lockedUntil")
    val biometricEnabled = booleanPreferencesKey("biometricEnabled")
    val lockTimeoutSeconds = intPreferencesKey("lockTimeoutSeconds")
    val hideFromRecents = booleanPreferencesKey("hideFromRecents")
    val lastBackupAt = longPreferencesKey("lastBackupAt")
    val backupReminderDays = intPreferencesKey("backupReminderDays")
}
