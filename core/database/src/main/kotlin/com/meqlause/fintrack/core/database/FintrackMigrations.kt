package com.meqlause.fintrack.core.database

import androidx.room.migration.Migration

/** NFR-009, FR-039: every schema migration, oldest first; no destructive fallback exists. */
object FintrackMigrations {
    /** FR-039: a shortcut gets its own optional note, empty for the shortcuts saved before version 2. */
    val FROM_1_TO_2: Migration =
        Migration(startVersion = 1, endVersion = 2) { database ->
            database.execSQL("ALTER TABLE quick_entry ADD COLUMN note TEXT NOT NULL DEFAULT ''")
        }
}
