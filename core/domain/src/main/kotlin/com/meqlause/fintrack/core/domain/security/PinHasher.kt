package com.meqlause.fintrack.core.domain.security

/** DOC-10, FR-100: hashes and checks the PIN; the PBKDF2 implementation lives in core:security. */
public interface PinHasher {
    /** DOC-10: a new random salt, Base64 encoded, for one PIN. */
    public fun newSalt(): String

    /** DOC-10: the PBKDF2 hash of [digits] with [salt], Base64 encoded. */
    public fun hash(
        digits: String,
        salt: String,
    ): String

    /** DOC-10: true when [digits] with [salt] hashes to [expectedHash]; the comparison is constant time. */
    public fun matches(
        digits: String,
        salt: String,
        expectedHash: String,
    ): Boolean
}
