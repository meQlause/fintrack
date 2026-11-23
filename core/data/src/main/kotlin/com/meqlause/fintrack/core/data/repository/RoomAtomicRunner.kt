package com.meqlause.fintrack.core.data.repository

import androidx.room.withTransaction
import com.meqlause.fintrack.core.database.FintrackDatabase
import com.meqlause.fintrack.core.domain.repository.AtomicRunner

/** BR-011, ADR-007: runs a block inside one Room transaction, so its writes are kept together or none is. */
public class RoomAtomicRunner(
    private val database: FintrackDatabase,
) : AtomicRunner {
    override suspend fun <T> atomically(block: suspend () -> T): T = database.withTransaction { block() }
}
