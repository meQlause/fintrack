package com.meqlause.fintrack.feature.lock

// S-02, FR-100: the message shown under the dots, apart from the lockout countdown
internal enum class PinNotice {
    NONE,
    WRONG,
    MISMATCH,
}
