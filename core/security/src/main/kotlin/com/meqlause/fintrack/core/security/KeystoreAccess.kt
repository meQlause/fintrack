package com.meqlause.fintrack.core.security

import java.security.Key

/** ADR-009: the Keystore operations the database key needs; the device implementation is AndroidKeystoreAccess. */
interface KeystoreAccess {
    /** ADR-009: the stored key, or null when none has been created yet. */
    fun findKey(): Key?

    /** ADR-009, NFR-003: creates the non-exportable AES-256-GCM key in the Keystore. */
    fun createKey(): Key
}
