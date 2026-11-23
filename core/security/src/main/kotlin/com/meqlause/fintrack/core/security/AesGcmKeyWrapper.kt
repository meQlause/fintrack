package com.meqlause.fintrack.core.security

import java.security.Key
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec

/** NFR-003: AES-256-GCM wrap; the cipher picks the IV (Keystore rejects caller IVs) and it is stored first. */
class AesGcmKeyWrapper(
    private val wrapKey: Key,
) : KeyWrapper {
    override fun wrap(plain: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, wrapKey)
        val nonce = cipher.iv
        check(nonce.size == IV_BYTES) { "Unexpected GCM IV length ${nonce.size}" }
        return nonce + cipher.doFinal(plain)
    }

    override fun unwrap(wrapped: ByteArray): ByteArray {
        val nonce = wrapped.copyOfRange(0, IV_BYTES)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, wrapKey, GCMParameterSpec(TAG_BITS, nonce))
        return cipher.doFinal(wrapped, IV_BYTES, wrapped.size - IV_BYTES)
    }

    private companion object {
        const val TRANSFORMATION = "AES/GCM/NoPadding"
        const val IV_BYTES = 12
        const val TAG_BITS = 128
    }
}
