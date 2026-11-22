package com.meqlause.fintrack.core.security

import android.security.keystore.KeyProperties
import java.security.Key
import java.security.KeyStore
import javax.crypto.KeyGenerator

/** ADR-009: [KeystoreAccess] backed by the Android Keystore; it only runs on a device, so it stays a thin adapter. */
class AndroidKeystoreAccess : KeystoreAccess {
    override fun findKey(): Key? {
        val keyStore = KeyStore.getInstance(PROVIDER).apply { load(null) }
        return keyStore.getKey(KEY_ALIAS, null)
    }

    override fun createKey(): Key {
        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, PROVIDER)
        generator.init(keystoreKeySpec(KEY_ALIAS))
        return generator.generateKey()
    }

    private companion object {
        const val PROVIDER = "AndroidKeyStore"
        const val KEY_ALIAS = "fintrack_db_key"
    }
}
