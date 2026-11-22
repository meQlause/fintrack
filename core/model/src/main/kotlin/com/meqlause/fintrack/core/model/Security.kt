package com.meqlause.fintrack.core.model

/** DOC-02: app lock settings; the PIN itself is held in PinLock (DOC-10). */
public data class Security(
    public val pinLock: PinLock = PinLock(),
    public val biometricEnabled: Boolean = false,
    public val lockTimeoutSeconds: Int = 30,
    public val hideFromRecents: Boolean = false,
)
