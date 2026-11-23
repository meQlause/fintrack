package com.meqlause.fintrack.security

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import com.meqlause.fintrack.core.domain.security.BiometricAvailability

// FR-101: the Android biometric check behind the core:domain port
class DeviceBiometricAvailability(
    private val context: Context,
) : BiometricAvailability {
    // FR-101: true only when the device reports that biometrics can be used now
    override fun isSupported(): Boolean {
        val result = BiometricManager.from(context).canAuthenticate(BIOMETRIC_STRONG or BIOMETRIC_WEAK)
        return result == BiometricManager.BIOMETRIC_SUCCESS
    }
}
