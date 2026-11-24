package com.meqlause.fintrack.core.domain.usecase

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.meqlause.fintrack.core.domain.category.CategoryError
import com.meqlause.fintrack.core.domain.category.CategoryResult
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.CategoryKind
import com.meqlause.fintrack.core.model.CategoryProfile
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

// FR-021, DOC-02: a new category is stored trimmed and appended, or not at all when a name rule breaks

/** Create category use case tests. */
class CreateCategoryUseCaseTest {
    private val repository = InMemoryCategoryRepository()
    private val createCategory =
        CreateCategoryUseCase(repository, Clock.fixed(Instant.parse("2026-11-24T02:00:00Z"), ZoneOffset.UTC))

    /** Stores a trimmed, non-default category after the existing ones. */
    @Test
    fun `stores a trimmed category after the existing ones`() =
        runBlocking {
            createCategory(draft("Kopi"))
            val result = createCategory(draft("  Bensin  "))

            assertThat(result).isInstanceOf(CategoryResult.Created::class)
            val stored = repository.observeAll().first()
            assertThat(stored.map { it.profile.name }).containsExactly("Kopi", "Bensin")
            assertThat(stored.map { it.profile.sortOrder }).containsExactly(0, 1)
            assertThat(stored.last().state.isDefault).isEqualTo(false)
        }

    /** Rejects a blank name and a name longer than 24 characters. */
    @Test
    fun `rejects a blank and a too long name`() =
        runBlocking {
            val blank = createCategory(draft("   "))
            val long = createCategory(draft("x".repeat(25)))

            assertThat(blank).isEqualTo(CategoryResult.Rejected(setOf(CategoryError.NAME_REQUIRED)))
            assertThat(long).isEqualTo(CategoryResult.Rejected(setOf(CategoryError.NAME_TOO_LONG)))
            assertThat(repository.observeAll().first().size).isEqualTo(0)
        }

    /** Rejects a name already used in the same kind, ignoring case, but allows it in the other kind. */
    @Test
    fun `rejects a duplicate name of the same kind only`() =
        runBlocking {
            createCategory(draft("Kopi"))

            val same = createCategory(draft("kopi"))
            val other = createCategory(draft("Kopi", CategoryKind.INCOME))

            assertThat(same).isEqualTo(CategoryResult.Rejected(setOf(CategoryError.NAME_TAKEN)))
            assertThat(other).isInstanceOf(CategoryResult.Created::class)
        }

    private fun draft(
        name: String,
        kind: CategoryKind = CategoryKind.EXPENSE,
    ) = CategoryProfile(name = name, kind = kind, appearance = Appearance("other", "gray"), sortOrder = 0)
}
