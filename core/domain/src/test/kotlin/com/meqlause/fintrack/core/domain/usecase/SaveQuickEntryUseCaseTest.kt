package com.meqlause.fintrack.core.domain.usecase

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.meqlause.fintrack.core.domain.quickentry.QuickEntryError
import com.meqlause.fintrack.core.domain.quickentry.QuickEntryResult
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.QuickEntry
import com.meqlause.fintrack.core.model.QuickEntryId
import com.meqlause.fintrack.core.model.QuickTemplate
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.WalletId
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.time.Instant

// FR-039, BR-047: a shortcut is stored with a trimmed title, or not at all when a rule breaks
class SaveQuickEntryUseCaseTest {
    private val repository = InMemoryQuickEntryRepository()
    private val saveQuickEntry = SaveQuickEntryUseCase(repository)

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

    @Test
    fun `rejects a title longer than 24 characters and stores nothing`() =
        runBlocking {
            val result = saveQuickEntry(draft(title = "x".repeat(25)))

            assertThat(result).isEqualTo(QuickEntryResult.Rejected(setOf(QuickEntryError.TITLE_TOO_LONG)))
            assertThat(repository.observeAll().first().size).isEqualTo(0)
        }

    @Test
    fun `rejects a transfer because a shortcut is an expense or income only`() =
        runBlocking {
            val result = saveQuickEntry(draft(title = "Pindah", type = TransactionType.TRANSFER))

            assertThat(result).isEqualTo(QuickEntryResult.Rejected(setOf(QuickEntryError.TYPE_NOT_SHORTCUT)))
        }

    private fun draft(
        title: String,
        type: TransactionType = TransactionType.EXPENSE,
    ): QuickEntry =
        QuickEntry(
            quickEntryId = QuickEntryId(0L),
            template = QuickTemplate(title = title, type = type, amount = Money(25_000L)),
            categoryId = CategoryId(1L),
            walletId = WalletId(1L),
            sortOrder = 0,
            audit = Audit(createdAt = Instant.EPOCH, updatedAt = Instant.EPOCH),
        )
}
