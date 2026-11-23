package com.meqlause.fintrack.feature.lock.navigation

// FR-100: SET makes a new PIN, CHANGE needs the current PIN first, REMOVE needs it and deletes the PIN
public enum class PinSetupMode {
    SET,
    CHANGE,
    REMOVE,
}
