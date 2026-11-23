package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.domain.port.AtomicRunner

// BR-011: the in-memory fakes are already atomic per write, so the block simply runs
internal class ImmediateAtomicRunner : AtomicRunner {
    override suspend fun <T> atomically(block: suspend () -> T): T = block()
}
