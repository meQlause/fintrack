package com.meqlause.fintrack.core.domain.usecase

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.meqlause.fintrack.core.domain.inbox.ConfirmInboxResult
import com.meqlause.fintrack.core.domain.inbox.InboxChoice
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.InboxItemId
import com.meqlause.fintrack.core.model.InboxStatus
import com.meqlause.fintrack.core.model.MoneyDirection
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.WalletId
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.ZoneOffset

// FR-087, FR-091, BR-011 … BR-013: the less common outcomes of confirming an inbox item

/** FR-087, BR-011: confirm inbox item branch tests. */
class ConfirmInboxBranchesTest {
    private val inbox = InMemoryInboxRepository()
    private val transactions = InMemoryTransactionRepository()
    private val rules = InMemoryMerchantRuleRepository()
    private val useCase =
        ConfirmInboxItemUseCase(
            inbox,
            transactions,
            rules,
            ImmediateAtomicRunner(),
            Clock.fixed(INBOX_NOW, ZoneOffset.UTC),
        )
    private val wallet = WalletId(1L)
    private val category = CategoryId(2L)

    /** BR-011: an unknown item is reported as not pending and nothing is stored. */
    @Test
    fun givenUnknownItemWhenConfirmedThenNotPendingAndNothingStored() {
        runBlocking {
            val result = useCase(InboxChoice(InboxItemId(99L), wallet, category))
            assertThat(result).isEqualTo(ConfirmInboxResult.NotPending)
            assertThat(transactions.stored).hasSize(0)
        }
    }

    /** BR-011: an item that was already confirmed cannot be confirmed again. */
    @Test
    fun givenAlreadyConfirmedItemWhenConfirmedThenNotPending() {
        runBlocking {
            val itemKey = inbox.insert(inboxItemOf("fp-done", status = InboxStatus.CONFIRMED))
            val result = useCase(InboxChoice(itemKey, wallet, category))
            assertThat(result).isEqualTo(ConfirmInboxResult.NotPending)
            assertThat(transactions.stored).hasSize(0)
        }
    }

    /** BR-013: a CREDIT item is stored as an income. */
    @Test
    fun givenCreditItemWhenConfirmedThenIncomeIsStored() {
        runBlocking {
            val itemKey = inbox.insert(inboxItemOf("fp-credit", direction = MoneyDirection.CREDIT))
            useCase(InboxChoice(itemKey, wallet, category))
            assertThat(
                transactions.stored
                    .single()
                    .movement.type,
            ).isEqualTo(TransactionType.INCOME)
        }
    }

    /** FR-087: without a wallet the item is kept pending and reported as missing a wallet. */
    @Test
    fun givenNoWalletWhenConfirmedThenMissingWalletAndItemStaysPending() {
        runBlocking {
            val itemKey = inbox.insert(inboxItemOf("fp-nowallet"))
            assertThat(useCase(InboxChoice(itemKey, null, category))).isEqualTo(ConfirmInboxResult.MissingWallet)
            assertThat(inbox.findById(itemKey)?.resolution?.status).isEqualTo(InboxStatus.PENDING)
        }
    }

    /** FR-091: a transfer needs both a source and a target wallet. */
    @Test
    fun givenPairWithoutSourceOrTargetWhenConfirmedAsTransferThenMissingWallet() {
        runBlocking {
            val debit = inbox.insert(inboxItemOf("fp-out", direction = MoneyDirection.DEBIT))
            val credit = inbox.insert(inboxItemOf("fp-in", direction = MoneyDirection.CREDIT))
            inbox.linkPair(debit, credit)
            val noSource = InboxChoice(debit, null, null, WalletId(2L), asTransfer = true)
            val noTarget = InboxChoice(debit, wallet, null, null, asTransfer = true)
            assertThat(useCase(noSource)).isEqualTo(ConfirmInboxResult.MissingWallet)
            assertThat(useCase(noTarget)).isEqualTo(ConfirmInboxResult.MissingWallet)
            assertThat(transactions.stored).hasSize(0)
        }
    }

    /** FR-091: when the partner is already resolved, the item is confirmed as a single expense instead. */
    @Test
    fun givenResolvedPartnerWhenConfirmedAsTransferThenSingleExpenseIsStored() {
        runBlocking {
            val debit = inbox.insert(inboxItemOf("fp-out-single", direction = MoneyDirection.DEBIT))
            val credit = inbox.insert(inboxItemOf("fp-in-done", MoneyDirection.CREDIT, status = InboxStatus.DISMISSED))
            inbox.linkPair(debit, credit)
            val choice = InboxChoice(debit, wallet, category, WalletId(2L), asTransfer = true)
            val result = useCase(choice)
            assertThat(result).isInstanceOf(ConfirmInboxResult.Confirmed::class)
            assertThat(
                transactions.stored
                    .single()
                    .movement.type,
            ).isEqualTo(TransactionType.EXPENSE)
        }
    }

    /** BR-012: a merchant with an empty normalized name is not remembered. */
    @Test
    fun givenBlankMerchantWhenConfirmedThenNoMerchantRuleIsRemembered() {
        runBlocking {
            val itemKey = inbox.insert(inboxItemOf("fp-blank", merchant = "   "))
            useCase(InboxChoice(itemKey, wallet, category))
            assertThat(rules.observeAll().first()).hasSize(0)
        }
    }

    /** BR-012: confirming the same merchant again raises its hit count instead of adding a rule. */
    @Test
    fun givenKnownMerchantWhenConfirmedAgainThenRuleHitCountGrows() {
        runBlocking {
            val first = inbox.insert(inboxItemOf("fp-kopi-1", merchant = "Kopi"))
            val second = inbox.insert(inboxItemOf("fp-kopi-2", merchant = "Kopi"))
            useCase(InboxChoice(first, wallet, category))
            useCase(InboxChoice(second, wallet, category))
            val rule = rules.observeAll().first().single()
            assertThat(rule.hitCount).isEqualTo(2)
        }
    }
}
