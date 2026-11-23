package com.meqlause.fintrack.core.domain.port

/** DOC-03, BR-011: runs several repository writes as one atomic unit. */
public interface AtomicRunner {
    /** BR-011: every write made inside [block] is stored together, or none of them is. */
    public suspend fun <T> atomically(block: suspend () -> T): T
}
