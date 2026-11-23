package com.meqlause.fintrack.core.backup

import com.meqlause.fintrack.core.domain.backup.BackupException
import com.meqlause.fintrack.core.domain.backup.BackupFailure
import java.nio.ByteBuffer

// DOC-11: the 42-byte plain header; it is passed to AES-GCM as AAD, so any change to it fails authentication
internal class BackupHeader(
    val salt: ByteArray,
    val cipherIv: ByteArray,
    val iterations: Int,
) {
    fun toBytes(): ByteArray {
        val buffer = ByteBuffer.allocate(BackupFormat.HEADER_BYTES)
        buffer.put(BackupFormat.MAGIC.toByteArray(Charsets.US_ASCII))
        buffer.putShort(BackupFormat.CONTAINER_VERSION.toShort())
        buffer.put(salt)
        buffer.put(cipherIv)
        buffer.putInt(iterations)
        return buffer.array()
    }

    companion object {
        // DOC-11: rejects files that are not backups or use another container version before any decryption
        fun parse(bytes: ByteArray): BackupHeader {
            if (bytes.size < BackupFormat.HEADER_BYTES) throw BackupException(BackupFailure.CORRUPT)
            val buffer = ByteBuffer.wrap(bytes, 0, BackupFormat.HEADER_BYTES)
            val magic = ByteArray(BackupFormat.MAGIC.length)
            buffer.get(magic)
            if (!magic.contentEquals(BackupFormat.MAGIC.toByteArray(Charsets.US_ASCII))) {
                throw BackupException(BackupFailure.NOT_BACKUP)
            }
            val version = buffer.getShort().toInt() and UNSIGNED_SHORT_MASK
            if (version != BackupFormat.CONTAINER_VERSION) throw BackupException(BackupFailure.UNSUPPORTED_VERSION)
            val salt = ByteArray(BackupFormat.SALT_BYTES)
            buffer.get(salt)
            val iv = ByteArray(BackupFormat.IV_BYTES)
            buffer.get(iv)
            val iterations = buffer.getInt()
            if (iterations !in 1..BackupFormat.MAX_ITERATIONS) throw BackupException(BackupFailure.CORRUPT)
            return BackupHeader(salt, iv, iterations)
        }

        private const val UNSIGNED_SHORT_MASK = 0xFFFF
    }
}
