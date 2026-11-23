package com.meqlause.fintrack.core.domain.usecase

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.meqlause.fintrack.core.domain.inbox.ConfirmInboxResult
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.InboxItemId
import com.meqlause.fintrack.core.model.Suggestion
import com.meqlause.fintrack.core.model.WalletId
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.ZoneOffset

// FR-089: the heads-up prompt confirms an item with the wallet and category it was suggested

/** FR-089: confirm suggested inbox item use case tests. */
class ConfirmSuggestedInboxItemUseCaseTest {
    private val inbox = InMemoryInboxRepository()
    private val transactions = InMemoryTransactionRepository()
    private val confirm =
        ConfirmInboxItemUseCase(
            inbox,
            transactions,
            InMemoryMerchantRuleRepository(),
            ImmediateAtomicRunner(),
            Clock.fixed(INBOX_NOW, ZoneOffset.UTC),
        )
    private val useCase = ConfirmSuggestedInboxItemUseCase(inbox, confirm)

    /** FR-089: an unknown item is reported as not pending. */
    @Test
    fun givenUnknownItemWhenConfirmedThenNotPending() {
        runBlocking {
            assertThat(useCase(InboxItemId(42L))).isEqualTo(ConfirmInboxResult.NotPending)
        }
    }

    /** FR-089: a suggested wallet and category are enough to store the transaction. */
    @Test
    fun givenSuggestionWithWalletAndCategoryWhenConfirmedThenTransactionIsStored() {
        runBlocking {
            val suggestion = Suggestion(WalletId(1L), CategoryId(2L), null)
            val itemKey = inbox.insert(inboxItemOf("fp-suggested").copy(suggestion = suggestion))
            val result = useCase(itemKey)
            assertThat(result).isInstanceOf(ConfirmInboxResult.Confirmed::class)
            assertThat(transactions.stored).hasSize(1)
        }
    }

    /** FR-089: without a suggested wallet the Konfirmasi action stores nothing. */
    @Test
    fun givenSuggestionWithoutWalletWhenConfirmedThenMissingWalletAndNothingStored() {
        runBlocking {
            val suggestion = Suggestion(null, CategoryId(2L), null)
            val itemKey = inbox.insert(inboxItemOf("fp-nowallet-suggest").copy(suggestion = suggestion))
            assertThat(useCase(itemKey)).isEqualTo(ConfirmInboxResult.MissingWallet)
            assertThat(transactions.stored).hasSize(0)
        }
    }
}
