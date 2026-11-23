package com.meqlause.fintrack.core.security

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotEqualTo
import assertk.assertions.isTrue
import org.junit.Test
import java.util.Base64

// DOC-10: a PIN is stored only as a salted PBKDF2-HMAC-SHA256 hash, Base64 encoded

/** DOC-10: PBKDF2 PIN hasher tests. */
class Pbkdf2PinHasherTest {
    private val hasher = Pbkdf2PinHasher()
    private val salt = hasher.newSalt()

    /** DOC-10: a salt is 16 random bytes, Base64 encoded, and two salts differ. */
    @Test
    fun givenNewSaltWhenCreatedThenItIsSixteenBytesAndRandom() {
        assertThat(Base64.getDecoder().decode(salt)).hasSize(16)
        assertThat(hasher.newSalt()).isNotEqualTo(hasher.newSalt())
    }

    /** DOC-10: a hash is 32 bytes, Base64 encoded, and the same PIN with the same salt gives the same hash. */
    @Test
    fun givenPinAndSaltWhenHashedTwiceThenHashIsThirtyTwoBytesAndStable() {
        val first = hasher.hash("123456", salt)
        assertThat(Base64.getDecoder().decode(first)).hasSize(32)
        assertThat(hasher.hash("123456", salt)).isEqualTo(first)
    }

    /** DOC-10: the same PIN under another salt gives another hash, so equal PINs do not look alike. */
    @Test
    fun givenSamePinWithOtherSaltWhenHashedThenHashDiffers() {
        val otherSalt = hasher.newSalt()
        assertThat(hasher.hash("123456", otherSalt)).isNotEqualTo(hasher.hash("123456", salt))
    }

    /** DOC-10: the right PIN matches its stored hash. */
    @Test
    fun givenCorrectPinWhenMatchedThenTrue() {
        val stored = hasher.hash("123456", salt)
        assertThat(hasher.matches("123456", salt, stored)).isTrue()
    }

    /** DOC-10: a wrong PIN does not match the stored hash. */
    @Test
    fun givenWrongPinWhenMatchedThenFalse() {
        val stored = hasher.hash("123456", salt)
        assertThat(hasher.matches("654321", salt, stored)).isFalse()
    }
}
