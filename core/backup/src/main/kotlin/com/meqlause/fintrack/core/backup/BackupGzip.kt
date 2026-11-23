package com.meqlause.fintrack.core.backup

import com.meqlause.fintrack.core.domain.backup.BackupException
import com.meqlause.fintrack.core.domain.backup.BackupFailure
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

// DOC-11: the payload is GZIP-compressed before encryption; decompression is capped against oversized payloads
internal object BackupGzip {
    fun compress(bytes: ByteArray): ByteArray {
        val output = ByteArrayOutputStream()
        GZIPOutputStream(output).use { it.write(bytes) }
        return output.toByteArray()
    }

    fun decompress(bytes: ByteArray): ByteArray =
        try {
            GZIPInputStream(bytes.inputStream()).use(::readCapped)
        } catch (error: IOException) {
            throw BackupException(BackupFailure.CORRUPT, error)
        }

    private fun readCapped(input: GZIPInputStream): ByteArray {
        val output = ByteArrayOutputStream()
        val buffer = ByteArray(BUFFER_BYTES)
        var read = input.read(buffer)
        while (read >= 0) {
            output.write(buffer, 0, read)
            if (output.size() > BackupFormat.MAX_PAYLOAD_BYTES) throw BackupException(BackupFailure.CORRUPT)
            read = input.read(buffer)
        }
        return output.toByteArray()
    }

    private const val BUFFER_BYTES = 8192
}
