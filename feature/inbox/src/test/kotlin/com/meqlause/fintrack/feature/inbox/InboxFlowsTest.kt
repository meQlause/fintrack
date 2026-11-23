package com.meqlause.fintrack.feature.inbox

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.meqlause.fintrack.core.model.CategoryKind
import com.meqlause.fintrack.core.model.InboxStatus
import com.meqlause.fintrack.core.model.MoneyDirection
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.testing.coroutine.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Rule
import org.junit.Test
import java.time.Duration

// FR-087, FR-091, S-07: pairs, edits and resolved rows of the Kotak Masuk
@OptIn(ExperimentalCoroutinesApi::class)
class InboxFlowsTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(UnconfinedTestDispatcher())

    private val fixtures = InboxTestFixtures()

    /** FR-091: a debit and a credit linked to each other show as one pair and become one transfer. */
    @Test
    fun `given a linked debit and credit when the pair is confirmed then one transfer is stored (FR-091)`() =
        runBlocking {
            val bank = fixtures.wallet("BCA")
            val cash = fixtures.wallet("Tunai")
            val (debit, _) = fixtures.pairOf(bank, cash)
            val model = fixtures.viewModel()
            val state = model.state.first { it.isLoaded }
            assertThat(state.pending.pairs).hasSize(1)
            assertThat(state.pending.cards).isEmpty()
            assertThat(
                state.pending.pairs
                    .single()
                    .from.name,
            ).isEqualTo("BCA")
            model.confirmPair(debit)
            val stored = fixtures.transactions.observeAll().first()
            assertThat(stored).hasSize(1)
            assertThat(stored.single().movement.type).isEqualTo(TransactionType.TRANSFER)
        }

    /** FR-091: splitting a pair shows its two items as two cards; unknown ids do nothing. */
    @Test
    fun `given a pair when it is split then two cards show and unknown ids are ignored (FR-091)`() =
        runBlocking {
            val (debit, _) = fixtures.pairOf(fixtures.wallet("BCA"), fixtures.wallet("Tunai"))
            val model = fixtures.viewModel()
            model.state.first { it.isLoaded }
            model.splitPair(999L)
            model.confirmPair(999L)
            model.confirmCard(999L)
            model.undoDismiss()
            assertThat(
                model.state
                    .first { it.isLoaded }
                    .pending.pairs,
            ).hasSize(1)
            model.splitPair(debit)
            val split = model.state.first { it.pending.pairs.isEmpty() }
            assertThat(split.pending.cards).hasSize(2)
            assertThat(split.pending.isEmpty).isFalse()
        }

    /** FR-087: the chips change a card's wallet and category, and Catat stores the card as shown. */
    @Test
    fun `given a card when its chips are chosen then Catat stores an expense with them (FR-087)`() =
        runBlocking {
            val bank = fixtures.wallet("BCA")
            val food = fixtures.category("Makan", CategoryKind.EXPENSE, order = 0)
            val itemId = fixtures.inbox.insert(fixtures.item(MoneyDirection.DEBIT)).value
            val model = fixtures.viewModel()
            model.state.first { it.isLoaded }
            model.chooseWallet(itemId, bank.value)
            model.chooseCategory(itemId, food.value)
            val card =
                model.state
                    .first {
                        it.pending.cards
                            .firstOrNull()
                            ?.wallet
                            ?.slotId != null
                    }.pending.cards
                    .single()
            assertThat(card.category.name).isEqualTo("Makan")
            model.confirmCard(itemId)
            assertThat(fixtures.transactions.observeAll().first()).hasSize(1)
        }

    /** S-07: dismissing everything resolves all pending items; resolved rows are newest first within a week. */
    @Test
    fun `given pending and resolved items when listed then rows, labels and window follow the rules (S-07)`() =
        runBlocking {
            val recent = fixtures.stamp.minus(Duration.ofDays(1))
            val ancient = fixtures.stamp.minus(Duration.ofDays(30))
            fixtures.insertResolved(MoneyDirection.CREDIT, "", InboxStatus.CONFIRMED, recent)
            fixtures.insertResolved(MoneyDirection.DEBIT, "Kopi", InboxStatus.DISMISSED, recent)
            fixtures.insertResolved(MoneyDirection.DEBIT, "Lama", InboxStatus.DISMISSED, ancient)
            fixtures.inbox.insert(fixtures.gojekItem())
            fixtures.granted = false
            val model = fixtures.viewModel()
            val state = model.state.first { it.isLoaded }
            assertThat(state.hasAccess).isFalse()
            assertThat(state.resolved).hasSize(2)
            assertThat(state.resolved.first { it.label == "BCA" }.isConfirmed).isTrue()
            assertThat(state.resolved.first { it.label == "BCA" }.type).isEqualTo(TransactionType.INCOME)
            model.dismissEverything()
            assertThat(model.state.first { it.pending.isEmpty }.resolved).hasSize(3)
        }
}
