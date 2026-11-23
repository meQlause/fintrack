package com.meqlause.fintrack.core.backup

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import com.meqlause.fintrack.core.domain.backup.BackupException
import com.meqlause.fintrack.core.domain.backup.BackupFailure
import com.meqlause.fintrack.core.domain.repository.BackupFiles
import java.io.IOException

// DOC-11: reads and writes the file the user picked through the system picker; no network is involved
public class ContentBackupFiles(
    private val context: Context,
) : BackupFiles {
    override suspend fun read(
        uri: String,
        maxBytes: Long,
    ): ByteArray {
        val bytes =
            try {
                val input =
                    context.contentResolver.openInputStream(Uri.parse(uri))
                        ?: throw BackupException(BackupFailure.READ_FAILED)
                input.use { it.readNBytes(maxBytes.toInt() + 1) }
            } catch (error: IOException) {
                throw BackupException(BackupFailure.READ_FAILED, error)
            }
        if (bytes.size > maxBytes) throw BackupException(BackupFailure.TOO_LARGE)
        return bytes
    }

    override suspend fun write(
        uri: String,
        bytes: ByteArray,
    ) {
        try {
            val output =
                context.contentResolver.openOutputStream(Uri.parse(uri), "wt")
                    ?: throw BackupException(BackupFailure.WRITE_FAILED)
            output.use { it.write(bytes) }
        } catch (error: IOException) {
            throw BackupException(BackupFailure.WRITE_FAILED, error)
        }
    }

    // DOC-11: best effort; a partial file the provider refuses to delete is left for the user to remove
    override suspend fun delete(uri: String) {
        runCatching { DocumentsContract.deleteDocument(context.contentResolver, Uri.parse(uri)) }
    }
}
