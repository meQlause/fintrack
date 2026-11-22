package com.meqlause.fintrack.core.data.support

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.meqlause.fintrack.core.database.FintrackDatabase

// DOC-02: plain SQLite in memory; the SQLCipher native library does not load under Robolectric
internal fun openInMemoryDatabase(): FintrackDatabase {
    val context = ApplicationProvider.getApplicationContext<Context>()
    return Room.inMemoryDatabaseBuilder(context, FintrackDatabase::class.java).build()
}
