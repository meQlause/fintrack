package com.meqlause.fintrack.core.testing.contract

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.meqlause.fintrack.core.model.Backup
import com.meqlause.fintrack.core.model.HistoryView
import com.meqlause.fintrack.core.model.LastUsed
import com.meqlause.fintrack.core.model.PinLock
import com.meqlause.fintrack.core.model.Preferences
import com.meqlause.fintrack.core.model.Security
import com.meqlause.fintrack.core.model.Settings
import com.meqlause.fintrack.core.model.ThemeMode
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.testing.data.anInstant
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Test

/** DOC-03, DOC-02: the contract of SettingsRepository, run against the fake and the DataStore implementation. */
abstract class SettingsRepositoryContractTest {
    private val fixture: RepositoryFixture by lazy { createFixture() }

    /** ADR-007: a fresh store with no stored values for one test. */
    protected abstract fun createFixture(): RepositoryFixture

    /** ADR-007: releases the store after each test. */
    @After
    fun closeFixture() {
        fixture.close()
    }

    /** DOC-02: a store with no stored values reads every setting as its DOC-02 default. */
    @Test
    fun givenFreshStoreWhenObservedThenEverySettingHasItsDOC02Default() =
        runTest {
            assertThat(fixture.settings.observe().first()).isEqualTo(Settings())
        }

    /** DOC-02: a fully populated value is stored and read back unchanged. */
    @Test
    fun givenPopulatedSettingsWhenUpdatedThenObservedUnchanged() =
        runTest {
            val populated = populatedSettings()

            fixture.settings.update { populated }

            assertThat(fixture.settings.observe().first()).isEqualTo(populated)
        }

    /** DOC-02: an update changes only what its transform changes. */
    @Test
    fun givenOnePreferenceUpdatedWhenObservedThenOtherSettingsKeepTheirValues() =
        runTest {
            fixture.settings.update { it.copy(preferences = it.preferences.copy(themeMode = ThemeMode.DARK)) }

            val observed = fixture.settings.observe().first()

            assertThat(observed.preferences.themeMode).isEqualTo(ThemeMode.DARK)
            assertThat(observed.preferences.periodStartDay).isEqualTo(Preferences().periodStartDay)
        }

    private fun populatedSettings(): Settings =
        Settings(
            preferences =
                Preferences(
                    onboardingCompleted = true,
                    themeMode = ThemeMode.DARK,
                    periodStartDay = 25,
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
                            lockedUntil = anInstant(offsetMillis = 4_000L),
                        ),
                    biometricEnabled = true,
                    lockTimeoutSeconds = 60,
                    hideFromRecents = true,
                ),
            backup = Backup(lastBackupAt = anInstant(offsetMillis = 6_000L), backupReminderDays = 0),
        )

    private fun populatedLastUsed(): LastUsed =
        LastUsed(
            walletId = WalletId(value = 3L),
            transactionType = TransactionType.INCOME,
            histView = HistoryView.STATISTICS,
        )
}
