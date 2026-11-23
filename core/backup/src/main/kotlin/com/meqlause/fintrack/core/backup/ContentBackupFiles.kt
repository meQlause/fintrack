package com.meqlause.fintrack.core.backup

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import com.meqlause.fintrack.core.domain.backup.BackupException
import com.meqlause.fintrack.core.domain.backup.BackupFailure
import com.meqlause.fintrack.core.domain.port.BackupFiles
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream

/** DOC-11: reads and writes the file the user picked through the system picker; no network is involved */
public class ContentBackupFiles(
    private val context: Context,
) : BackupFiles {
    override suspend fun read(
        fileUri: String,
        maxBytes: Long,
    ): ByteArray {
        val bytes =
            try {
                val input =
                    context.contentResolver.openInputStream(Uri.parse(fileUri))
                        ?: throw BackupException(BackupFailure.READ_FAILED)
                input.use { it.readUpTo(maxBytes.toInt() + 1) }
            } catch (error: IOException) {
                throw BackupException(BackupFailure.READ_FAILED, error)
            }
        if (bytes.size > maxBytes) throw BackupException(BackupFailure.TOO_LARGE)
        return bytes
    }

    override suspend fun write(
        fileUri: String,
        bytes: ByteArray,
    ) {
        try {
            val output =
                context.contentResolver.openOutputStream(Uri.parse(fileUri), "wt")
                    ?: throw BackupException(BackupFailure.WRITE_FAILED)
            output.use { it.write(bytes) }
        } catch (error: IOException) {
            throw BackupException(BackupFailure.WRITE_FAILED, error)
        }
    }

    // DOC-11: best effort; a partial file the provider refuses to delete is left for the user to remove
    override suspend fun delete(fileUri: String) {
        runCatching { DocumentsContract.deleteDocument(context.contentResolver, Uri.parse(fileUri)) }
    }
}

// DOC-11: reads at most [limit] bytes; InputStream.readNBytes(Int) needs API 33 and the minimum is 26
private fun InputStream.readUpTo(limit: Int): ByteArray {
    val out = ByteArrayOutputStream()
    val buffer = ByteArray(READ_CHUNK_BYTES)
    while (out.size() < limit) {
        val count = read(buffer, 0, minOf(buffer.size, limit - out.size()))
        if (count < 0) break
        out.write(buffer, 0, count)
    }
    return out.toByteArray()
}

private const val READ_CHUNK_BYTES = 8_192
