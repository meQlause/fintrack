package com.meqlause.fintrack.core.backup

import com.meqlause.fintrack.core.common.time.ClockProvider
import com.meqlause.fintrack.core.domain.backup.BackupException
import com.meqlause.fintrack.core.domain.backup.BackupFailure
import com.meqlause.fintrack.core.domain.repository.BackupFiles
import com.meqlause.fintrack.core.domain.repository.BackupRepository
import com.meqlause.fintrack.core.domain.repository.BackupStore
import com.meqlause.fintrack.core.domain.repository.SettingsRepository
import com.meqlause.fintrack.core.model.BackupSnapshot
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

// FR-110, FR-111, DOC-11: encrypted export that proves itself by reading the file back, and validated restore
public class FileBackupRepository(
    private val store: BackupStore,
    private val settings: SettingsRepository,
    private val files: BackupFiles,
    private val clock: ClockProvider,
    private val io: CoroutineDispatcher,
    private val appVersionName: String,
) : BackupRepository {
    override suspend fun export(
        uri: String,
        password: String,
    ): Unit =
        withContext(io) {
            ensurePassword(password)
            val snapshot = BackupScope.scope(readSnapshot())
            val bytes = BackupCodec.encode(snapshot, password, appVersionName)
            writeChecked(uri, bytes, password, snapshot)
            settings.update { stored -> stored.copy(backup = stored.backup.copy(lastBackupAt = snapshot.createdAt)) }
        }

    override suspend fun readBackup(
        uri: String,
        password: String,
    ): BackupSnapshot =
        withContext(io) {
            val snapshot = BackupCodec.decode(files.read(uri, BackupFormat.MAX_FILE_BYTES), password)
            BackupValidator.validate(snapshot)
            snapshot
        }

    override suspend fun restore(snapshot: BackupSnapshot) {
        withContext(io) {
            BackupValidator.validate(snapshot)
            store.replaceAll(snapshot)
        }
        settings.update { stored ->
            stored.copy(
                preferences = snapshot.preferences,
                backup = stored.backup.copy(backupReminderDays = snapshot.backupReminderDays),
            )
        }
    }

    private suspend fun readSnapshot(): BackupSnapshot {
        val (ledger, adjunct) = store.read()
        val stored = settings.observe().first()
        return BackupSnapshot(
            createdAt = clock.clock().instant(),
            ledger = ledger,
            adjunct = adjunct,
            preferences = stored.preferences,
            backupReminderDays = stored.backup.backupReminderDays,
        )
    }

    // DOC-11: the file is only reported as saved once it decrypts and matches the snapshot it was made from
    private suspend fun writeChecked(
        uri: String,
        bytes: ByteArray,
        password: String,
        expected: BackupSnapshot,
    ) {
        try {
            files.write(uri, bytes)
            val written = BackupCodec.decode(files.read(uri, BackupFormat.MAX_FILE_BYTES), password)
            if (written != expected) throw BackupException(BackupFailure.WRITE_FAILED)
        } catch (error: BackupException) {
            files.delete(uri)
            throw error
        }
    }

    private fun ensurePassword(password: String) {
        if (password.length < BackupFormat.MIN_PASSWORD_LENGTH) throw BackupException(BackupFailure.PASSWORD_TOO_SHORT)
    }
}
