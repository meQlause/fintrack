package com.meqlause.fintrack.core.domain.security

/** FR-101: port for asking whether this device can unlock with biometrics; the Android check lives in core:security */
public interface BiometricAvailability {
    /** FR-101: true when strong or weak biometrics are enrolled and usable on this device */
    public fun isSupported(): Boolean
}
