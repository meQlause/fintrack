package com.meqlause.fintrack.feature.transaction.shortcut

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.meqlause.fintrack.core.domain.quickentry.QuickEntryError
import com.meqlause.fintrack.core.domain.usecase.SaveQuickEntryUseCase
import com.meqlause.fintrack.core.testing.coroutine.MainDispatcherRule
import com.meqlause.fintrack.core.testing.repository.FakeCategoryRepository
import com.meqlause.fintrack.core.testing.repository.FakeQuickEntryRepository
import com.meqlause.fintrack.core.testing.repository.FakeSettingsRepository
import com.meqlause.fintrack.core.testing.repository.FakeTransactionRepository
import com.meqlause.fintrack.core.testing.repository.FakeWalletRepository
import com.meqlause.fintrack.core.testing.time.FakeClock
import com.meqlause.fintrack.feature.transaction.TransactionSources
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.time.Instant

private const val UNKNOWN_ID = 99L

// FR-039: the edges of the shortcut form, an empty catalog and the messages of every rule
@OptIn(ExperimentalCoroutinesApi::class)
class ShortcutFormEdgeTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val clock = FakeClock(Instant.parse("2026-11-24T07:05:00Z"))
    private val transactions = FakeTransactionRepository()
    private val wallets = FakeWalletRepository(transactions)
    private val categories = FakeCategoryRepository()
    private val quickEntries = FakeQuickEntryRepository()

    /** FR-039: saving without a wallet or category, or opening a deleted shortcut, changes nothing. */
    @Test
    fun givenNoWalletOrUnknownShortcutWhenSavingOrOpeningThenNothingHappens() =
        runTest {
            val viewModel =
                ShortcutFormViewModel(
                    sources = TransactionSources(transactions, wallets, categories, FakeSettingsRepository()),
                    quickEntries = quickEntries,
                    saveQuickEntry = SaveQuickEntryUseCase(quickEntries, categories, wallets),
                    clock = clock,
                )
            viewModel.open(UNKNOWN_ID)
            viewModel.onEvent(ShortcutFormEvent.NameChanged("Kopi"))
            viewModel.onEvent(ShortcutFormEvent.SaveRequested)
            advanceUntilIdle()

            assertThat(viewModel.state.value.isEditing).isFalse()
            assertThat(viewModel.state.value.status.isSaved).isFalse()
            assertThat(quickEntries.observeAll().first()).isEmpty()
        }

    /** FR-039: every rule has its own Indonesian message. */
    @Test
    fun givenEveryRuleWhenAskedThenItHasAMessage() {
        QuickEntryError.entries.forEach { assertThat(it.messageRes() != 0).isTrue() }
    }
}
