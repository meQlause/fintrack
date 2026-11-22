package com.meqlause.fintrack.core.testing.contract

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import com.meqlause.fintrack.core.model.InboxStatus
import com.meqlause.fintrack.core.model.Resolution
import com.meqlause.fintrack.core.model.Suggestion
import com.meqlause.fintrack.core.testing.data.aCategory
import com.meqlause.fintrack.core.testing.data.aWallet
import com.meqlause.fintrack.core.testing.data.anInboxItem
import com.meqlause.fintrack.core.testing.data.anInstant
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Test

/** DOC-03, FR-090: the contract of InboxRepository, run against the fake and the Local implementation. */
abstract class InboxRepositoryContractTest {
    private val fixture: RepositoryFixture by lazy { createFixture() }

    /** ADR-007: a fresh, empty store for one test. */
    protected abstract fun createFixture(): RepositoryFixture

    /** ADR-007: releases the store after each test. */
    @After
    fun closeFixture() {
        fixture.close()
    }

    /** DOC-02: a fresh store has no inbox items. */
    @Test
    fun givenNoInboxItemsWhenObservedThenTheListIsEmpty() =
        runTest {
            assertThat(fixture.inbox.observeAll().first()).isEmpty()
        }

    /** FR-090, FR-091: suggestions and the review state of an item are kept. */
    @Test
    fun givenItemWithSuggestionsAndResolutionWhenObservedThenEveryFieldIsKept() =
        runTest {
            val walletId = fixture.wallets.insert(aWallet())
            val categoryId = fixture.categories.insert(aCategory())
            val item =
                anInboxItem("fp-inbox-1").let {
                    it.copy(
                        suggestion = Suggestion(walletId, categoryId, relatedInboxItemId = null),
                        resolution =
                            Resolution(
                                InboxStatus.DISMISSED,
                                transactionId = null,
                                resolvedAt = anInstant(offsetMillis = 9_000L),
                            ),
                    )
                }

            val inboxItemId = fixture.inbox.insert(item)

            assertThat(fixture.inbox.observeAll().first()).isEqualTo(listOf(item.copy(inboxItemId = inboxItemId)))
        }

    /** FR-092: a fingerprint is the dedup key, so a second item with the same fingerprint is rejected. */
    @Test
    fun givenItemWithAKnownFingerprintWhenInsertedAgainThenItIsRejected() =
        runTest {
            fixture.inbox.insert(anInboxItem("fp-inbox-2"))

            val failure = runCatching { fixture.inbox.insert(anInboxItem("fp-inbox-2")) }.exceptionOrNull()

            assertThat(failure).isNotNull()
            assertThat(fixture.inbox.observeAll().first()).hasSize(1)
        }

    /** DOC-02: items come back oldest notification first. */
    @Test
    fun givenItemsInsertedOutOfOrderWhenObservedThenPostedAtDecides() =
        runTest {
            fixture.inbox.insert(anInboxItem("fp-late", postedAt = anInstant(offsetMillis = 2_000L)))
            fixture.inbox.insert(anInboxItem("fp-early", postedAt = anInstant(offsetMillis = 1_000L)))

            val fingerprints =
                fixture.inbox
                    .observeAll()
                    .first()
                    .map { it.notice.fingerprint }

            assertThat(fingerprints).isEqualTo(listOf("fp-early", "fp-late"))
        }
}
