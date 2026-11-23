package com.meqlause.fintrack.core.domain.usecase

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import com.meqlause.fintrack.core.model.InboxItemId
import com.meqlause.fintrack.core.model.InboxStatus
import com.meqlause.fintrack.core.model.MoneyDirection
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.ZoneOffset

// FR-087, FR-092, S-07: dismissing, dismissing all and restoring inbox items never create transactions

/** FR-087, S-07: inbox dismiss, dismiss all and restore use case tests. */
class InboxDismissUseCasesTest {
    private val clock = Clock.fixed(INBOX_NOW, ZoneOffset.UTC)
    private val inbox = InMemoryInboxRepository()
    private val dismiss = DismissInboxItemUseCase(inbox, clock)
    private val dismissAll = DismissAllInboxItemsUseCase(inbox, ImmediateAtomicRunner(), clock)
    private val restore = RestoreInboxItemUseCase(inbox)

    /** FR-087: a pending item is dismissed and keeps no transaction. */
    @Test
    fun givenPendingItemWhenDismissedThenStatusIsDismissed() {
        runBlocking {
            val itemKey = inbox.insert(inboxItemOf("fp-dismiss"))
            dismiss(itemKey)
            assertThat(inbox.findById(itemKey)?.resolution?.status).isEqualTo(InboxStatus.DISMISSED)
        }
    }

    /** FR-087: a confirmed item cannot be dismissed. */
    @Test
    fun givenConfirmedItemWhenDismissedThenStatusIsUnchanged() {
        runBlocking {
            val itemKey = inbox.insert(inboxItemOf("fp-confirmed", status = InboxStatus.CONFIRMED))
            dismiss(itemKey)
            assertThat(inbox.findById(itemKey)?.resolution?.status).isEqualTo(InboxStatus.CONFIRMED)
        }
    }

    /** FR-087: dismissing an unknown item does nothing. */
    @Test
    fun givenUnknownItemWhenDismissedThenNothingChanges() {
        runBlocking {
            dismiss(InboxItemId(77L))
            assertThat(inbox.observeAll().first()).hasSize(0)
        }
    }

    /** FR-087, S-07: dismiss all only touches pending items. */
    @Test
    fun givenPendingAndConfirmedItemsWhenDismissAllThenOnlyPendingAreDismissed() {
        runBlocking {
            val pending = inbox.insert(inboxItemOf("fp-all-1", MoneyDirection.DEBIT))
            val confirmed = inbox.insert(inboxItemOf("fp-all-2", status = InboxStatus.CONFIRMED))
            dismissAll()
            assertThat(inbox.findById(pending)?.resolution?.status).isEqualTo(InboxStatus.DISMISSED)
            assertThat(inbox.findById(confirmed)?.resolution?.status).isEqualTo(InboxStatus.CONFIRMED)
        }
    }

    /** FR-087: restoring a dismissed item makes it pending again. */
    @Test
    fun givenDismissedItemWhenRestoredThenItIsPendingAgain() {
        runBlocking {
            val itemKey = inbox.insert(inboxItemOf("fp-restore", status = InboxStatus.DISMISSED))
            restore(itemKey)
            assertThat(inbox.findById(itemKey)?.resolution?.status).isEqualTo(InboxStatus.PENDING)
        }
    }

    /** FR-087: restoring an item that is not dismissed leaves it as it is. */
    @Test
    fun givenPendingItemWhenRestoredThenStatusIsUnchanged() {
        runBlocking {
            val itemKey = inbox.insert(inboxItemOf("fp-restore-noop"))
            restore(itemKey)
            restore(InboxItemId(55L))
            assertThat(inbox.findById(itemKey)?.resolution?.status).isEqualTo(InboxStatus.PENDING)
        }
    }
}
