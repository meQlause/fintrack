package com.meqlause.fintrack.core.security

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** ADR-009, NFR-003: the Keystore wrapper creates one key on first use and reuses it afterwards. */
@RunWith(RobolectricTestRunner::class)
class AndroidKeystoreWrapperTest {
    private val plain = "database passphrase".toByteArray()

    /** ADR-009: the first wrap creates the Keystore key, and the same key unwraps the result. */
    @Test
    fun `given no keystore key when wrapped then one key is created and it unwraps the bytes (ADR-009)`() {
        val keystore = FakeKeystoreAccess()
        val wrapper = AndroidKeystoreWrapper(keystore)

        val unwrapped = wrapper.unwrap(wrapper.wrap(plain))

        assertThat(unwrapped).isEqualTo(plain)
        assertThat(keystore.createdKeyCount).isEqualTo(1)
    }

    /** ADR-009: a new wrapper over the same Keystore reuses the stored key instead of creating another. */
    @Test
    fun `given a stored keystore key when a new wrapper is created then no key is created (app restart)`() {
        val keystore = FakeKeystoreAccess()
        val wrappedBytes = AndroidKeystoreWrapper(keystore).wrap(plain)

        val restarted = AndroidKeystoreWrapper(keystore)

        assertThat(restarted.unwrap(wrappedBytes)).isEqualTo(plain)
        assertThat(keystore.createdKeyCount).isEqualTo(1)
    }
}
