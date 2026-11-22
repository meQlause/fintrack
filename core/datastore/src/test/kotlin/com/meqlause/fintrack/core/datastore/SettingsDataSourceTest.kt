package com.meqlause.fintrack.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.meqlause.fintrack.core.model.Backup
import com.meqlause.fintrack.core.model.HistoryView
import com.meqlause.fintrack.core.model.LastUsed
import com.meqlause.fintrack.core.model.PinLock
import com.meqlause.fintrack.core.model.Security
import com.meqlause.fintrack.core.model.Settings
import com.meqlause.fintrack.core.model.ThemeMode
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.WalletId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.time.Instant
import com.meqlause.fintrack.core.model.Preferences as SettingsPreferences

/** DOC-02: the settings data source reads and writes every key of the Settings table. */
class SettingsDataSourceTest {
    @get:Rule
    val folder = TemporaryFolder()

    /** DOC-02: a fresh store reads every key as its literal table default. */
    @Test
    fun `given fresh store when settings read then every key has its DOC-02 default (DOC-02)`() =
        runTest {
            val settings = SettingsDataSource(fileStore(backgroundScope)).settings.first()

            assertThat(settings.preferences.onboardingCompleted).isEqualTo(false)
            assertThat(settings.preferences.themeMode).isEqualTo(ThemeMode.SYSTEM)
            assertThat(settings.preferences.periodStartDay).isEqualTo(1)
            assertThat(settings.preferences.hideAmountsByDefault).isEqualTo(false)
            assertThat(settings.preferences.headsUpForInbox).isEqualTo(true)
            assertThat(settings.preferences.investReminder).isEqualTo(false)
            assertThat(settings.lastUsed.histView).isEqualTo(HistoryView.LIST)
            assertThat(settings.lastUsed.walletId).isNull()
            assertThat(settings.lastUsed.transactionType).isEqualTo(TransactionType.EXPENSE)
            assertThat(settings.security.pinLock.pinHash).isNull()
            assertThat(settings.security.pinLock.pinSalt).isNull()
            assertThat(settings.security.pinLock.failedPinAttempts).isEqualTo(0)
            assertThat(settings.security.pinLock.lockedUntil).isEqualTo(Instant.EPOCH)
            assertThat(settings.security.biometricEnabled).isEqualTo(false)
            assertThat(settings.security.lockTimeoutSeconds).isEqualTo(30)
            assertThat(settings.security.hideFromRecents).isEqualTo(false)
            assertThat(settings.backup.lastBackupAt).isNull()
            assertThat(settings.backup.backupReminderDays).isEqualTo(7)
        }

    /** DOC-02: every written value reads back unchanged, including nullable values. */
    @Test
    fun `given settings written when read back then the values round trip (DOC-02)`() =
        runTest {
            val source = SettingsDataSource(fileStore(backgroundScope))
            val written = populatedSettings()

            source.update { written }

            assertThat(source.settings.first()).isEqualTo(written)
        }

    /** DOC-02: clearing nullable values and returning to defaults restores the table defaults. */
    @Test
    fun `given populated settings when reset to defaults then read returns defaults (DOC-02)`() =
        runTest {
            val source = SettingsDataSource(InMemoryPreferencesStore())
            source.update { populatedSettings() }

            source.update { Settings() }

            assertThat(source.settings.first()).isEqualTo(Settings())
        }

    /** DOC-02: an unknown stored enum name falls back to the default, never crashes. */
    @Test
    fun `given unknown enum names stored when read then defaults are used (DOC-02)`() =
        runTest {
            val store = fileStore(backgroundScope)
            store.edit {
                it[stringPreferencesKey("themeMode")] = "NEON"
                it[stringPreferencesKey("lastUsedTransactionType")] = "GIFT"
                it[stringPreferencesKey("histView")] = "PIE"
            }

            val settings = SettingsDataSource(store).settings.first()

            assertThat(settings.preferences.themeMode).isEqualTo(ThemeMode.SYSTEM)
            assertThat(settings.lastUsed.transactionType).isEqualTo(TransactionType.EXPENSE)
            assertThat(settings.lastUsed.histView).isEqualTo(HistoryView.LIST)
        }

    /** DOC-02: an out-of-range period start day falls back to the default day 1. */
    @Test
    fun `given out of range period start day stored when read then default is used (DOC-02)`() =
        runTest {
            val store = fileStore(backgroundScope)
            store.edit { it[intPreferencesKey("periodStartDay")] = 31 }

            val settings = SettingsDataSource(store).settings.first()

            assertThat(settings.preferences.periodStartDay).isEqualTo(1)
        }

    private fun populatedSettings(): Settings =
        Settings(
            preferences =
                SettingsPreferences(
                    onboardingCompleted = true,
                    themeMode = ThemeMode.DARK,
                    periodStartDay = 15,
                    hideAmountsByDefault = true,
                    headsUpForInbox = false,
                    investReminder = true,
                ),
            lastUsed = populatedLastUsed(),
            security =
                Security(
                    pinLock =
                        PinLock(
                            pinHash = "hash",
                            pinSalt = "salt",
                            failedPinAttempts = 2,
                            lockedUntil = Instant.ofEpochMilli(1_700_000_000_000L),
                        ),
                    biometricEnabled = true,
                    lockTimeoutSeconds = 120,
                    hideFromRecents = true,
                ),
            backup = Backup(lastBackupAt = Instant.ofEpochMilli(1_690_000_000_000L), backupReminderDays = 0),
        )

    private fun fileStore(scope: CoroutineScope): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            scope = scope,
            produceFile = { File(folder.root, "settings.preferences_pb") },
        )

    private fun populatedLastUsed(): LastUsed =
        LastUsed(
            walletId = WalletId(42L),
            transactionType = TransactionType.TRANSFER,
            histView = HistoryView.STATISTICS,
        )
}
