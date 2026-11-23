package com.meqlause.fintrack.core.backup

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEqualTo
import com.meqlause.fintrack.core.domain.backup.BackupFailure
import org.junit.Test

// DOC-11: round trip and the tamper tests that must each fail cleanly
/** Backup codec tests. */
class BackupCodecTest {
    private val password = "rahasia-123"
    private val snapshot = SampleSnapshot.build()
    private val bytes = BackupCodec.encode(snapshot, password, "1.0.0")

    /** Round trip restores every table. */
    @Test
    fun roundTripRestoresEveryTable() {
        val restored = BackupCodec.decode(bytes, password)
        assertThat(restored).isEqualTo(snapshot)
    }

    /** Wrong password is rejected. */
    @Test
    fun wrongPasswordIsRejected() {
        assertThat(failureOf { BackupCodec.decode(bytes, "salah-sandi") }).isEqualTo(BackupFailure.WRONG_PASSWORD)
    }

    /** Truncated file is rejected. */
    @Test
    fun truncatedFileIsRejected() {
        val truncated = bytes.copyOf(bytes.size - 10)
        assertThat(failureOf { BackupCodec.decode(truncated, password) }).isEqualTo(BackupFailure.WRONG_PASSWORD)
        val headerOnly = bytes.copyOf(BackupFormat.HEADER_BYTES + 4)
        assertThat(failureOf { BackupCodec.decode(headerOnly, password) }).isEqualTo(BackupFailure.CORRUPT)
    }

    /** Modified header byte is rejected. */
    @Test
    fun modifiedHeaderByteIsRejected() {
        val tampered = bytes.copyOf()
        tampered[SALT_OFFSET] = (tampered[SALT_OFFSET].toInt() xor 1).toByte()
        assertThat(failureOf { BackupCodec.decode(tampered, password) }).isEqualTo(BackupFailure.WRONG_PASSWORD)
    }

    /** Modified ciphertext byte is rejected. */
    @Test
    fun modifiedCiphertextByteIsRejected() {
        val tampered = bytes.copyOf()
        val index = tampered.size - 5
        tampered[index] = (tampered[index].toInt() xor 1).toByte()
        assertThat(failureOf { BackupCodec.decode(tampered, password) }).isEqualTo(BackupFailure.WRONG_PASSWORD)
    }

    /** Foreign file is not abackup. */
    @Test
    fun foreignFileIsNotABackup() {
        val foreign = bytes.copyOf()
        foreign[0] = 'X'.code.toByte()
        assertThat(failureOf { BackupCodec.decode(foreign, password) }).isEqualTo(BackupFailure.NOT_BACKUP)
    }

    /** Newer container version is unsupported. */
    @Test
    fun newerContainerVersionIsUnsupported() {
        val newer = bytes.copyOf()
        newer[VERSION_OFFSET + 1] = 2
        assertThat(failureOf { BackupCodec.decode(newer, password) }).isEqualTo(BackupFailure.UNSUPPORTED_VERSION)
    }

    /** Ciphertext changes with every export. */
    @Test
    fun ciphertextChangesWithEveryExport() {
        assertThat(BackupCodec.encode(snapshot, password, "1.0.0")).isNotEqualTo(bytes)
    }

    private companion object {
        const val VERSION_OFFSET = 8
        const val SALT_OFFSET = 10
    }
}
