package com.meqlause.fintrack.core.domain.usecase

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.meqlause.fintrack.core.domain.quickentry.QuickEntryError
import com.meqlause.fintrack.core.domain.quickentry.QuickEntryResult
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.Category
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.CategoryKind
import com.meqlause.fintrack.core.model.CategoryProfile
import com.meqlause.fintrack.core.model.CategoryState
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.QuickEntry
import com.meqlause.fintrack.core.model.QuickEntryId
import com.meqlause.fintrack.core.model.QuickTemplate
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.Wallet
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.model.WalletKind
import com.meqlause.fintrack.core.model.WalletProfile
import com.meqlause.fintrack.core.model.WalletStatus
import com.meqlause.fintrack.core.model.WalletTerms
import com.meqlause.fintrack.core.model.WalletType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.time.Instant

// FR-039, BR-047: a shortcut is stored with a trimmed name, or not at all when a rule breaks

/** Save quick entry use case tests. */
class SaveQuickEntryUseCaseTest {
    private val repository = InMemoryQuickEntryRepository()
    private val categories = InMemoryCategoryRepository()
    private val wallets = InMemoryWalletRepository()
    private val saveQuickEntry = SaveQuickEntryUseCase(repository, categories, wallets)

    /** Stores a trimmed shortcut after the existing ones. */
    @Test
    fun `stores a trimmed shortcut after the existing ones`() =
        runBlocking {
            val first = saveQuickEntry(draft(title = "Kopi"))
            val second = saveQuickEntry(draft(title = "  Bensin  "))

            assertThat(first).isInstanceOf(QuickEntryResult.Saved::class)
            assertThat(second).isInstanceOf(QuickEntryResult.Saved::class)
            val stored = repository.observeAll().first()
            assertThat(stored.map { it.template.title }).containsExactly("Kopi", "Bensin")
            assertThat(stored.map { it.sortOrder }).containsExactly(0, 1)
        }

    /** Rejects a title longer than 24 characters and stores nothing. */
    @Test
    fun `rejects a title longer than 24 characters and stores nothing`() =
        runBlocking {
            val result = saveQuickEntry(draft(title = "x".repeat(25)))

            assertThat(result).isEqualTo(QuickEntryResult.Rejected(setOf(QuickEntryError.TITLE_TOO_LONG)))
            assertThat(repository.observeAll().first().size).isEqualTo(0)
        }

    /** Rejects a transfer because a shortcut is an expense or income only. */
    @Test
    fun `rejects a transfer because a shortcut is an expense or income only`() =
        runBlocking {
            val result = saveQuickEntry(draft(title = "Pindah", type = TransactionType.TRANSFER))

            assertThat(result).isEqualTo(QuickEntryResult.Rejected(setOf(QuickEntryError.TYPE_NOT_SHORTCUT)))
        }

    /** FR-039: the optional note is kept trimmed and capped at 120 characters like a transaction note. */
    @Test
    fun `keeps a trimmed note and rejects one longer than 120 characters`() =
        runBlocking {
            val saved = saveQuickEntry(draft(title = "Kopi", note = "  tanpa gula  "))
            val tooLong = saveQuickEntry(draft(title = "Teh", note = "x".repeat(121)))

            assertThat(saved).isInstanceOf(QuickEntryResult.Saved::class)
            assertThat(repository.observeAll().first().map { it.template.note }).containsExactly("tanpa gula")
            assertThat(tooLong).isEqualTo(QuickEntryResult.Rejected(setOf(QuickEntryError.NOTE_TOO_LONG)))
        }

    /** BR-006: an expense shortcut cannot point at an income category. */
    @Test
    fun `rejects a category whose kind does not match the type (BR-006)`() =
        runBlocking {
            val salary = seedCategory(CategoryKind.INCOME)

            val result = saveQuickEntry(draft(title = "Kopi").copy(categoryId = salary))

            assertThat(result).isEqualTo(QuickEntryResult.Rejected(setOf(QuickEntryError.CATEGORY_KIND_MISMATCH)))
        }

    /** FR-039: an income shortcut cannot use a credit wallet, an expense one can. */
    @Test
    fun `rejects an income shortcut on a credit wallet but allows an expense one`() =
        runBlocking {
            val salary = seedCategory(CategoryKind.INCOME)
            val card = seedWallet(WalletType.CREDIT_CARD)

            val income =
                saveQuickEntry(
                    draft(title = "Gaji", type = TransactionType.INCOME).copy(categoryId = salary, walletId = card),
                )
            val expense = saveQuickEntry(draft(title = "Kopi").copy(walletId = card))

            assertThat(income).isEqualTo(QuickEntryResult.Rejected(setOf(QuickEntryError.WALLET_NOT_ALLOWED)))
            assertThat(expense).isInstanceOf(QuickEntryResult.Saved::class)
        }

    /** FR-039, BR-006: an income shortcut on a cash wallet with an income category is stored. */
    @Test
    fun `stores an income shortcut with an income category on a cash wallet`() =
        runBlocking {
            val salary = seedCategory(CategoryKind.INCOME)
            val cash = seedWallet(WalletType.CASH)

            val result =
                saveQuickEntry(
                    draft(title = "Gaji", type = TransactionType.INCOME).copy(categoryId = salary, walletId = cash),
                )

            assertThat(result).isInstanceOf(QuickEntryResult.Saved::class)
        }

    /** FR-039: a category or wallet the stores do not know is left to the foreign keys, not rejected here. */
    @Test
    fun `leaves unknown category and wallet to the foreign keys`() =
        runBlocking {
            val income = saveQuickEntry(draft(title = "Gaji", type = TransactionType.INCOME))
            val transfer = saveQuickEntry(draft(title = "Pindah", type = TransactionType.TRANSFER))

            assertThat(income).isInstanceOf(QuickEntryResult.Saved::class)
            assertThat(transfer).isInstanceOf(QuickEntryResult.Rejected::class)
        }

    /** BR-001: an amount of zero or above 999 billion is rejected. */
    @Test
    fun `rejects an amount outside one to 999 billion (BR-001)`() =
        runBlocking {
            val zero = saveQuickEntry(draft(title = "Kopi").withAmount(0L))
            val huge = saveQuickEntry(draft(title = "Kopi").withAmount(Money.MAX_TRANSACTION_RUPIAH + 1))

            assertThat(zero).isEqualTo(QuickEntryResult.Rejected(setOf(QuickEntryError.AMOUNT_INVALID)))
            assertThat(huge).isEqualTo(QuickEntryResult.Rejected(setOf(QuickEntryError.AMOUNT_INVALID)))
        }

    /** FR-039: a blank name is rejected. */
    @Test
    fun `rejects a blank name`() =
        runBlocking {
            val result = saveQuickEntry(draft(title = "   "))

            assertThat(result).isEqualTo(QuickEntryResult.Rejected(setOf(QuickEntryError.TITLE_REQUIRED)))
        }

    /** FR-039: editing replaces the fields but keeps the position and the creation time. */
    @Test
    fun `editing a shortcut keeps its place and creation time`() =
        runBlocking {
            val first = saveQuickEntry(draft(title = "Kopi")) as QuickEntryResult.Saved
            saveQuickEntry(draft(title = "Bensin"))
            val later = Instant.ofEpochSecond(LATER_SECONDS)

            val edited =
                draft(title = "Kopi susu", note = "oat").copy(
                    quickEntryId = first.quickEntryId,
                    audit = Audit(createdAt = later, updatedAt = later),
                )
            saveQuickEntry(edited)

            val stored = repository.observeAll().first()
            assertThat(stored.map { it.template.title }).containsExactly("Kopi susu", "Bensin")
            assertThat(stored.first().template.note).isEqualTo("oat")
            assertThat(stored.first().audit).isEqualTo(Audit(createdAt = Instant.EPOCH, updatedAt = later))
        }

    private fun draft(
        title: String,
        type: TransactionType = TransactionType.EXPENSE,
        note: String = "",
    ): QuickEntry =
        QuickEntry(
            quickEntryId = QuickEntryId(0L),
            template = QuickTemplate(title = title, type = type, amount = Money(25_000L), note = note),
            categoryId = CategoryId(1L),
            walletId = WalletId(1L),
            sortOrder = 0,
            audit = Audit(createdAt = Instant.EPOCH, updatedAt = Instant.EPOCH),
        )

    private fun QuickEntry.withAmount(rupiah: Long): QuickEntry = copy(template = template.copy(amount = Money(rupiah)))

    private fun seedWallet(type: WalletType): WalletId =
        runBlocking {
            wallets.insert(
                Wallet(
                    walletId = WalletId(0L),
                    kind = WalletKind(type, null),
                    terms = WalletTerms(Money(0L), null),
                    profile = WalletProfile("Dompet", Appearance("wallet", "teal"), 0),
                    status = WalletStatus(),
                    audit = Audit(Instant.EPOCH, Instant.EPOCH),
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
                    audit = Audit(Instant.EPOCH, Instant.EPOCH),
                ),
            )
        }

    private companion object {
        const val LATER_SECONDS = 1_000L
    }
}
