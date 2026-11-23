package com.meqlause.fintrack.core.backup

import com.meqlause.fintrack.core.domain.backup.BackupException
import com.meqlause.fintrack.core.domain.backup.BackupFailure
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

// DOC-11: UTF-8 JSON of the payload; the schema version is checked before the full payload is read
internal object BackupJson {
    private val json = Json { encodeDefaults = true }

    fun encode(payload: BackupPayload): ByteArray =
        json.encodeToString(BackupPayload.serializer(), payload).toByteArray(Charsets.UTF_8)

    fun decode(bytes: ByteArray): BackupPayload {
        val root = parseRoot(String(bytes, Charsets.UTF_8))
        if (schemaVersion(root) > BackupFormat.SCHEMA_VERSION) throw BackupException(BackupFailure.UNSUPPORTED_VERSION)
        return decodePayload(root)
    }

    private fun parseRoot(text: String): JsonObject =
        try {
            json.parseToJsonElement(text).jsonObject
        } catch (error: IllegalArgumentException) {
            throw BackupException(BackupFailure.CORRUPT, error)
        }

    private fun schemaVersion(root: JsonObject): Int {
        val version =
            try {
                root["schemaVersion"]?.jsonPrimitive?.intOrNull
            } catch (error: IllegalArgumentException) {
                throw BackupException(BackupFailure.CORRUPT, error)
            }
        return version ?: throw BackupException(BackupFailure.CORRUPT)
    }

    private fun decodePayload(root: JsonObject): BackupPayload =
        try {
            json.decodeFromJsonElement(BackupPayload.serializer(), root)
        } catch (error: IllegalArgumentException) {
            throw BackupException(BackupFailure.CORRUPT, error)
        }
}
