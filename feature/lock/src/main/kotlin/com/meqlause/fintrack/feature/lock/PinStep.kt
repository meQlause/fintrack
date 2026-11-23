package com.meqlause.fintrack.feature.lock

// FR-100: the PIN setup steps in order; CURRENT is skipped when a new PIN is set
internal enum class PinStep {
    CURRENT,
    NEW,
    CONFIRM,
}
