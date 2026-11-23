package com.meqlause.fintrack.core.domain.budget

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import com.meqlause.fintrack.core.domain.usecase.SetBudgetLimitUseCase
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.Money
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

// FR-070, FR-074: one budget per target; a second limit for the same target updates it
internal class SetBudgetLimitUseCaseTest {
    private val budgets = InMemoryBudgetRepository()
    private val useCase = SetBudgetLimitUseCase(budgets, clockAt("2026-11-15T10:00:00Z"))

    @Test
    fun `given no budget when a limit is set then one budget is stored (FR-070)`() =
        runBlocking {
            useCase(CategoryId(CATEGORY_FOOD), Money(500_000L))
            val stored = budgets.observeAll().first()
            assertThat(stored).hasSize(1)
            assertThat(stored.single().limitAmount).isEqualTo(Money(500_000L))
        }

    @Test
    fun `given a budget for the category when another limit is set then it is updated not duplicated (FR-074)`() =
        runBlocking {
            useCase(CategoryId(CATEGORY_FOOD), Money(500_000L))
            useCase(CategoryId(CATEGORY_FOOD), Money(750_000L))
            val stored = budgets.observeAll().first()
            assertThat(stored).hasSize(1)
            assertThat(stored.single().limitAmount).isEqualTo(Money(750_000L))
        }
}
