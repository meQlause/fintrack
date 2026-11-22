package com.meqlause.fintrack.core.testing.data

import com.meqlause.fintrack.core.model.Audit
import java.time.Instant

// DOC-02: fixed millisecond-precision instants, so the database and the fakes store the same values
private const val SAMPLE_EPOCH_MILLIS: Long = 1_700_000_000_000L

/** DOC-02: a sample instant offset from the fixed base, in milliseconds. */
internal fun anInstant(offsetMillis: Long = 0L): Instant = Instant.ofEpochMilli(SAMPLE_EPOCH_MILLIS + offsetMillis)

/** DOC-02: sample audit stamps with createdAt before updatedAt. */
internal fun anAudit(): Audit = Audit(createdAt = anInstant(), updatedAt = anInstant(OFFSET_UPDATED_MILLIS))

private const val OFFSET_UPDATED_MILLIS: Long = 1_000L
