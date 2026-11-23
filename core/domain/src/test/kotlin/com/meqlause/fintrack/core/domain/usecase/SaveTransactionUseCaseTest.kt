package com.meqlause.fintrack.core.domain.usecase

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.meqlause.fintrack.core.domain.transaction.DraftRouting
import com.meqlause.fintrack.core.domain.transaction.SaveTransactionResult
import com.meqlause.fintrack.core.domain.transaction.TransactionDraft
import com.meqlause.fintrack.core.domain.transaction.TransactionError
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.Category
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.CategoryKind
import com.meqlause.fintrack.core.model.CategoryProfile
import com.meqlause.fintrack.core.model.CategoryState
import com.meqlause.fintrack.core.model.Memo
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Movement
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.Wallet
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.model.WalletKind
import com.meqlause.fintrack.core.model.WalletProfile
import com.meqlause.fintrack.core.model.WalletStatus
import com.meqlause.fintrack.core.model.WalletTerms
import com.meqlause.fintrack.core.model.WalletType
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

/** FR-030..FR-032, BR-001..BR-003, BR-006, BR-009, BR-039: the validation rules behind the add sheet. */
class SaveTransactionUseCaseTest {
    private val currentTime = Instant.parse("2026-11-23T10:00:00Z")
    private val clock = Clock.fixed(currentTime, ZoneOffset.UTC)
    private val wallets = InMemoryWalletRepository()
    private val categories = InMemoryCategoryRepository()
    private val transactions = InMemoryTransactionRepository()
    private val useCase = SaveTransactionUseCase(transactions, wallets, categories, clock)

    /** BR-001: an amount of zero is rejected as required. */
    @Test
    fun `given zero amount when saving then amount required is reported (BR-001)`() =
        runBlocking {
            val draft = expense(amount = 0L, categoryId = foodId())
            assertThat(
                useCase(draft),
            ).isEqualTo(SaveTransactionResult.Rejected(setOf(TransactionError.AMOUNT_REQUIRED)))
        }

    /** BR-001: an amount above 999,999,999,999 is rejected as too large. */
    @Test
    fun `given amount above the bound when saving then amount too large is reported (BR-001)`() =
        runBlocking {
            val draft = expense(amount = 1_000_000_000_000L, categoryId = foodId())
            assertThat(
                useCase(draft),
            ).isEqualTo(SaveTransactionResult.Rejected(setOf(TransactionError.AMOUNT_TOO_LARGE)))
        }

    /** BR-002: an expense without a category is rejected. */
    @Test
    fun `given expense without category when saving then category required is reported (BR-002)`() =
        runBlocking {
            val draft = expense(amount = 25_000L, categoryId = null)
            assertThat(
                useCase(draft),
            ).isEqualTo(SaveTransactionResult.Rejected(setOf(TransactionError.CATEGORY_REQUIRED)))
        }

    /** BR-006: an income category cannot classify an expense. */
    @Test
    fun `given income category on expense when saving then kind mismatch is reported (BR-006)`() =
        runBlocking {
            val draft = expense(amount = 25_000L, categoryId = salaryId())
            assertThat(
                useCase(draft),
            ).isEqualTo(SaveTransactionResult.Rejected(setOf(TransactionError.CATEGORY_KIND_MISMATCH)))
        }

    /** BR-003: a transfer needs source different from target. */
    @Test
    fun `given transfer with the same source and target when saving then same wallet is reported (BR-003)`() =
        runBlocking {
            val cash = seedWallet(WalletType.CASH)
            val draft = transfer(source = cash, target = cash)
            assertThat(useCase(draft)).isEqualTo(SaveTransactionResult.Rejected(setOf(TransactionError.SAME_WALLET)))
        }

    /** BR-039: a credit wallet is never allowed as the source of a transfer. */
    @Test
    fun `given credit wallet as transfer source when saving then it is rejected (BR-039)`() =
        runBlocking {
            val card = seedWallet(WalletType.CREDIT_CARD)
            val bank = seedWallet(WalletType.BANK)
            val draft = transfer(source = card, target = bank)
            assertThat(useCase(draft)).isEqualTo(
                SaveTransactionResult.Rejected(setOf(TransactionError.CREDIT_SOURCE_NOT_ALLOWED)),
            )
        }

    /** BR-039: a credit wallet is allowed as the target of a transfer (paying the bill). */
    @Test
    fun `given credit wallet as transfer target when saving then it is stored (BR-039)`() =
        runBlocking {
            val bank = seedWallet(WalletType.BANK)
            val card = seedWallet(WalletType.CREDIT_CARD)
            val result = useCase(transfer(source = bank, target = card))
            assertThat(result).isInstanceOf(SaveTransactionResult.Saved::class)
            assertThat(transactions.stored.map { it.movement.type }).containsExactly(TransactionType.TRANSFER)
        }

    /** BR-009: a date more than one day ahead is rejected. */
    @Test
    fun `given date two days ahead when saving then date too far is reported (BR-009)`() =
        runBlocking {
            val draft =
                expense(amount = 25_000L, categoryId = foodId()).copy(occurredAt = currentTime.plusSeconds(172_800L))
            assertThat(useCase(draft)).isEqualTo(SaveTransactionResult.Rejected(setOf(TransactionError.DATE_TOO_FAR)))
        }

    /** FR-030: an amount and a category are enough; the valid draft is stored once. */
    @Test
    fun `given amount and category when saving then the expense is stored (FR-030)`() =
        runBlocking {
            val wallet = seedWallet(WalletType.CASH)
            val result = useCase(expense(amount = 25_000L, categoryId = foodId(), walletId = wallet))
            assertThat(result).isInstanceOf(SaveTransactionResult.Saved::class)
            assertThat(transactions.stored.map { it.movement.amount }).containsExactly(Money(25_000L))
        }

    private fun expense(
        amount: Long,
        categoryId: CategoryId?,
        walletId: WalletId? = null,
    ): TransactionDraft =
        TransactionDraft(
            existing = null,
            movement = Movement(TransactionType.EXPENSE, Money(amount)),
            routing = DraftRouting(walletId ?: seedWallet(WalletType.CASH), null, categoryId),
            memo = Memo(),
            occurredAt = currentTime,
        )

    private fun transfer(
        source: WalletId,
        target: WalletId,
    ): TransactionDraft =
        TransactionDraft(
            existing = null,
            movement = Movement(TransactionType.TRANSFER, Money(10_000L)),
            routing = DraftRouting(source, target, null),
            memo = Memo(),
            occurredAt = currentTime,
        )

    private fun seedWallet(type: WalletType): WalletId =
        runBlocking {
            wallets.insert(
                Wallet(
                    walletId = WalletId(0L),
                    kind = WalletKind(type, null),
                    terms = WalletTerms(Money(0L), null),
                    profile = WalletProfile("Dompet", Appearance("wallet", "teal"), 0),
                    status = WalletStatus(),
                    audit = Audit(currentTime, currentTime),
                ),
            )
        }

    private fun foodId(): CategoryId = seedCategory(CategoryKind.EXPENSE)

    private fun salaryId(): CategoryId = seedCategory(CategoryKind.INCOME)

    private fun seedCategory(kind: CategoryKind): CategoryId =
        runBlocking {
            categories.insert(
                Category(
                    categoryId = CategoryId(0L),
                    profile = CategoryProfile("Kategori", kind, Appearance("tag", "teal"), 0),
                    state = CategoryState(isDefault = true, isArchived = false),
                    audit = Audit(currentTime, currentTime),
                ),
            )
        }
}
