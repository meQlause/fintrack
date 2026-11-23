package com.meqlause.fintrack.feature.settings

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.meqlause.fintrack.core.model.ThemeMode
import com.meqlause.fintrack.core.testing.repository.FakeSettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

// FR-120, FR-121: SettingsViewModel against the fake repository
@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setMainDispatcher() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun resetMainDispatcher() {
        Dispatchers.resetMain()
    }

    // FR-120: with nothing stored, the theme follows the system and the period starts on day 1
    @Test
    fun givenNothingStoredWhenScreenOpensThenDefaultsAreShown() =
        runTest(dispatcher) {
            val viewModel = SettingsViewModel(FakeSettingsRepository())
            val collector = backgroundScope.launch { viewModel.state.collect {} }
            advanceUntilIdle()

            assertThat(viewModel.state.value.themeMode).isEqualTo(ThemeMode.SYSTEM)
            assertThat(viewModel.state.value.periodStartDay).isEqualTo(1)
            collector.cancel()
        }

    // FR-120: picking a theme stores it and closes the drawer
    @Test
    fun givenThemePickedWhenSavedThenSettingIsStoredAndDrawerCloses() =
        runTest(dispatcher) {
            val repository = FakeSettingsRepository()
            val viewModel = SettingsViewModel(repository)
            val collector = backgroundScope.launch { viewModel.state.collect {} }
            viewModel.openSheet(SettingsSheet.THEME)

            viewModel.pickThemeMode(ThemeMode.DARK)
            advanceUntilIdle()

            assertThat(
                repository
                    .observe()
                    .first()
                    .preferences.themeMode,
            ).isEqualTo(ThemeMode.DARK)
            assertThat(viewModel.state.value.sheet).isNull()
            collector.cancel()
        }

    // FR-121: picking a period start day stores it
    @Test
    fun givenPeriodDayPickedWhenSavedThenSettingIsStored() =
        runTest(dispatcher) {
            val repository = FakeSettingsRepository()
            val viewModel = SettingsViewModel(repository)
            val collector = backgroundScope.launch { viewModel.state.collect {} }

            viewModel.pickPeriodStartDay(25)
            advanceUntilIdle()

            assertThat(
                repository
                    .observe()
                    .first()
                    .preferences.periodStartDay,
            ).isEqualTo(25)
            collector.cancel()
        }

    // S-12: dismissing a drawer keeps the stored value
    @Test
    fun givenDrawerOpenWhenDismissedThenNothingIsChanged() =
        runTest(dispatcher) {
            val repository = FakeSettingsRepository()
            val viewModel = SettingsViewModel(repository)
            val collector = backgroundScope.launch { viewModel.state.collect {} }
            viewModel.openSheet(SettingsSheet.PERIOD_DAY)

            viewModel.dismissSheet()
            advanceUntilIdle()

            assertThat(
                repository
                    .observe()
                    .first()
                    .preferences.periodStartDay,
            ).isEqualTo(1)
            assertThat(viewModel.state.value.sheet).isNull()
            collector.cancel()
        }
}
