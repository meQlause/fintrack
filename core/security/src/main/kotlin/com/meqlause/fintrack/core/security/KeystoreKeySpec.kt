package com.meqlause.fintrack.core.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties

// ADR-009: no user authentication, so the notification listener can still write while the PIN lock is on
internal fun keystoreKeySpec(alias: String): KeyGenParameterSpec {
    val purposes = KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
    return KeyGenParameterSpec
        .Builder(alias, purposes)
        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
        .setKeySize(KEY_BITS)
        .build()
}

private const val KEY_BITS = 256
