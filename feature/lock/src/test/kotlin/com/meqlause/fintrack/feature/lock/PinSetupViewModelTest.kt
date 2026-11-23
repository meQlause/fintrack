package com.meqlause.fintrack.feature.lock

import androidx.lifecycle.SavedStateHandle
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isGreaterThan
import assertk.assertions.isTrue
import com.meqlause.fintrack.core.domain.usecase.ObservePinLockUseCase
import com.meqlause.fintrack.core.domain.usecase.RemovePinUseCase
import com.meqlause.fintrack.core.domain.usecase.SetPinUseCase
import com.meqlause.fintrack.core.domain.usecase.VerifyPinUseCase
import com.meqlause.fintrack.core.testing.coroutine.MainDispatcherRule
import com.meqlause.fintrack.core.testing.repository.FakeSettingsRepository
import com.meqlause.fintrack.core.testing.security.FakePinHasher
import com.meqlause.fintrack.core.testing.time.FakeClock
import com.meqlause.fintrack.feature.lock.navigation.PinSetupMode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.Instant

// FR-100, FR-103, DOC-10: the PIN setup, change and remove flows of PinSetupViewModel
@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class PinSetupViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val settings = FakeSettingsRepository()
    private val hasher = FakePinHasher()
    private val clock = FakeClock(Instant.parse("2026-11-23T10:00:00Z"))

    private fun viewModel(mode: PinSetupMode): PinSetupViewModel {
        val verify = VerifyPinUseCase(settings, hasher, clock)
        return PinSetupViewModel(
            savedStateHandle = SavedStateHandle(mapOf("mode" to mode)),
            verifyPin = verify,
            removePin = RemovePinUseCase(verify, settings),
            setPin = SetPinUseCase(settings, hasher),
            observePinLock = ObservePinLockUseCase(settings),
            clock = clock,
        )
    }

    private fun PinSetupViewModel.type(digits: String) = digits.forEach { onDigit(it.toString()) }

    private suspend fun existingPin() {
        SetPinUseCase(settings, hasher).invoke("123456")
    }

    private fun TestScope.watch(model: PinSetupViewModel) = backgroundScope.launch { model.state.collect {} }

    /** FR-100: a new PIN is typed twice; the second time it matches and the setup is done. */
    @Test
    fun `given set mode when the same pin is typed twice then the setup is done (FR-100)`() =
        runTest {
            val model = viewModel(PinSetupMode.SET)
            watch(model)
            model.type("654321")
            runCurrent()
            assertThat(model.state.value.prompt.step).isEqualTo(PinStep.CONFIRM)
            model.type("654321")
            runCurrent()
            assertThat(model.state.value.done).isTrue()
        }

    /** FR-100: a confirmation that differs shakes, shows the mismatch and returns to the first step. */
    @Test
    fun `given set mode when the confirmation differs then it starts over with a mismatch notice (FR-100)`() =
        runTest {
            val model = viewModel(PinSetupMode.SET)
            watch(model)
            model.type("654321")
            runCurrent()
            model.type("111111")
            runCurrent()
            val state = model.state.value
            assertThat(state.done).isFalse()
            assertThat(state.notice).isEqualTo(PinNotice.MISMATCH)
            assertThat(state.prompt.step).isEqualTo(PinStep.NEW)
            assertThat(state.shakeCount).isEqualTo(1)
        }

    /** FR-100: changing the PIN needs the current one first; a wrong one shakes, the right one moves on. */
    @Test
    fun `given change mode when the current pin is wrong then right then a new pin is stored (FR-100)`() =
        runTest {
            existingPin()
            val model = viewModel(PinSetupMode.CHANGE)
            watch(model)
            model.type("000000")
            runCurrent()
            assertThat(model.state.value.notice).isEqualTo(PinNotice.WRONG)
            model.type("123456")
            runCurrent()
            assertThat(model.state.value.prompt.step).isEqualTo(PinStep.NEW)
            model.type("222222")
            runCurrent()
            model.type("222222")
            runCurrent()
            assertThat(model.state.value.done).isTrue()
        }

    /** FR-100: removing the PIN needs the current one; a wrong one keeps it. */
    @Test
    fun `given remove mode when the current pin is wrong then right then the pin is removed (FR-100)`() =
        runTest {
            existingPin()
            val model = viewModel(PinSetupMode.REMOVE)
            watch(model)
            model.type("000000")
            runCurrent()
            assertThat(model.state.value.done).isFalse()
            model.type("123456")
            runCurrent()
            assertThat(model.state.value.done).isTrue()
        }

    /** FR-103: five wrong PINs lock the entry, digits are ignored, and backspace still edits nothing. */
    @Test
    fun `given change mode when five wrong pins are typed then a lockout ignores digits (FR-103)`() =
        runTest {
            existingPin()
            val model = viewModel(PinSetupMode.CHANGE)
            watch(model)
            repeat(5) {
                model.type("000000")
                runCurrent()
            }
            runCurrent()
            assertThat(model.state.value.remainingSeconds).isGreaterThan(0L)
            model.onDigit("1")
            model.onBackspace()
            runCurrent()
            assertThat(model.state.value.digits).isEqualTo("")
        }

    /** FR-100: backspace removes the last typed digit. */
    @Test
    fun `given digits typed when backspace is pressed then the last digit goes (FR-100)`() =
        runTest {
            val model = viewModel(PinSetupMode.SET)
            val collector = watch(model)
            model.type("12")
            model.onBackspace()
            runCurrent()
            assertThat(model.state.value.digits).isEqualTo("1")
            collector.cancel()
        }

    /** FR-100: a seventh digit typed before the sixth is handled is ignored. */
    @Test
    fun `given six digits typed when another digit comes at once then it is ignored (FR-100)`() =
        runTest {
            val model = viewModel(PinSetupMode.SET)
            watch(model)
            model.type("123456")
            model.onDigit("7")
            runCurrent()
            assertThat(model.state.value.prompt.step).isEqualTo(PinStep.CONFIRM)
            assertThat(model.state.value.digits).isEqualTo("")
        }

    /** FR-103: a PIN typed while the lockout is not yet shown is refused by the check and clears the dots. */
    @Test
    fun `given a lockout not yet shown when a pin is typed then the check refuses it (FR-103)`() =
        runTest {
            existingPin()
            val verify = VerifyPinUseCase(settings, hasher, clock)
            repeat(5) { verify("000000") }
            val model = viewModel(PinSetupMode.CHANGE)
            model.type("123456")
            runCurrent()
            assertThat(model.state.value.done).isFalse()
            assertThat(model.state.value.digits).isEqualTo("")
        }

    /** FR-100: a PIN that is not made of digits is refused even when it is typed twice. */
    @Test
    fun `given letters typed twice when confirmed then the setup is refused (FR-100)`() =
        runTest {
            val model = viewModel(PinSetupMode.SET)
            watch(model)
            model.type("abcdef")
            runCurrent()
            model.type("abcdef")
            runCurrent()
            assertThat(model.state.value.done).isFalse()
            assertThat(model.state.value.notice).isEqualTo(PinNotice.MISMATCH)
        }
}
