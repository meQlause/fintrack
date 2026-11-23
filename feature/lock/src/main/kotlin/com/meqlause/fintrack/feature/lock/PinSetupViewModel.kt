package com.meqlause.fintrack.feature.lock

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.meqlause.fintrack.core.domain.security.PinCheck
import com.meqlause.fintrack.core.domain.security.PinPolicy
import com.meqlause.fintrack.core.domain.usecase.ObservePinLockUseCase
import com.meqlause.fintrack.core.domain.usecase.RemovePinUseCase
import com.meqlause.fintrack.core.domain.usecase.SetPinUseCase
import com.meqlause.fintrack.core.domain.usecase.VerifyPinUseCase
import com.meqlause.fintrack.feature.lock.navigation.PinSetupDestination
import com.meqlause.fintrack.feature.lock.navigation.PinSetupMode
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

// FR-100, FR-103, DOC-10: drives the PIN setup; CHANGE and REMOVE check the current PIN before anything is stored
@HiltViewModel
internal class PinSetupViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        private val verifyPin: VerifyPinUseCase,
        private val removePin: RemovePinUseCase,
        private val setPin: SetPinUseCase,
        observePinLock: ObservePinLockUseCase,
        private val clock: Clock,
    ) : ViewModel() {
        private val mode = savedStateHandle.toRoute<PinSetupDestination>().mode
        private val entry = MutableStateFlow(PinSetupEntry(step = firstStep(mode)))
        private val lockedUntil = observePinLock().map { it.lockedUntil }
        private val ticks =
            flow {
                while (true) {
                    emit(clock.instant())
                    delay(TICK_MILLIS)
                }
            }

        // FR-100: the steps, the digits and the lockout countdown, emitted again after every change
        val state: StateFlow<PinSetupUiState> =
            combine(entry, lockedUntil, ticks) { current, until, instant ->
                current.toUiState(mode, remainingSeconds(instant, until))
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), PinSetupUiState())

        // FR-100: a digit is added; the sixth digit is handled at once and ignored during a lockout
        fun onDigit(digit: String) {
            if (state.value.remainingSeconds > 0L || entry.value.digits.length >= PinPolicy.LENGTH) return
            val digits = entry.value.digits + digit
            entry.update { it.copy(digits = digits, notice = PinNotice.NONE) }
            if (digits.length == PinPolicy.LENGTH) submit(digits)
        }

        // FR-100: the last digit is removed
        fun onBackspace() {
            entry.update { it.copy(digits = it.digits.dropLast(1), notice = PinNotice.NONE) }
        }

        private fun submit(digits: String) {
            viewModelScope.launch {
                when (entry.value.step) {
                    PinStep.CURRENT -> checkCurrent(digits)
                    PinStep.NEW -> entry.update { it.copy(step = PinStep.CONFIRM, firstDigits = digits, digits = "") }
                    PinStep.CONFIRM -> confirm(digits)
                }
            }
        }

        private suspend fun checkCurrent(digits: String) {
            val check = if (mode == PinSetupMode.REMOVE) removePin(digits) else verifyPin(digits)
            when (check) {
                PinCheck.Accepted -> afterCurrentAccepted()
                PinCheck.Rejected -> entry.update { failed(it, PinNotice.WRONG) }
                is PinCheck.LockedOut -> entry.update { it.copy(digits = "") }
            }
        }

        private fun afterCurrentAccepted() {
            if (mode == PinSetupMode.REMOVE) {
                entry.update { it.copy(done = true, digits = "") }
            } else {
                entry.update { it.copy(step = PinStep.NEW, digits = "", notice = PinNotice.NONE) }
            }
        }

        private suspend fun confirm(digits: String) {
            if (digits == entry.value.firstDigits && setPin(digits)) {
                entry.update { it.copy(done = true, digits = "") }
            } else {
                entry.update { failed(it, PinNotice.MISMATCH).copy(step = PinStep.NEW, firstDigits = "") }
            }
        }

        private fun failed(
            current: PinSetupEntry,
            notice: PinNotice,
        ): PinSetupEntry = current.copy(digits = "", notice = notice, shakeCount = current.shakeCount + 1)

        private fun PinSetupEntry.toUiState(
            mode: PinSetupMode,
            remainingSeconds: Long,
        ): PinSetupUiState =
            PinSetupUiState(
                mode = mode,
                step = step,
                digits = digits,
                notice = notice,
                remainingSeconds = remainingSeconds,
                shakeCount = shakeCount,
                done = done,
            )

        // FR-103: the seconds left of a lockout, 0 when the PIN can be tried again
        private fun remainingSeconds(
            instant: Instant,
            until: Instant,
        ): Long = Duration.between(instant, until).seconds.coerceAtLeast(0L)

        private fun firstStep(mode: PinSetupMode): PinStep =
            if (mode ==
                PinSetupMode.SET
            ) {
                PinStep.NEW
            } else {
                PinStep.CURRENT
            }
    }
