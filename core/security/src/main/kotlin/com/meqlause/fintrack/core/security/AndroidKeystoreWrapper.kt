package com.meqlause.fintrack.core.security

import java.security.Key

/** ADR-009, NFR-003: wraps with an AES-256-GCM key kept in the Android Keystore, created on first use. */
class AndroidKeystoreWrapper(
    private val keystoreAccess: KeystoreAccess = AndroidKeystoreAccess(),
) : KeyWrapper {
    private val delegate: KeyWrapper by lazy { AesGcmKeyWrapper(findOrCreateKey()) }

    override fun wrap(plain: ByteArray): ByteArray = delegate.wrap(plain)

    override fun unwrap(wrapped: ByteArray): ByteArray = delegate.unwrap(wrapped)

    private fun findOrCreateKey(): Key = keystoreAccess.findKey() ?: keystoreAccess.createKey()
}
