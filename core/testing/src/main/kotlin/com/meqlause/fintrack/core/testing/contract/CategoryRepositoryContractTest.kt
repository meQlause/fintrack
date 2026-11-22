package com.meqlause.fintrack.core.testing.contract

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isGreaterThan
import com.meqlause.fintrack.core.model.CategoryKind
import com.meqlause.fintrack.core.model.CategoryState
import com.meqlause.fintrack.core.testing.data.aCategory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Test

/** DOC-03, FR-004: the contract of CategoryRepository, run against the fake and the Local implementation. */
abstract class CategoryRepositoryContractTest {
    private val fixture: RepositoryFixture by lazy { createFixture() }

    /** ADR-007: a fresh, empty store for one test. */
    protected abstract fun createFixture(): RepositoryFixture

    /** ADR-007: releases the store after each test. */
    @After
    fun closeFixture() {
        fixture.close()
    }

    /** FR-004: a fresh store has no categories. */
    @Test
    fun givenNoCategoriesWhenObservedThenTheListIsEmpty() =
        runTest {
            assertThat(fixture.categories.observeAll().first()).isEmpty()
        }

    /** FR-004: an inserted category keeps its kind, state and appearance. */
    @Test
    fun givenInsertedArchivedIncomeCategoryWhenObservedThenEveryFieldIsKept() =
        runTest {
            val category =
                aCategory(name = "Gaji", kind = CategoryKind.INCOME, sortOrder = 4).let {
                    it.copy(state = CategoryState(isDefault = false, isArchived = true))
                }

            val categoryId = fixture.categories.insert(category)

            assertThat(
                fixture.categories.observeAll().first(),
            ).isEqualTo(listOf(category.copy(categoryId = categoryId)))
        }

    /** FR-004: categories come back in manual order. */
    @Test
    fun givenCategoriesInsertedOutOfOrderWhenObservedThenManualOrderDecides() =
        runTest {
            fixture.categories.insert(aCategory(name = "Hiburan", sortOrder = 2))
            fixture.categories.insert(aCategory(name = "Makanan", sortOrder = 1))

            val names =
                fixture.categories
                    .observeAll()
                    .first()
                    .map { it.profile.name }

            assertThat(names).isEqualTo(listOf("Makanan", "Hiburan"))
        }

    /** BR-008: a deleted category disappears and its id is never handed out again. */
    @Test
    fun givenCategoryDeletedWhenObservedThenItIsGoneAndItsIdIsNotReused() =
        runTest {
            val deleted = aCategory(name = "Lama")
            val oldId = fixture.categories.insert(deleted)
            fixture.categories.delete(deleted.copy(categoryId = oldId))

            val newId = fixture.categories.insert(aCategory(name = "Baru"))

            assertThat(
                fixture.categories
                    .observeAll()
                    .first()
                    .map { it.profile.name },
            ).isEqualTo(listOf("Baru"))
            assertThat(newId.value).isGreaterThan(oldId.value)
        }
}
