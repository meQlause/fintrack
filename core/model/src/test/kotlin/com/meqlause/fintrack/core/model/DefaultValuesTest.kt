package com.meqlause.fintrack.core.model

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import assertk.assertions.isTrue
import org.junit.jupiter.api.Test
import java.time.Instant

/** DOC-02: default values of settings and entity fields match the domain model tables. */
class DefaultValuesTest {
    /** DOC-02: a fresh install reads the Settings defaults from the key table. */
    @Test
    fun `given default settings when read then they match the DOC-02 defaults`() {
        val settings = Settings()

        assertThat(settings.preferences.onboardingCompleted).isEqualTo(false)
        assertThat(settings.preferences.themeMode).isEqualTo(ThemeMode.SYSTEM)
        assertThat(settings.preferences.periodStartDay).isEqualTo(1)
        assertThat(settings.preferences.headsUpForInbox).isEqualTo(true)
        assertThat(settings.lastUsed.transactionType).isEqualTo(TransactionType.EXPENSE)
        assertThat(settings.security.lockTimeoutSeconds).isEqualTo(30)
        assertThat(settings.security.pinLock.lockedUntil).isEqualTo(Instant.EPOCH)
        assertThat(settings.security.pinLock.failedPinAttempts).isEqualTo(0)
        assertThat(settings.backup.backupReminderDays).isEqualTo(7)
        assertThat(settings.backup.lastBackupAt).isNull()
    }

    /** BR-043: a new wallet counts in the total and is not archived. */
    @Test
    fun `given new wallet status when read then it is included in total and not archived (BR-043)`() {
        val status = WalletStatus()

        assertThat(status.isIncludedInTotal).isTrue()
        assertThat(status.isArchived).isEqualTo(false)
    }

    /** BR-004: a transfer without a fee has a zero fee and an empty memo. */
    @Test
    fun `given transfer without fee when built then fee is zero and memo is empty (BR-004)`() {
        val movement = Movement(type = TransactionType.TRANSFER, amount = Money(50_000L))
        val memo = Memo()

        assertThat(movement.feeAmount.isZero).isTrue()
        assertThat(memo.note).isEqualTo("")
        assertThat(memo.merchant).isEqualTo("")
    }

    /** DOC-02: a budget is active until it is changed. */
    @Test
    fun `given new budget when built then it is active by default`() {
        val moment = Instant.parse("2026-11-22T03:00:00Z")
        val budget =
            Budget(
                budgetId = BudgetId(1L),
                categoryId = null,
                limitAmount = Money(1_000_000L),
                createdAt = moment,
                updatedAt = moment,
            )

        assertThat(budget.isActive).isTrue()
    }

    /** DOC-02: a PIN lock starts with no PIN and no lockout. */
    @Test
    fun `given new PIN lock when read then no PIN is set and no lockout applies`() {
        val pinLock = PinLock()

        assertThat(pinLock.pinHash).isNull()
        assertThat(pinLock.pinSalt).isNull()
        assertThat(pinLock.failedPinAttempts).isEqualTo(0)
    }
}
