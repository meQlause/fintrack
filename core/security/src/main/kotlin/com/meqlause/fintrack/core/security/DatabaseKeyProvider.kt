package com.meqlause.fintrack.core.security

import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.security.SecureRandom

/** NFR-003, ADR-009: 32 random bytes are created once, stored wrapped in files/db_key.bin and reused on launch. */
class DatabaseKeyProvider(
    private val filesDirectory: File,
    private val keyWrapper: KeyWrapper,
    private val random: SecureRandom = SecureRandom(),
) {
    /** NFR-003: the SQLCipher passphrase; every call returns the same 32 bytes for the same stored key. */
    fun passphrase(): ByteArray {
        val keyFile = File(filesDirectory, KEY_FILE_NAME)
        return if (keyFile.exists()) {
            keyWrapper.unwrap(keyFile.readBytes())
        } else {
            createPassphrase(keyFile)
        }
    }

    private fun createPassphrase(keyFile: File): ByteArray {
        val passphrase = ByteArray(PASSPHRASE_BYTES).also { random.nextBytes(it) }
        writeAtomically(keyFile, keyWrapper.wrap(passphrase))
        return passphrase
    }

    // NFR-003: a crash mid-write must not leave a truncated key, which would make the database unreadable
    private fun writeAtomically(
        keyFile: File,
        wrapped: ByteArray,
    ) {
        val tempFile = File(filesDirectory, "$KEY_FILE_NAME.tmp")
        tempFile.writeBytes(wrapped)
        Files.move(tempFile.toPath(), keyFile.toPath(), StandardCopyOption.ATOMIC_MOVE)
    }

    private companion object {
        const val KEY_FILE_NAME = "db_key.bin"
        const val PASSPHRASE_BYTES = 32
    }
}
