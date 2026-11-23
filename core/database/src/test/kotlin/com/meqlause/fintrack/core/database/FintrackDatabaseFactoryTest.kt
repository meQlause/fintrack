package com.meqlause.fintrack.core.database

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import assertk.assertThat
import assertk.assertions.isFalse
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** NFR-003, ADR-009: the SQLCipher factory builds a handle; the first query opens the file. */
@RunWith(RobolectricTestRunner::class)
class FintrackDatabaseFactoryTest {
    /** NFR-003: opening the factory does not touch the encrypted file until a query runs. */
    @Test
    fun `factory returns a handle that is not opened yet (NFR-003)`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val database = FintrackDatabaseFactory.open(context, ByteArray(PASSPHRASE_BYTES), loadNativeLibrary = {})
        assertThat(database.isOpen).isFalse()
        database.close()
    }

    private companion object {
        const val PASSPHRASE_BYTES = 32
    }
}
