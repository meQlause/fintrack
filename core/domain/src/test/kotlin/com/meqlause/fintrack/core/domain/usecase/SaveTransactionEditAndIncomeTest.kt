package com.meqlause.fintrack.core.domain.usecase

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNull
import com.meqlause.fintrack.core.domain.transaction.DraftRouting
import com.meqlause.fintrack.core.domain.transaction.SaveTransactionResult
import com.meqlause.fintrack.core.domain.transaction.TransactionDraft
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
import com.meqlause.fintrack.core.model.Origin
import com.meqlause.fintrack.core.model.Routing
import com.meqlause.fintrack.core.model.Timing
import com.meqlause.fintrack.core.model.Transaction
import com.meqlause.fintrack.core.model.TransactionId
import com.meqlause.fintrack.core.model.TransactionSource
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

// FR-030 … FR-034, BR-003: income is stored as income, a transfer keeps no category, and an edit keeps its origin

/** FR-030, FR-034: save transaction use case tests for income, transfers and edits. */
class SaveTransactionEditAndIncomeTest {
    private val currentTime = Instant.parse("2026-11-23T10:00:00Z")
    private val created = Instant.parse("2026-11-01T08:00:00Z")
    private val wallets = InMemoryWalletRepository()
    private val categories = InMemoryCategoryRepository()
    private val transactions = InMemoryTransactionRepository()
    private val useCase =
        SaveTransactionUseCase(transactions, wallets, categories, Clock.fixed(currentTime, ZoneOffset.UTC))

    /** FR-030: an income with an income category is stored as an INCOME. */
    @Test
    fun givenIncomeWithIncomeCategoryWhenSavedThenIncomeIsStored() {
        runBlocking {
            val bank = seedWallet()
            val salary = seedCategory(CategoryKind.INCOME)
            val draft = draftOf(TransactionType.INCOME, 5_000_000L, DraftRouting(bank, null, salary))
            assertThat(useCase(draft)).isInstanceOf(SaveTransactionResult.Saved::class)
            assertThat(
                transactions.stored
                    .single()
                    .movement.type,
            ).isEqualTo(TransactionType.INCOME)
        }
    }

    /** BR-003: a transfer keeps its target wallet and never stores a category. */
    @Test
    fun givenTransferWithCategoryWhenSavedThenCategoryIsDropped() {
        runBlocking {
            val source = seedWallet()
            val target = seedWallet()
            val food = seedCategory(CategoryKind.EXPENSE)
            val draft = draftOf(TransactionType.TRANSFER, 10_000L, DraftRouting(source, target, food))
            useCase(draft)
            val routing = transactions.stored.single().routing
            assertThat(routing.targetWalletId).isEqualTo(target)
            assertThat(routing.categoryId).isNull()
        }
    }

    /** FR-034: editing keeps the origin and the creation time, updates the amount and reuses the id. */
    @Test
    fun givenStoredTransactionWhenEditedThenOriginAndCreatedAtAreKept() {
        runBlocking {
            val bank = seedWallet()
            val food = seedCategory(CategoryKind.EXPENSE)
            val stored = storedRow(bank, food)
            val storedId = transactions.insert(stored)
            val routing = DraftRouting(bank, null, food)
            val edited = draftOf(TransactionType.EXPENSE, 30_000L, routing, stored.copy(transactionId = storedId))
            val result = useCase(edited) as SaveTransactionResult.Saved
            val saved = transactions.stored.single()
            assertThat(result.transactionId).isEqualTo(storedId)
            assertThat(transactions.stored.map { it.transactionId }).containsExactly(storedId)
            assertThat(saved.movement.amount).isEqualTo(Money(30_000L))
            assertThat(saved.origin.source).isEqualTo(TransactionSource.NOTIFICATION)
            assertThat(saved.timing.audit.createdAt).isEqualTo(created)
            assertThat(saved.timing.audit.updatedAt).isEqualTo(currentTime)
        }
    }

    /** FR-030: a new transaction gets a fresh id and a manual origin. */
    @Test
    fun givenNewDraftWhenSavedThenOriginIsManual() {
        runBlocking {
            val bank = seedWallet()
            val food = seedCategory(CategoryKind.EXPENSE)
            useCase(draftOf(TransactionType.EXPENSE, 12_000L, DraftRouting(bank, null, food)))
            assertThat(transactions.stored).hasSize(1)
            assertThat(
                transactions.stored
                    .single()
                    .origin.source,
            ).isEqualTo(TransactionSource.MANUAL)
        }
    }

    private fun draftOf(
        type: TransactionType,
        amount: Long,
        routing: DraftRouting,
        existing: Transaction? = null,
    ): TransactionDraft =
        TransactionDraft(
            existing = existing,
            movement = Movement(type, Money(amount)),
            routing = routing,
            memo = Memo(),
            occurredAt = currentTime,
        )

    private fun storedRow(
        wallet: WalletId,
        category: CategoryId,
    ): Transaction =
        Transaction(
            transactionId = TransactionId(0L),
            movement = Movement(TransactionType.EXPENSE, Money(20_000L)),
            routing = Routing(wallet, null, category, null),
            memo = Memo(),
            origin = Origin(TransactionSource.NOTIFICATION, null),
            timing = Timing(currentTime, Audit(created, created)),
        )

    private fun seedWallet(): WalletId =
        runBlocking {
            wallets.insert(
                Wallet(
                    walletId = WalletId(0L),
                    kind = WalletKind(WalletType.BANK, null),
                    terms = WalletTerms(Money(0L), null),
                    profile = WalletProfile("Bank", Appearance("bank", "teal"), 0),
                    status = WalletStatus(),
                    audit = Audit(currentTime, currentTime),
                ),
            )
        }

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
