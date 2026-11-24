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

    /** FR-039: updating replaces the stored fields, including the note, and leaves the others alone. */
    @Test
    fun givenShortcutWhenUpdatedThenOnlyThatRowChanges() =
        runTest {
            val categoryId = fixture.categories.insert(aCategory())
            val walletId = fixture.wallets.insert(aWallet())
            val keptId = fixture.quickEntries.insert(aQuickEntry(categoryId, walletId, sortOrder = 0))
            val editedId = fixture.quickEntries.insert(aQuickEntry(categoryId, walletId, sortOrder = 1))
            val template = aQuickEntry(categoryId, walletId).template.copy(title = "Teh", note = "manis")

            fixture.quickEntries.update(
                aQuickEntry(categoryId, walletId, sortOrder = 1).copy(quickEntryId = editedId, template = template),
            )

            val stored = fixture.quickEntries.observeAll().first()
            assertThat(stored.map { it.quickEntryId }).isEqualTo(listOf(keptId, editedId))
            assertThat(stored.map { it.template.title }).isEqualTo(listOf("Kopi", "Teh"))
            assertThat(stored.map { it.template.note }).isEqualTo(listOf("tanpa gula", "manis"))
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
