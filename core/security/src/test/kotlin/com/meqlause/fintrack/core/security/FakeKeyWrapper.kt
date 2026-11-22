package com.meqlause.fintrack.core.security

import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

// NFR-003, ADR-009: in-memory stand-in for the Android Keystore, which Robolectric does not provide
internal class FakeKeyWrapper : KeyWrapper by AesGcmKeyWrapper(generateInMemoryAesKey())

internal fun generateInMemoryAesKey(): SecretKey =
    KeyGenerator
        .getInstance("AES")
        .apply {
            init(AES_KEY_BITS)
        }.generateKey()

private const val AES_KEY_BITS = 256
