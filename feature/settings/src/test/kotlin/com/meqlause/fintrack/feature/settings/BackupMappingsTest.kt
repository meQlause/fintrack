package com.meqlause.fintrack.feature.settings

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEqualTo
import assertk.assertions.isNull
import com.meqlause.fintrack.core.domain.backup.BackupFailure
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

// FR-110, FR-111, FR-123: the messages, the password rules and the context readers of the settings screen
@RunWith(RobolectricTestRunner::class)
class BackupMappingsTest {
    /** FR-110, FR-111: every failure reason has its own message and every outcome maps to one. */
    @Test
    fun `given every outcome when mapped then each failure has its own message (FR-111)`() {
        val failures = BackupFailure.entries.map { failureRes(it) }
        assertThat(failures.toSet().size).isEqualTo(BackupFailure.entries.size)
        assertThat(outcomeRes(BackupOutcome.Saved)).isNotEqualTo(outcomeRes(BackupOutcome.Restored))
        assertThat(outcomeRes(BackupOutcome.CsvSaved)).isNotEqualTo(outcomeRes(BackupOutcome.Saved))
        assertThat(outcomeRes(BackupOutcome.Failed(BackupFailure.CORRUPT))).isEqualTo(failureRes(BackupFailure.CORRUPT))
    }

    /** FR-110: a new password needs the minimum length and a matching repeat; a restore accepts anything. */
    @Test
    fun `given passwords when checked then only a creation can be refused (FR-110)`() {
        assertThat(passwordError(creating = false, password = "", repeated = "x")).isNull()
        assertThat(passwordError(true, "pendek", "pendek")).isEqualTo(R.string.backup_password_too_short)
        assertThat(passwordError(true, "cukup-panjang", "lain")).isEqualTo(R.string.backup_password_mismatch)
        assertThat(passwordError(true, "cukup-panjang", "cukup-panjang")).isNull()
        assertThat(titleRes(creating = true)).isNotEqualTo(titleRes(creating = false))
    }

    /** FR-123, DOC-07: the version name and the battery state are read from the system. */
    @Test
    fun `given a context when read then the version and the battery state come back (FR-123)`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        assertThat(readVersionName(context)).isEqualTo("")
        assertThat(isOptimisationIgnored(context)).isEqualTo(false)
    }
}
