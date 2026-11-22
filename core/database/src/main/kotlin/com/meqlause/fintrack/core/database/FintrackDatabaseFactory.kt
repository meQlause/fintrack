package com.meqlause.fintrack.core.database

import android.content.Context
import androidx.room.Room
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory

/** NFR-003, ADR-009: opens fintrack.db encrypted with SQLCipher; the passphrase is supplied by the caller. */
object FintrackDatabaseFactory {
    private const val DATABASE_NAME = "fintrack.db"

    /** NFR-003: opens the encrypted database; no destructive migration fallback exists (NFR-009). */
    fun open(
        context: Context,
        passphrase: ByteArray,
    ): FintrackDatabase =
        Room
            .databaseBuilder(context, FintrackDatabase::class.java, DATABASE_NAME)
            .openHelperFactory(SupportOpenHelperFactory(passphrase))
            .build()
}
