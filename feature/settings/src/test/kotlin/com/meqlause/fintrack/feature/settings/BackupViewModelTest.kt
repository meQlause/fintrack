package com.meqlause.fintrack.feature.settings

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNull
import assertk.assertions.isTrue
import com.meqlause.fintrack.core.domain.backup.BackupException
import com.meqlause.fintrack.core.domain.backup.BackupFailure
import com.meqlause.fintrack.core.domain.repository.BackupRepository
import com.meqlause.fintrack.core.domain.repository.TransactionCsvRepository
import com.meqlause.fintrack.core.domain.usecase.ExportBackupUseCase
import com.meqlause.fintrack.core.domain.usecase.ExportCsvUseCase
import com.meqlause.fintrack.core.domain.usecase.ReadBackupUseCase
import com.meqlause.fintrack.core.domain.usecase.RestoreBackupUseCase
import com.meqlause.fintrack.core.model.BackupAdjunct
import com.meqlause.fintrack.core.model.BackupLedger
import com.meqlause.fintrack.core.model.BackupSnapshot
import com.meqlause.fintrack.core.model.Preferences
import com.meqlause.fintrack.core.testing.coroutine.MainDispatcherRule
import com.meqlause.fintrack.core.testing.repository.FakeSettingsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

// FR-110..FR-113, S-12: BackupViewModel against an in-memory backup repository
@OptIn(ExperimentalCoroutinesApi::class)
class BackupViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val settings = FakeSettingsRepository()
    private val snapshot =
        BackupSnapshot(
            createdAt = Instant.parse("2026-11-20T00:00:00Z"),
            ledger = BackupLedger(emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList()),
            adjunct = BackupAdjunct(emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList()),
            preferences = Preferences(),
            backupReminderDays = 7,
        )
    private var failure: BackupFailure? = null
    private val exports = mutableListOf<String>()
    private val restores = mutableListOf<BackupSnapshot>()
    private val csvRanges = mutableListOf<ClosedRange<LocalDate>>()

    private val backups =
        object : BackupRepository {
            override suspend fun export(
                fileUri: String,
                password: String,
            ) {
                failure?.let { throw BackupException(it) }
                exports += "$fileUri:$password"
            }

            override suspend fun readBackup(
                fileUri: String,
                password: String,
            ): BackupSnapshot {
                failure?.let { throw BackupException(it) }
                return snapshot
            }

            override suspend fun restore(snapshot: BackupSnapshot) {
                failure?.let { throw BackupException(it) }
                restores += snapshot
            }
        }

    private val csvFiles =
        object : TransactionCsvRepository {
            override suspend fun export(
                fileUri: String,
                start: LocalDate,
                endDate: LocalDate,
                zone: ZoneId,
            ) {
                failure?.let { throw BackupException(it) }
                csvRanges += start..endDate
            }
        }

    private fun TestScope.watched(): BackupViewModel {
        val model =
            BackupViewModel(
                exportBackup = ExportBackupUseCase(backups),
                readBackup = ReadBackupUseCase(backups),
                restoreBackup = RestoreBackupUseCase(backups),
                exportCsv = ExportCsvUseCase(csvFiles),
                settings = settings,
            )
        backgroundScope.launch { model.state.collect {} }
        return model
    }

    /** FR-110: the password dialog, then the picked file, writes the backup and reports it saved. */
    @Test
    fun `given an export when password and file are given then the backup is saved (FR-110)`() =
        runTest {
            val model = watched()
            model.startExport()
            runCurrent()
            assertThat(model.state.value.step).isEqualTo(BackupStep.ExportPassword)
            model.onExportPassword("rahasia-123")
            model.onExportTarget("content://file")
            runCurrent()
            assertThat(exports).isEqualTo(listOf("content://file:rahasia-123"))
            assertThat(model.state.value.outcome).isEqualTo(BackupOutcome.Saved)
            model.dismissOutcome()
            runCurrent()
            assertThat(model.state.value.outcome).isNull()
        }

    /** FR-110: a cancelled picker or a missing password writes nothing; a failure is reported. */
    @Test
    fun `given an export when the picker is cancelled or the write fails then nothing is saved (FR-110)`() =
        runTest {
            val model = watched()
            model.onExportTarget("content://file")
            model.onExportPassword("rahasia-123")
            model.onExportTarget(null)
            runCurrent()
            assertThat(exports).isEqualTo(emptyList())
            failure = BackupFailure.WRITE_FAILED
            model.onExportPassword("rahasia-123")
            model.onExportTarget("content://file")
            runCurrent()
            assertThat(model.state.value.outcome).isEqualTo(BackupOutcome.Failed(BackupFailure.WRITE_FAILED))
        }

    /** FR-111: a picked file asks for its password, shows the summary and restores on confirm. */
    @Test
    fun `given a picked backup when the password is right and confirmed then it is restored (FR-111)`() =
        runTest {
            val model = watched()
            model.onImportPicked(null)
            model.onImportPassword("ignored")
            model.confirmRestore()
            runCurrent()
            assertThat(model.state.value.step).isNull()
            model.onImportPicked("content://backup")
            runCurrent()
            assertThat(model.state.value.step).isEqualTo(BackupStep.ImportPassword("content://backup"))
            model.onImportPassword("rahasia-123")
            runCurrent()
            assertThat(model.state.value.step).isEqualTo(BackupStep.ConfirmRestore(snapshot))
            model.confirmRestore()
            runCurrent()
            assertThat(restores).isEqualTo(listOf(snapshot))
            assertThat(model.state.value.outcome).isEqualTo(BackupOutcome.Restored)
        }

    /** FR-111: a wrong password or a bad file is reported and nothing is restored. */
    @Test
    fun `given a picked backup when it cannot be read then the failure is shown (FR-111)`() =
        runTest {
            val model = watched()
            failure = BackupFailure.WRONG_PASSWORD
            model.onImportPicked("content://backup")
            model.onImportPassword("salah")
            runCurrent()
            assertThat(model.state.value.outcome).isEqualTo(BackupOutcome.Failed(BackupFailure.WRONG_PASSWORD))
            assertThat(model.state.value.busy).isFalse()
            assertThat(restores).isEqualTo(emptyList())
        }

    /** FR-113: the range dialog opens on the budget period; the picked range is written to the file. */
    @Test
    fun `given the csv export when a range and a file are given then the csv is written (FR-113)`() =
        runTest {
            val model = watched()
            model.startCsvExport()
            runCurrent()
            assertThat(model.state.value.step is BackupStep.CsvRange).isTrue()
            val range = LocalDate.of(2026, 11, 1)..LocalDate.of(2026, 11, 30)
            model.onCsvRangePicked(range)
            model.onCsvTarget("content://csv")
            runCurrent()
            assertThat(csvRanges).isEqualTo(listOf(range))
            assertThat(model.state.value.outcome).isEqualTo(BackupOutcome.CsvSaved)
            model.onCsvTarget("content://again")
            model.onCsvRangePicked(range)
            model.onCsvTarget(null)
            runCurrent()
            assertThat(csvRanges.size).isEqualTo(1)
        }

    /** FR-112: the reminder choice is stored at once; dismissing a dialog changes nothing. */
    @Test
    fun `given the reminder pick when a length is chosen then it is stored (FR-112)`() =
        runTest {
            val model = watched()
            model.startReminderPick()
            runCurrent()
            assertThat(model.state.value.step).isEqualTo(BackupStep.ReminderDays)
            model.onReminderDaysPicked(14)
            runCurrent()
            assertThat(
                settings
                    .observe()
                    .first()
                    .backup.backupReminderDays,
            ).isEqualTo(14)
            assertThat(model.state.value.reminderDays).isEqualTo(14)
            model.startExport()
            model.dismissStep()
            runCurrent()
            assertThat(model.state.value.step).isNull()
        }
}
