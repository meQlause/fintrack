package com.meqlause.fintrack.core.security

/** ADR-009: wraps and unwraps the database passphrase with a key that never leaves secure storage. */
interface KeyWrapper {
    /** NFR-003: returns the wrapped form of [plain], which is safe to store as a file. */
    fun wrap(plain: ByteArray): ByteArray

    /** NFR-003: reverses [wrap]; fails when the bytes were altered or wrapped with another key. */
    fun unwrap(wrapped: ByteArray): ByteArray
}
