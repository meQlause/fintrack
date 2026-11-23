package com.meqlause.fintrack.core.backup

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.meqlause.fintrack.core.common.time.ClockProvider
import com.meqlause.fintrack.core.domain.backup.BackupFailure
import com.meqlause.fintrack.core.domain.port.BackupFiles
import com.meqlause.fintrack.core.domain.port.BackupStore
import com.meqlause.fintrack.core.domain.repository.SettingsRepository
import com.meqlause.fintrack.core.model.BackupAdjunct
import com.meqlause.fintrack.core.model.BackupLedger
import com.meqlause.fintrack.core.model.BackupSnapshot
import com.meqlause.fintrack.core.model.Preferences
import com.meqlause.fintrack.core.model.Settings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.time.Clock
import java.time.ZoneOffset

// FR-110, FR-111: export proves itself, a wrong password changes nothing, and a restore replaces the store
/** File backup repository tests. */
class FileBackupRepositoryTest {
    private val password = "rahasia-123"
    private val store = FakeBackupStore()
    private val settings = FakeSettings()
    private val files = FakeBackupFiles()
    private val clock =
        object : ClockProvider {
            override fun clock(): Clock = Clock.fixed(SampleSnapshot.fixedTime, ZoneOffset.UTC)
        }
    private val repository = FileBackupRepository(store, settings, files, clock, Dispatchers.Unconfined, "1.0.0")

    /** Export then read then restore keeps every table. */
    @Test
    fun exportThenReadThenRestoreKeepsEveryTable() =
        runBlocking {
            val sample = SampleSnapshot.build()
            store.replaceAll(sample)
            settings.update { stored ->
                stored.copy(
                    preferences = sample.preferences,
                    backup = stored.backup.copy(backupReminderDays = sample.backupReminderDays),
                )
            }
            repository.export(BACKUP_URI, password)
            assertThat(settings.current.backup.lastBackupAt).isEqualTo(SampleSnapshot.fixedTime)

            store.replaceAll(emptySnapshot())
            settings.update { Settings() }
            val read = repository.readBackup(BACKUP_URI, password)
            repository.restore(read)

            assertThat(store.current).isEqualTo(sample)
            assertThat(settings.current.preferences).isEqualTo(sample.preferences)
            assertThat(settings.current.backup.backupReminderDays).isEqualTo(sample.backupReminderDays)
        }

    /** Wrong password leaves the store untouched. */
    @Test
    fun wrongPasswordLeavesTheStoreUntouched() =
        runBlocking {
            store.replaceAll(SampleSnapshot.build())
            repository.export(BACKUP_URI, password)
            val before = store.current
            assertThat(failureOf { runBlocking { repository.readBackup(BACKUP_URI, "salah-sandi") } })
                .isEqualTo(BackupFailure.WRONG_PASSWORD)
            assertThat(store.current).isEqualTo(before)
        }

    /** Short password is refused before writing. */
    @Test
    fun shortPasswordIsRefusedBeforeWriting() =
        runBlocking {
            store.replaceAll(SampleSnapshot.build())
            assertThat(failureOf { runBlocking { repository.export(BACKUP_URI, "pendek") } })
                .isEqualTo(BackupFailure.PASSWORD_TOO_SHORT)
            assertThat(files.stored).isNull()
        }

    private fun emptySnapshot(): BackupSnapshot =
        BackupSnapshot(
            createdAt = SampleSnapshot.fixedTime,
            ledger = BackupLedger(emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList()),
            adjunct = BackupAdjunct(emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList()),
            preferences = Preferences(),
            backupReminderDays = 0,
        )

    private companion object {
        const val BACKUP_URI = "content://test/backup.fintrack"
    }
}

internal class FakeBackupStore : BackupStore {
    var current: BackupSnapshot = SampleSnapshot.build()

    override suspend fun read(): Pair<BackupLedger, BackupAdjunct> = current.ledger to current.adjunct

    override suspend fun replaceAll(snapshot: BackupSnapshot) {
        current = snapshot
    }
}

internal class FakeSettings : SettingsRepository {
    private val state = MutableStateFlow(Settings())
    val current: Settings get() = state.value

    override fun observe(): StateFlow<Settings> = state

    override suspend fun update(transform: (Settings) -> Settings) {
        state.value = transform(state.value)
    }
}

internal class FakeBackupFiles : BackupFiles {
    var stored: ByteArray? = null

    override suspend fun read(
        fileUri: String,
        maxBytes: Long,
    ): ByteArray = checkNotNull(stored)

    override suspend fun write(
        fileUri: String,
        bytes: ByteArray,
    ) {
        stored = bytes
    }

    override suspend fun delete(fileUri: String) {
        stored = null
    }
}
