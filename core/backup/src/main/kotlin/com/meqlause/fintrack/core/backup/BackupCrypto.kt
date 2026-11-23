package com.meqlause.fintrack.core.backup

import com.meqlause.fintrack.core.domain.backup.BackupException
import com.meqlause.fintrack.core.domain.backup.BackupFailure
import java.security.SecureRandom
import javax.crypto.AEADBadTagException
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

// DOC-11: PBKDF2-HMAC-SHA256 key derivation and AES-256-GCM sealing with the header as AAD
internal object BackupCrypto {
    private val random = SecureRandom()

    fun newSalt(): ByteArray = ByteArray(BackupFormat.SALT_BYTES).also(random::nextBytes)

    fun newIv(): ByteArray = ByteArray(BackupFormat.IV_BYTES).also(random::nextBytes)

    fun seal(
        plain: ByteArray,
        password: String,
        header: BackupHeader,
    ): ByteArray = cipher(Cipher.ENCRYPT_MODE, password, header).doFinal(plain)

    // DOC-11: a wrong password, a changed header or a changed ciphertext all fail GCM authentication
    fun open(
        sealed: ByteArray,
        password: String,
        header: BackupHeader,
    ): ByteArray =
        try {
            cipher(Cipher.DECRYPT_MODE, password, header).doFinal(sealed)
        } catch (error: AEADBadTagException) {
            throw BackupException(BackupFailure.WRONG_PASSWORD, error)
        }

    private fun cipher(
        mode: Int,
        password: String,
        header: BackupHeader,
    ): Cipher {
        val key = deriveKey(password, header.salt, header.iterations)
        val cipher = Cipher.getInstance(CIPHER_NAME)
        cipher.init(mode, SecretKeySpec(key, KEY_ALGORITHM), GCMParameterSpec(BackupFormat.TAG_BITS, header.iv))
        cipher.updateAAD(header.toBytes())
        return cipher
    }

    private fun deriveKey(
        password: String,
        salt: ByteArray,
        iterations: Int,
    ): ByteArray {
        val spec = PBEKeySpec(password.toCharArray(), salt, iterations, BackupFormat.KEY_BITS)
        try {
            return SecretKeyFactory.getInstance(KDF_NAME).generateSecret(spec).encoded
        } finally {
            spec.clearPassword()
        }
    }

    private const val CIPHER_NAME = "AES/GCM/NoPadding"
    private const val KEY_ALGORITHM = "AES"
    private const val KDF_NAME = "PBKDF2WithHmacSHA256"
}
