package com.meqlause.fintrack.feature.dashboard

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNull
import com.meqlause.fintrack.core.designsystem.component.UndoWindow
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.QuickEntry
import com.meqlause.fintrack.core.model.QuickEntryId
import com.meqlause.fintrack.core.model.QuickTemplate
import com.meqlause.fintrack.core.model.ThemeMode
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.testing.repository.FakeTransactionRepository
import com.meqlause.fintrack.core.testing.repository.FakeWalletRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Test

// FR-039, FR-040, FR-120: the Beranda actions that change the state or the stored settings
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelActionsTest : HomeViewModelTestBase() {
    // FR-120: the moon button in dark mode switches the stored theme to light
    @Test
    fun givenDarkThemeWhenToggledThenStoredThemeIsLight() =
        runTest {
            val viewModel = viewModel(FakeWalletRepository(), FakeTransactionRepository())

            viewModel.toggleTheme(isDark = true)
            advanceUntilIdle()

            assertThat(
                settings
                    .observe()
                    .first()
                    .preferences.themeMode,
            ).isEqualTo(ThemeMode.LIGHT)
        }

    // FR-120: the sun button in light mode switches the stored theme to dark
    @Test
    fun givenLightThemeWhenToggledThenStoredThemeIsDark() =
        runTest {
            val viewModel = viewModel(FakeWalletRepository(), FakeTransactionRepository())

            viewModel.toggleTheme(isDark = false)
            advanceUntilIdle()

            assertThat(
                settings
                    .observe()
                    .first()
                    .preferences.themeMode,
            ).isEqualTo(ThemeMode.DARK)
        }

    // FR-040: the privacy toggle works before anyone has read a success state
    @Test
    fun givenNoSubscriberWhenToggledThenStateStaysLoading() {
        val viewModel = viewModel(FakeWalletRepository(), FakeTransactionRepository())
        viewModel.toggleHidden()
        viewModel.closeDrawer()
        assertThat(viewModel.state.value).isInstanceOf(HomeUiState.Loading::class)
    }

    // FR-042: the category card opens the Kategori drawer
    @Test
    fun givenCategoriesOpenedThenDrawerIsCategories() =
        runTest {
            val viewModel = viewModel(FakeWalletRepository(), FakeTransactionRepository())
            val collector = backgroundScope.launch { viewModel.state.collect {} }
            advanceUntilIdle()

            viewModel.openCategories()
            advanceUntilIdle()
            assertThat((viewModel.state.value as HomeUiState.Success).drawer).isEqualTo(HomeDrawer.CATEGORIES)
            collector.cancel()
        }

    // FR-039: the Atur link opens the shortcut manager
    @Test
    fun givenShortcutsOpenedThenDrawerIsShortcuts() =
        runTest {
            val viewModel = viewModel(FakeWalletRepository(), FakeTransactionRepository())
            val collector = backgroundScope.launch { viewModel.state.collect {} }
            advanceUntilIdle()

            viewModel.openShortcuts()
            advanceUntilIdle()
            assertThat((viewModel.state.value as HomeUiState.Success).drawer).isEqualTo(HomeDrawer.SHORTCUTS)
            collector.cancel()
        }

    // FR-039: deleting an unknown shortcut changes nothing and offers no undo
    @Test
    fun givenUnknownShortcutWhenDeletedThenNothingIsRemoved() =
        runTest {
            val viewModel = viewModel(FakeWalletRepository(), FakeTransactionRepository())
            val collector = backgroundScope.launch { viewModel.state.collect {} }
            advanceUntilIdle()

            viewModel.deleteShortcut(QuickEntryId(99L))
            runCurrent()
            assertThat((viewModel.state.value as HomeUiState.Success).removedShortcut).isNull()
            collector.cancel()
        }

    // FR-039: the deleted shortcut is gone at once and can be put back with Urungkan
    @Test
    fun givenShortcutDeletedWhenRestoredThenShortcutIsBackAndUndoIsGone() =
        runTest {
            val itemKey = quickEntries.insert(entry())
            val viewModel = viewModel(FakeWalletRepository(), FakeTransactionRepository())
            val collector = backgroundScope.launch { viewModel.state.collect {} }
            advanceUntilIdle()

            viewModel.deleteShortcut(itemKey)
            runCurrent()
            assertThat((viewModel.state.value as HomeUiState.Success).removedShortcut).isEqualTo(entry(itemKey))
            assertThat(quickEntries.observeAll().first()).hasSize(0)

            viewModel.restoreShortcut()
            advanceUntilIdle()
            assertThat((viewModel.state.value as HomeUiState.Success).removedShortcut).isNull()
            assertThat(quickEntries.observeAll().first()).hasSize(1)
            collector.cancel()
        }

    // FR-039: the undo offer lapses after the shared undo window
    @Test
    fun givenShortcutDeletedWhenWindowPassesThenUndoIsGone() =
        runTest {
            val itemKey = quickEntries.insert(entry())
            val viewModel = viewModel(FakeWalletRepository(), FakeTransactionRepository())
            val collector = backgroundScope.launch { viewModel.state.collect {} }
            advanceUntilIdle()

            viewModel.deleteShortcut(itemKey)
            advanceTimeBy(UndoWindow.MILLIS)
            advanceUntilIdle()
            assertThat((viewModel.state.value as HomeUiState.Success).removedShortcut).isNull()
            collector.cancel()
        }

    // FR-039: Urungkan without a deletion does nothing
    @Test
    fun givenNothingDeletedWhenRestoredThenShortcutsAreUnchanged() =
        runTest {
            val viewModel = viewModel(FakeWalletRepository(), FakeTransactionRepository())
            viewModel.restoreShortcut()
            advanceUntilIdle()
            assertThat(quickEntries.observeAll().first()).hasSize(0)
        }

    // FR-039: a second deletion replaces the pending undo offer
    @Test
    fun givenTwoDeletionsWhenRestoredThenOnlyTheLastIsPutBack() =
        runTest {
            val first = quickEntries.insert(entry(sortOrder = 0))
            val second = quickEntries.insert(entry(sortOrder = 1))
            val viewModel = viewModel(FakeWalletRepository(), FakeTransactionRepository())
            val collector = backgroundScope.launch { viewModel.state.collect {} }
            advanceUntilIdle()

            viewModel.deleteShortcut(first)
            runCurrent()
            viewModel.deleteShortcut(second)
            runCurrent()
            assertThat((viewModel.state.value as HomeUiState.Success).removedShortcut?.quickEntryId).isEqualTo(second)
            collector.cancel()
        }

    private fun entry(
        itemKey: QuickEntryId = QuickEntryId(0L),
        sortOrder: Int = 0,
    ): QuickEntry =
        QuickEntry(
            quickEntryId = itemKey,
            template = QuickTemplate(title = "Kopi", type = TransactionType.EXPENSE, amount = Money(18_000L)),
            categoryId = CategoryId(0L),
            walletId = WalletId(0L),
            sortOrder = sortOrder,
            audit = Audit(createdAt = fixedNow, updatedAt = fixedNow),
        )
}
