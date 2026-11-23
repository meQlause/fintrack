package com.meqlause.fintrack.core.backup

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.meqlause.fintrack.core.domain.backup.BackupFailure
import org.junit.Test

/** DOC-11: every malformed header, payload or JSON root is rejected with the failure that names it. */
class BackupBranchTest {
    private val password = "rahasia-123"
    private val bytes = BackupCodec.encode(SampleSnapshot.build(), password, "1.0.0")

    /** An unknown container version is reported as an unsupported version. */
    @Test
    fun unknownContainerVersionIsUnsupported() {
        val tampered = bytes.copyOf()
        tampered[BackupFormat.MAGIC.length + 1] = 99
        assertThat(failureOf { BackupCodec.decode(tampered, password) }).isEqualTo(BackupFailure.UNSUPPORTED_VERSION)
    }

    /** An iteration count of zero is outside the allowed range and therefore corrupt. */
    @Test
    fun zeroIterationsAreCorrupt() {
        val tampered = bytes.copyOf()
        val iterationsAt = BackupFormat.HEADER_BYTES - 4
        tampered.fill(0, iterationsAt, BackupFormat.HEADER_BYTES)
        assertThat(failureOf { BackupCodec.decode(tampered, password) }).isEqualTo(BackupFailure.CORRUPT)
    }

    /** A payload without settings cannot be turned into a snapshot. */
    @Test
    fun payloadWithoutSettingsIsCorrupt() {
        assertThat(failureOf { BackupPayload().toSnapshot() }).isEqualTo(BackupFailure.CORRUPT)
    }

    /** A JSON root from a newer schema is refused before the payload is read. */
    @Test
    fun newerSchemaVersionIsUnsupported() {
        val payload = BackupPayload().apply { schemaVersion = BackupFormat.SCHEMA_VERSION + 1 }
        assertThat(failureOf { BackupJson.decode(BackupJson.encode(payload)) })
            .isEqualTo(BackupFailure.UNSUPPORTED_VERSION)
    }

    /** Text that is not JSON is corrupt. */
    @Test
    fun notJsonIsCorrupt() {
        assertThat(failureOf { BackupJson.decode("tidak json".toByteArray(Charsets.UTF_8)) })
            .isEqualTo(BackupFailure.CORRUPT)
    }

    /** A JSON root without a schema version is corrupt. */
    @Test
    fun missingSchemaVersionIsCorrupt() {
        assertThat(failureOf { BackupJson.decode("{}".toByteArray(Charsets.UTF_8)) }).isEqualTo(BackupFailure.CORRUPT)
    }
}
