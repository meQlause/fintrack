package com.meqlause.fintrack.core.testing.repository

import com.meqlause.fintrack.core.domain.repository.AtomicRunner

/** BR-011: in-memory runner for tests; the fakes are already atomic, so the block simply runs. */
public class PassthroughAtomicRunner : AtomicRunner {
    override suspend fun <T> atomically(block: suspend () -> T): T = block()
}
