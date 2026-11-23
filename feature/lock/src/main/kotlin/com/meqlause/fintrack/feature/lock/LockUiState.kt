package com.meqlause.fintrack.feature.lock

// S-02, FR-100, FR-101, FR-103: what the lock screen renders; remainingSeconds is the lockout countdown, 0 when free
internal data class LockUiState(
    val digits: String = "",
    val notice: PinNotice = PinNotice.NONE,
    val remainingSeconds: Long = 0L,
    val shakeCount: Int = 0,
    val unlocked: Boolean = false,
    val biometricEnabled: Boolean = false,
)
