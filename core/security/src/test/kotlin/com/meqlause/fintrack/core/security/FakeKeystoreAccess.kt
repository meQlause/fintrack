package com.meqlause.fintrack.core.security

import javax.crypto.SecretKey

// ADR-009: in-memory Keystore that counts key creations, so tests can prove a key is reused
internal class FakeKeystoreAccess : KeystoreAccess {
    private var storedKey: SecretKey? = null

    var createdKeyCount: Int = 0
        private set

    override fun findKey(): SecretKey? = storedKey

    override fun createKey(): SecretKey {
        createdKeyCount += 1
        return generateInMemoryAesKey().also { storedKey = it }
    }
}
