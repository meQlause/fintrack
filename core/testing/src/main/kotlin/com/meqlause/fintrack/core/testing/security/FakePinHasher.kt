package com.meqlause.fintrack.core.testing.security

import com.meqlause.fintrack.core.domain.security.PinHasher

// DOC-10: readable hashing for ViewModel tests; the real PBKDF2 hasher lives in core:security
public class FakePinHasher : PinHasher {
    private var counter = 0

    override fun newSalt(): String {
        counter++
        return "salt$counter"
    }

    override fun hash(
        digits: String,
        salt: String,
    ): String = "hash($digits,$salt)"

    override fun matches(
        digits: String,
        salt: String,
        expectedHash: String,
    ): Boolean = hash(digits, salt) == expectedHash
}
