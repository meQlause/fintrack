package com.meqlause.fintrack.core.testing.contract

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import com.meqlause.fintrack.core.testing.data.aBudget
import com.meqlause.fintrack.core.testing.data.aCategory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Test

/** DOC-03, FR-074: the contract of BudgetRepository, run against the fake and the Local implementation. */
abstract class BudgetRepositoryContractTest {
    private val fixture: RepositoryFixture by lazy { createFixture() }

    /** ADR-007: a fresh, empty store for one test. */
    protected abstract fun createFixture(): RepositoryFixture

    /** ADR-007: releases the store after each test. */
    @After
    fun closeFixture() {
        fixture.close()
    }

    /** DOC-02: a fresh store has no budgets. */
    @Test
    fun givenNoBudgetsWhenObservedThenTheListIsEmpty() =
        runTest {
            assertThat(fixture.budgets.observeAll().first()).isEmpty()
        }

    /** FR-074: a category budget comes back unchanged except for its generated id. */
    @Test
    fun givenCategoryBudgetWhenObservedThenEveryFieldIsKept() =
        runTest {
            val categoryId = fixture.categories.insert(aCategory())
            val budget = aBudget(categoryId)

            val budgetId = fixture.budgets.insert(budget)

            assertThat(fixture.budgets.observeAll().first()).isEqualTo(listOf(budget.copy(budgetId = budgetId)))
        }

    /** FR-074: several overall budgets (no category) are allowed, because a unique index ignores nulls. */
    @Test
    fun givenTwoOverallBudgetsWhenObservedThenBothAreKept() =
        runTest {
            fixture.budgets.insert(aBudget(categoryId = null, limit = 500_000L))
            fixture.budgets.insert(aBudget(categoryId = null, limit = 900_000L))

            assertThat(fixture.budgets.observeAll().first()).hasSize(2)
        }

    /** FR-074: a second budget for the same category is rejected and the stored list stays the same. */
    @Test
    fun givenBudgetForACategoryWhenASecondOneIsInsertedThenItIsRejected() =
        runTest {
            val categoryId = fixture.categories.insert(aCategory())
            fixture.budgets.insert(aBudget(categoryId))

            val failure = runCatching { fixture.budgets.insert(aBudget(categoryId)) }.exceptionOrNull()

            assertThat(failure).isNotNull()
            assertThat(fixture.budgets.observeAll().first()).hasSize(1)
        }
}
