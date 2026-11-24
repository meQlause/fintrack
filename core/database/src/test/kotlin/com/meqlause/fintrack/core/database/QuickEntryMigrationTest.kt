package com.meqlause.fintrack.core.database

import android.content.Context
import androidx.room.testing.MigrationTestHelper
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

private const val TEST_DB = "migration-test"

/** NFR-009, FR-039: version 1 shortcuts survive the move to version 2 and get an empty note. */
@RunWith(RobolectricTestRunner::class)
class QuickEntryMigrationTest {
    @get:Rule
    val helper = MigrationTestHelper(InstrumentationRegistry.getInstrumentation(), FintrackDatabase::class.java)

    /** NFR-009: the shortcut saved on version 1 keeps every field and reads an empty note on version 2. */
    @Test
    fun `given a version 1 shortcut when migrated then its note is empty and the rest is kept (FR-039)`() {
        helper.createDatabase(testDbPath(), 1).apply {
            execSQL(WALLET_ROW)
            execSQL(CATEGORY_ROW)
            execSQL(QUICK_ROW)
            close()
        }

        val migrated = helper.runMigrationsAndValidate(testDbPath(), 2, true, FintrackMigrations.FROM_1_TO_2)

        migrated.query("SELECT title, note, amount FROM quick_entry").use { cursor ->
            assertThat(cursor.moveToFirst()).isEqualTo(true)
            assertThat(cursor.getString(0)).isEqualTo("Kopi")
            assertThat(cursor.getString(1)).isEqualTo("")
            assertThat(cursor.getLong(2)).isEqualTo(20_000L)
        }
    }

    private fun testDbPath(): String =
        ApplicationProvider.getApplicationContext<Context>().getDatabasePath(TEST_DB).absolutePath

    private companion object {
        const val WALLET_ROW =
            "INSERT INTO wallet (walletId, name, type, initialBalance, iconKey, colorKey, " +
                "isIncludedInTotal, isArchived, sortOrder, createdAt, updatedAt) " +
                "VALUES (1, 'Tunai', 'CASH', 0, 'cash', 'green', 1, 0, 0, 1, 1)"
        const val CATEGORY_ROW =
            "INSERT INTO category (categoryId, name, kind, iconKey, colorKey, isDefault, isArchived, " +
                "sortOrder, createdAt, updatedAt) VALUES (1, 'Makan', 'EXPENSE', 'food', 'orange', 1, 0, 0, 1, 1)"
        const val QUICK_ROW =
            "INSERT INTO quick_entry (quickEntryId, title, sortOrder, type, amount, categoryId, walletId, " +
                "createdAt, updatedAt) VALUES (1, 'Kopi', 0, 'EXPENSE', 20000, 1, 1, 1, 1)"
    }
}
