package com.meqlause.fintrack.feature.lock

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.meqlause.fintrack.core.domain.usecase.ObserveBiometricEnabledUseCase
import com.meqlause.fintrack.core.domain.usecase.ObservePinLockUseCase
import com.meqlause.fintrack.core.domain.usecase.SetPinUseCase
import com.meqlause.fintrack.core.domain.usecase.VerifyPinUseCase
import com.meqlause.fintrack.core.testing.coroutine.MainDispatcherRule
import com.meqlause.fintrack.core.testing.repository.FakeSettingsRepository
import com.meqlause.fintrack.core.testing.security.FakePinHasher
import com.meqlause.fintrack.core.testing.time.FakeClock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.time.Instant

// FR-100, FR-103, S-02: the lock screen state after digits are typed
@OptIn(ExperimentalCoroutinesApi::class)
class LockViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val settings = FakeSettingsRepository()
    private val hasher = FakePinHasher()
    private val clock = FakeClock(Instant.parse("2026-11-23T10:00:00Z"))

    private fun viewModel(): LockViewModel =
        LockViewModel(
            verifyPin = VerifyPinUseCase(settings, hasher, clock),
            observePinLock = ObservePinLockUseCase(settings),
            observeBiometric = ObserveBiometricEnabledUseCase(settings),
            clock = clock,
        )

    private suspend fun setPin(digits: String) {
        SetPinUseCase(settings, hasher).invoke(digits)
    }

    // FR-100: the correct PIN opens the lock
    @Test
    fun `given correct PIN when six digits typed then the lock opens (FR-100)`() =
        runTest {
            setPin("123456")
            val viewModel = viewModel()
            val collector = launch { viewModel.state.collect {} }
            "123456".forEach { viewModel.onDigit(it.toString()) }
            runCurrent()
            assertThat(viewModel.state.value.unlocked).isTrue()
            collector.cancel()
        }

    // FR-100: a wrong PIN clears the dots and shows the notice with a shake
    @Test
    fun `given wrong PIN when six digits typed then the dots clear and the shake counts up (FR-100)`() =
        runTest {
            setPin("123456")
            val viewModel = viewModel()
            val collector = launch { viewModel.state.collect {} }
            "000000".forEach { viewModel.onDigit(it.toString()) }
            runCurrent()
            assertThat(viewModel.state.value.digits).isEqualTo("")
            assertThat(viewModel.state.value.notice).isEqualTo(PinNotice.WRONG)
            assertThat(viewModel.state.value.shakeCount).isEqualTo(1)
            assertThat(viewModel.state.value.unlocked).isFalse()
            collector.cancel()
        }

    // FR-103: the fifth wrong PIN starts a 30 second countdown, and digits are ignored while it runs
    @Test
    fun `given five wrong PINs when typed then a 30 second countdown ignores further digits (FR-103)`() =
        runTest {
            setPin("123456")
            val viewModel = viewModel()
            val collector = launch { viewModel.state.collect {} }
            repeat(5) {
                "000000".forEach { viewModel.onDigit(it.toString()) }
                runCurrent()
            }
            runCurrent()
            assertThat(viewModel.state.value.remainingSeconds).isEqualTo(30L)
            viewModel.onDigit("1")
            assertThat(viewModel.state.value.digits).isEqualTo("")
            collector.cancel()
        }

    // FR-100: backspace removes the last digit
    @Test
    fun `given digits typed when backspace pressed then the last digit is removed (FR-100)`() =
        runTest {
            setPin("123456")
            val viewModel = viewModel()
            val collector = launch { viewModel.state.collect {} }
            viewModel.onDigit("1")
            viewModel.onDigit("2")
            viewModel.onBackspace()
            runCurrent()
            assertThat(viewModel.state.value.digits).isEqualTo("1")
            collector.cancel()
        }

    // FR-100: a seventh digit typed before the sixth is handled is ignored
    @Test
    fun `given six digits typed when another digit comes at once then it is ignored (FR-100)`() =
        runTest {
            setPin("123456")
            val viewModel = viewModel()
            val collector = launch { viewModel.state.collect {} }
            "123456".forEach { viewModel.onDigit(it.toString()) }
            viewModel.onDigit("7")
            runCurrent()
            assertThat(viewModel.state.value.unlocked).isTrue()
            collector.cancel()
        }
}
