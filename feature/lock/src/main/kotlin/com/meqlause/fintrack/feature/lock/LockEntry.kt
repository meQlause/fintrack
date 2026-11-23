package com.meqlause.fintrack.feature.lock

// S-02: the digits typed so far and the last outcome; the lockout time comes from the stored PIN state
internal data class LockEntry(
    val digits: String = "",
    val notice: PinNotice = PinNotice.NONE,
    val shakeCount: Int = 0,
    val unlocked: Boolean = false,
)
