package com.meqlause.fintrack.core.security

import android.security.keystore.KeyProperties
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** ADR-009: the Keystore key is AES-256-GCM and needs no user authentication. */
@RunWith(RobolectricTestRunner::class)
class KeystoreKeySpecTest {
    /** ADR-009: the spec describes a 256-bit AES-GCM key that is usable without user authentication. */
    @Test
    fun `given the database key spec then it is AES-256-GCM without user authentication (ADR-009)`() {
        val spec = keystoreKeySpec("fintrack_db_key")

        assertThat(spec.keySize).isEqualTo(256)
        assertThat(spec.blockModes.toList()).isEqualTo(listOf(KeyProperties.BLOCK_MODE_GCM))
        assertThat(spec.isUserAuthenticationRequired).isFalse()
    }
}
