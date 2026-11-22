package com.meqlause.fintrack.core.security

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File
import java.nio.charset.StandardCharsets

/** NFR-003, ADR-009: the database passphrase is random, stored wrapped, and stable across app restarts. */
@RunWith(RobolectricTestRunner::class)
class DatabaseKeyProviderTest {
    private val filesDirectory: File = ApplicationProvider.getApplicationContext<Context>().filesDir
    private val keyWrapper = FakeKeyWrapper()

    /** NFR-003: a 32-byte passphrase is created on first use and returned unchanged on the next call. */
    @Test
    fun `given no stored key when passphrase requested twice then the same 32-byte passphrase is returned (NFR-003)`() {
        val provider = DatabaseKeyProvider(filesDirectory, keyWrapper)

        val firstPassphrase = provider.passphrase()
        val secondPassphrase = provider.passphrase()

        assertThat(firstPassphrase).hasSize(32)
        assertThat(secondPassphrase).isEqualTo(firstPassphrase)
    }

    /** ADR-009: a new provider over the same stored file unwraps the key that the first one created. */
    @Test
    fun `given a stored key when a new provider is created then the same passphrase is reused (app restart)`() {
        val firstPassphrase = DatabaseKeyProvider(filesDirectory, keyWrapper).passphrase()

        val restartedPassphrase = DatabaseKeyProvider(filesDirectory, keyWrapper).passphrase()

        assertThat(File(filesDirectory, KEY_FILE_NAME).exists()).isTrue()
        assertThat(restartedPassphrase).isEqualTo(firstPassphrase)
    }

    /** NFR-003: the passphrase is never written to disk in plain form. */
    @Test
    fun `given a stored key file then the file does not contain the plain passphrase (NFR-003)`() {
        val passphrase = DatabaseKeyProvider(filesDirectory, keyWrapper).passphrase()

        val storedText = String(File(filesDirectory, KEY_FILE_NAME).readBytes(), StandardCharsets.ISO_8859_1)
        val passphraseText = String(passphrase, StandardCharsets.ISO_8859_1)

        assertThat(storedText.contains(passphraseText)).isEqualTo(false)
    }

    private companion object {
        const val KEY_FILE_NAME = "db_key.bin"
    }
}
