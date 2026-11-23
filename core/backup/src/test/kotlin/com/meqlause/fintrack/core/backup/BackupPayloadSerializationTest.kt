package com.meqlause.fintrack.core.backup

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlinx.serialization.json.Json
import org.junit.Test

/** DOC-11: a payload written without default values reads back to the same snapshot. */
class BackupPayloadSerializationTest {
    private val sparse = Json { encodeDefaults = false }

    /** Fields that hold defaults are left out and read back as defaults. */
    @Test
    fun defaultFieldsAreOmittedAndReadBackAsDefaults() {
        val text = sparse.encodeToString(BackupPayload.serializer(), BackupPayload())
        assertThat(text).isEqualTo("{}")
        val restored = sparse.decodeFromString(BackupPayload.serializer(), text)
        assertThat(restored.schemaVersion).isEqualTo(BackupFormat.SCHEMA_VERSION)
    }

    /** A full payload written without default values restores the whole snapshot. */
    @Test
    fun sparsePayloadRestoresTheSnapshot() {
        val snapshot = SampleSnapshot.build()
        val payload = BackupPayload.from(snapshot, "1.0.0")
        val text = sparse.encodeToString(BackupPayload.serializer(), payload)
        val restored = sparse.decodeFromString(BackupPayload.serializer(), text).toSnapshot()
        assertThat(restored).isEqualTo(snapshot)
    }
}
