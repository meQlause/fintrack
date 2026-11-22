package com.meqlause.fintrack.core.testing.contract

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import com.meqlause.fintrack.core.testing.data.aCategory
import com.meqlause.fintrack.core.testing.data.aQuickEntry
import com.meqlause.fintrack.core.testing.data.aWallet
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Test

/** DOC-03, FR-039: the contract of QuickEntryRepository, run against the fake and the Local implementation. */
abstract class QuickEntryRepositoryContractTest {
    private val fixture: RepositoryFixture by lazy { createFixture() }

    /** ADR-007: a fresh, empty store for one test. */
    protected abstract fun createFixture(): RepositoryFixture

    /** ADR-007: releases the store after each test. */
    @After
    fun closeFixture() {
        fixture.close()
    }

    /** DOC-02: a fresh store has no shortcuts. */
    @Test
    fun givenNoQuickEntriesWhenObservedThenTheListIsEmpty() =
        runTest {
            assertThat(fixture.quickEntries.observeAll().first()).isEmpty()
        }

    /** FR-039, BR-047: a shortcut keeps its template, category and wallet. */
    @Test
    fun givenShortcutWhenObservedThenEveryFieldIsKept() =
        runTest {
            val categoryId = fixture.categories.insert(aCategory())
            val walletId = fixture.wallets.insert(aWallet())
            val entry = aQuickEntry(categoryId, walletId, sortOrder = 1)

            val quickEntryId = fixture.quickEntries.insert(entry)

            assertThat(
                fixture.quickEntries.observeAll().first(),
            ).isEqualTo(listOf(entry.copy(quickEntryId = quickEntryId)))
        }

    /** DOC-02: shortcuts come back in the user's chosen order. */
    @Test
    fun givenShortcutsInsertedOutOfOrderWhenObservedThenSortOrderDecides() =
        runTest {
            val categoryId = fixture.categories.insert(aCategory())
            val walletId = fixture.wallets.insert(aWallet())
            fixture.quickEntries.insert(aQuickEntry(categoryId, walletId, sortOrder = 2))
            fixture.quickEntries.insert(aQuickEntry(categoryId, walletId, sortOrder = 1))

            val sortOrders =
                fixture.quickEntries
                    .observeAll()
                    .first()
                    .map { it.sortOrder }

            assertThat(sortOrders).isEqualTo(listOf(1, 2))
        }
}
