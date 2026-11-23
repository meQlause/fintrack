package com.meqlause.fintrack.core.security

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

// DOC-10: PBKDF2WithHmacSHA256, 210,000 iterations, 16-byte salt, 32-byte hash; both stored Base64 encoded
public class Pbkdf2PinHasher(
    private val random: SecureRandom = SecureRandom(),
) {
    /** DOC-10: a new random 16-byte salt, Base64 encoded. */
    public fun newSalt(): String {
        val bytes = ByteArray(SALT_BYTES)
        random.nextBytes(bytes)
        return Base64.getEncoder().encodeToString(bytes)
    }

    /** DOC-10: the PBKDF2 hash of [digits] with the Base64 [salt], Base64 encoded. */
    public fun hash(
        digits: String,
        salt: String,
    ): String {
        val spec = PBEKeySpec(digits.toCharArray(), Base64.getDecoder().decode(salt), ITERATIONS, KEY_BITS)
        try {
            val factory = SecretKeyFactory.getInstance(ALGORITHM)
            return Base64.getEncoder().encodeToString(factory.generateSecret(spec).encoded)
        } finally {
            spec.clearPassword()
        }
    }

    /** DOC-10: true when the hash of [digits] equals [expectedHash]; compared in constant time. */
    public fun matches(
        digits: String,
        salt: String,
        expectedHash: String,
    ): Boolean {
        val actual = Base64.getDecoder().decode(hash(digits, salt))
        val expected = Base64.getDecoder().decode(expectedHash)
        return MessageDigest.isEqual(actual, expected)
    }

    private companion object {
        const val ALGORITHM = "PBKDF2WithHmacSHA256"
        const val ITERATIONS = 210_000
        const val KEY_BITS = 256
        const val SALT_BYTES = 16
    }
}
