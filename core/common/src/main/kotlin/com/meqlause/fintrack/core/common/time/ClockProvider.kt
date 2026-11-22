package com.meqlause.fintrack.core.common.time

import java.time.Clock

/** DOC-03: injectable source of the current time. Code asks this for a Clock; it never calls Instant.now(). */
public interface ClockProvider {
    /** DOC-03: the clock that defines "now" for the caller. */
    public fun clock(): Clock
}
