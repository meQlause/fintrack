package com.meqlause.fintrack.feature.inbox

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.meqlause.fintrack.core.domain.detection.NotificationAccess
import com.meqlause.fintrack.core.domain.usecase.ConfirmInboxItemUseCase
import com.meqlause.fintrack.core.domain.usecase.DismissAllInboxItemsUseCase
import com.meqlause.fintrack.core.domain.usecase.DismissInboxItemUseCase
import com.meqlause.fintrack.core.domain.usecase.RestoreInboxItemUseCase
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.InboxItem
import com.meqlause.fintrack.core.model.InboxItemId
import com.meqlause.fintrack.core.model.InboxStatus
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.MoneyDirection
import com.meqlause.fintrack.core.model.ParsedAmount
import com.meqlause.fintrack.core.model.RawNotice
import com.meqlause.fintrack.core.model.Resolution
import com.meqlause.fintrack.core.model.Suggestion
import com.meqlause.fintrack.core.testing.coroutine.MainDispatcherRule
import com.meqlause.fintrack.core.testing.repository.FakeCategoryRepository
import com.meqlause.fintrack.core.testing.repository.FakeInboxRepository
import com.meqlause.fintrack.core.testing.repository.FakeMerchantRuleRepository
import com.meqlause.fintrack.core.testing.repository.FakeTransactionRepository
import com.meqlause.fintrack.core.testing.repository.FakeWalletRepository
import com.meqlause.fintrack.core.testing.repository.PassthroughAtomicRunner
import com.meqlause.fintrack.core.testing.time.FakeClock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Rule
import org.junit.Test
import java.time.Instant

// FR-084, FR-087, S-07: the ViewModel lists pending items and resolves them through the use cases
@OptIn(ExperimentalCoroutinesApi::class)
class InboxViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(UnconfinedTestDispatcher())

    private val fixedNow: Instant = Instant.parse("2026-11-23T07:05:00Z")
    private val clock = FakeClock(fixedNow)
    private val inbox = FakeInboxRepository()
    private val transactions = FakeTransactionRepository()
    private val rules = FakeMerchantRuleRepository()
    private val access =
        object : NotificationAccess {
            override fun isGranted(): Boolean = true
        }

    private fun viewModel(): InboxViewModel {
        val atomic = PassthroughAtomicRunner()
        val commands =
            InboxCommands(
                confirmItem = ConfirmInboxItemUseCase(inbox, transactions, rules, atomic, clock),
                dismissItem = DismissInboxItemUseCase(inbox, clock),
                dismissAllItems = DismissAllInboxItemsUseCase(inbox, atomic, clock),
                restoreItem = RestoreInboxItemUseCase(inbox),
            )
        return InboxViewModel(
            inbox = inbox,
            wallets = FakeWalletRepository(transactions),
            categories = FakeCategoryRepository(),
            access = access,
            commands = commands,
            clock = clock,
        )
    }

    @Test
    fun `given a pending item when dismissed then it leaves the list and undo restores it (FR-087)`() =
        runBlocking {
            val itemId = inbox.insert(pending("f1"))
            val model = viewModel()
            assertThat(
                model.state
                    .first { it.isLoaded }
                    .pending.cards.size,
            ).isEqualTo(1)

            model.dismissCard(itemId.value)
            assertThat(
                model.state
                    .first { it.isLoaded }
                    .pending.cards,
            ).isEmpty()
            assertThat(model.state.first { it.isLoaded }.undoItemId).isEqualTo(itemId.value)

            model.undoDismiss()
            assertThat(
                model.state
                    .first { it.isLoaded }
                    .pending.cards.size,
            ).isEqualTo(1)
            assertThat(inbox.findById(itemId)?.resolution?.status).isEqualTo(InboxStatus.PENDING)
        }

    @Test
    fun `given a card without wallet when Catat is pressed then nothing is stored (FR-087)`() =
        runBlocking {
            val itemId = inbox.insert(pending("f2"))
            val model = viewModel()
            model.state.first { it.isLoaded }

            model.confirmCard(itemId.value)

            assertThat(inbox.findById(itemId)?.resolution?.status).isEqualTo(InboxStatus.PENDING)
            assertThat(transactions.observeAll().first()).isEmpty()
        }

    @Test
    fun `given a dismissed undo window when cleared then no undo is pending (S-07)`() =
        runBlocking {
            val itemId = inbox.insert(pending("f3"))
            val model = viewModel()
            model.dismissCard(itemId.value)

            model.clearUndo()

            assertThat(model.state.first { it.isLoaded }.undoItemId).isNull()
            assertThat(
                model.state
                    .first { it.isLoaded }
                    .resolved.size,
            ).isEqualTo(1)
        }

    private fun pending(fingerprint: String): InboxItem =
        InboxItem(
            inboxItemId = InboxItemId(0L),
            notice =
                RawNotice(
                    packageName = "com.bca",
                    parserId = "bca",
                    rawTitle = "Pembayaran",
                    rawText = "Rp25.000",
                    postedAt = fixedNow,
                    fingerprint = fingerprint,
                ),
            parsed = ParsedAmount(Money(25_000L), MoneyDirection.DEBIT, "Indomaret"),
            suggestion = Suggestion(null, null, null),
            resolution = Resolution(InboxStatus.PENDING, null, null),
            audit = Audit(fixedNow, fixedNow),
        )
}
