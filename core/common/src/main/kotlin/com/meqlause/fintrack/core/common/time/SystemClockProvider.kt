package com.meqlause.fintrack.core.common.time

import java.time.Clock

/** DOC-03: production clock provider that reads the device clock in the device's default time zone. */
public class SystemClockProvider : ClockProvider {
    /** DOC-03: the device clock in the default time zone. */
    override fun clock(): Clock = Clock.systemDefaultZone()
}
