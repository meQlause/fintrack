package com.meqlause.fintrack.core.backup

import com.meqlause.fintrack.core.model.DatabaseVersion

// DOC-11: fixed parts of the .fintrack file: magic, versions, key derivation, cipher and size limits
internal object BackupFormat {
    const val MAGIC = "FINTRACK"
    const val CONTAINER_VERSION = 1
    const val SCHEMA_VERSION = 1
    const val DATABASE_VERSION = DatabaseVersion.CURRENT
    const val ITERATIONS = 310_000
    const val MAX_ITERATIONS = 10_000_000
    const val SALT_BYTES = 16
    const val IV_BYTES = 12
    const val TAG_BITS = 128
    const val TAG_BYTES = 16
    const val KEY_BITS = 256
    const val HEADER_BYTES = 42
    const val MIN_PASSWORD_LENGTH = 8
    const val MAX_FILE_BYTES = 50L * 1024 * 1024
    const val MAX_PAYLOAD_BYTES = 256L * 1024 * 1024
}
