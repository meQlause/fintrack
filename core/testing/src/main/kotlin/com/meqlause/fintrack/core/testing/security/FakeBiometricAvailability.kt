package com.meqlause.fintrack.core.testing.security

import com.meqlause.fintrack.core.domain.security.BiometricAvailability

// FR-101: a device whose biometric support is fixed by the test
public class FakeBiometricAvailability(
    private val supported: Boolean = true,
) : BiometricAvailability {
    override fun isSupported(): Boolean = supported
}
