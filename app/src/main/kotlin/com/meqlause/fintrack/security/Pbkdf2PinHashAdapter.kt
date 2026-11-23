package com.meqlause.fintrack.security

import com.meqlause.fintrack.core.domain.security.PinHasher
import com.meqlause.fintrack.core.security.Pbkdf2PinHasher

// DOC-10: adapts core:security PBKDF2 hashing to the PinHasher port of core:domain, so core:security needs no domain
internal class Pbkdf2PinHashAdapter(
    private val hasher: Pbkdf2PinHasher,
) : PinHasher {
    override fun newSalt(): String = hasher.newSalt()

    override fun hash(
        digits: String,
        salt: String,
    ): String = hasher.hash(digits, salt)

    override fun matches(
        digits: String,
        salt: String,
        expectedHash: String,
    ): Boolean = hasher.matches(digits, salt, expectedHash)
}
