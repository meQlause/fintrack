package com.meqlause.fintrack.core.model

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test
import java.time.Instant

/** DOC-11, FR-111: a backup snapshot carries the ledger, the adjunct tables and the reminder setting. */
class BackupBundleTest {
    private val stamp = Instant.parse("2026-11-23T02:00:00Z")

    /** FR-111: an empty ledger holds no rows in any of its tables. */
    @Test
    fun `given empty ledger when read then every table is empty (FR-111)`() {
        val ledger = BackupLedger(emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList())
        assertThat(ledger.wallets).isEmpty()
        assertThat(ledger.budgetAlertLogs).isEmpty()
        assertThat(ledger.merchantRules).isEmpty()
    }

    /** FR-111: an empty adjunct holds no rows in any of its tables. */
    @Test
    fun `given empty adjunct when read then every table is empty (FR-111)`() {
        val adjunct = BackupAdjunct(emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList())
        assertThat(adjunct.monitoredApps).isEmpty()
        assertThat(adjunct.quickEntries).isEmpty()
    }

    /** DOC-11: a snapshot keeps its ledger, adjunct, preferences and reminder days exactly as given. */
    @Test
    fun `given snapshot when read then it keeps its parts and reminder days (DOC-11)`() {
        val ledger = BackupLedger(emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList())
        val adjunct = BackupAdjunct(emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList())
        val snapshot = BackupSnapshot(stamp, ledger, adjunct, Preferences(), backupReminderDays = 14)
        assertThat(snapshot.ledger).isEqualTo(ledger)
        assertThat(snapshot.adjunct).isEqualTo(adjunct)
        assertThat(snapshot.backupReminderDays).isEqualTo(14)
        assertThat(snapshot.createdAt).isEqualTo(stamp)
    }
}
