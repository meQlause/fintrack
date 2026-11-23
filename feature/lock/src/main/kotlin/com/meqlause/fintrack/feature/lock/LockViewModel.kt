package com.meqlause.fintrack.feature.lock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meqlause.fintrack.core.domain.security.PinCheck
import com.meqlause.fintrack.core.domain.security.PinPolicy
import com.meqlause.fintrack.core.domain.usecase.ObserveBiometricEnabledUseCase
import com.meqlause.fintrack.core.domain.usecase.ObservePinLockUseCase
import com.meqlause.fintrack.core.domain.usecase.VerifyPinUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.Duration
import java.time.Instant
import javax.inject.Inject

private const val TICK_MILLIS = 1_000L
private const val STOP_TIMEOUT_MILLIS = 5_000L

// FR-100, FR-103, S-02: a correct PIN unlocks, a wrong one shakes, and a lockout counts down on screen
@HiltViewModel
internal class LockViewModel
    @Inject
    constructor(
        private val verifyPin: VerifyPinUseCase,
        observePinLock: ObservePinLockUseCase,
        observeBiometric: ObserveBiometricEnabledUseCase,
        private val clock: Clock,
    ) : ViewModel() {
        private val entry = MutableStateFlow(LockEntry())
        private val lockedUntil = observePinLock().map { it.lockedUntil }
        private val biometric = observeBiometric()
        private val ticks =
            flow {
                while (true) {
                    emit(clock.instant())
                    delay(TICK_MILLIS)
                }
            }

        // S-02: the typed digits, the countdown and the shake count, emitted again after every change
        val state: StateFlow<LockUiState> =
            combine(entry, lockedUntil, ticks, biometric) { current, until, instant, enabled ->
                current.toUiState(remainingSeconds(instant, until), enabled)
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), LockUiState())

        // FR-101: a correct biometric unlock opens the app at once and clears the typed digits
        fun onBiometricSucceeded() {
            entry.update { it.copy(digits = "", unlocked = true) }
        }

        // S-02: a digit is added; the sixth digit is checked at once and ignored during a lockout
        fun onDigit(digit: String) {
            if (state.value.remainingSeconds > 0L || entry.value.digits.length >= PinPolicy.LENGTH) return
            val digits = entry.value.digits + digit
            entry.update { it.copy(digits = digits, notice = PinNotice.NONE) }
            if (digits.length == PinPolicy.LENGTH) submit(digits)
        }

        // S-02: the last digit is removed
        fun onBackspace() {
            entry.update { it.copy(digits = it.digits.dropLast(1), notice = PinNotice.NONE) }
        }

        private fun submit(digits: String) {
            viewModelScope.launch {
                when (verifyPin(digits)) {
                    PinCheck.Accepted -> entry.update { it.copy(digits = "", unlocked = true) }
                    PinCheck.Rejected -> entry.update { failed(it) }
                    is PinCheck.LockedOut -> entry.update { it.copy(digits = "") }
                }
            }
        }

        private fun failed(current: LockEntry): LockEntry =
            current.copy(digits = "", notice = PinNotice.WRONG, shakeCount = current.shakeCount + 1)

        private fun LockEntry.toUiState(
            remainingSeconds: Long,
            biometricEnabled: Boolean,
        ): LockUiState =
            LockUiState(
                digits = digits,
                notice = notice,
                remainingSeconds = remainingSeconds,
                shakeCount = shakeCount,
                unlocked = unlocked,
                biometricEnabled = biometricEnabled,
            )

        // FR-103: the seconds left of a lockout, 0 when the PIN can be tried again
        private fun remainingSeconds(
            instant: Instant,
            until: Instant,
        ): Long = Duration.between(instant, until).seconds.coerceAtLeast(0L)
    }
