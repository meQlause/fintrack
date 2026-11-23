package com.meqlause.fintrack.core.domain.budget

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.meqlause.fintrack.core.domain.usecase.EvaluateBudgetAlertsUseCase
import com.meqlause.fintrack.core.domain.usecase.InMemoryCategoryRepository
import com.meqlause.fintrack.core.domain.usecase.InMemoryTransactionRepository
import com.meqlause.fintrack.core.model.TransactionType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

// FR-073, BR-020, BR-021: alerts once per budget, period and threshold
internal class EvaluateBudgetAlertsUseCaseTest {
    private val budgets = InMemoryBudgetRepository()
    private val transactions = InMemoryTransactionRepository()
    private val categories = InMemoryCategoryRepository()
    private val settings = InMemorySettingsRepository()
    private val alertLog = InMemoryBudgetAlertLogRepository()
    private val notifier = RecordingBudgetAlertNotifier()

    private fun useCaseAt(iso: String) =
        EvaluateBudgetAlertsUseCase(
            budgets,
            transactions,
            categories,
            settings,
            alertLog,
            notifier,
            clockAt(iso),
        )

    @Test
    fun `given spending of 79 percent when evaluated then no alert is sent (BR-020)`() =
        runBlocking {
            budgets.insert(aBudget(limit = 1_000L))
            transactions.insert(aMovement(790L))
            useCaseAt("2026-11-15T10:00:00Z").invoke()
            assertThat(notifier.alerts).isEmpty()
        }

    @Test
    fun `given spending of 80 percent when evaluated twice then one warning alert is sent (FR-073)`() =
        runBlocking {
            budgets.insert(aBudget(limit = 1_000L))
            transactions.insert(aMovement(800L))
            val useCase = useCaseAt("2026-11-15T10:00:00Z")
            useCase.invoke()
            useCase.invoke()
            assertThat(notifier.alerts).hasSize(1)
            assertThat(notifier.alerts.single().threshold).isEqualTo(80)
        }

    @Test
    fun `given spending of 120 percent when evaluated then 80 and 100 are logged, one alert for 100 (BR-021)`() =
        runBlocking {
            budgets.insert(aBudget(limit = 1_000L))
            transactions.insert(aMovement(1_200L))
            useCaseAt("2026-11-15T10:00:00Z").invoke()
            assertThat(alertLog.observeAll().first()).hasSize(2)
            assertThat(notifier.alerts.single().threshold).isEqualTo(100)
        }

    @Test
    fun `given loan and transfer movements when evaluated then they do not count (BR-040)`() =
        runBlocking {
            budgets.insert(aBudget(limit = 1_000L))
            transactions.insert(aMovement(900L, type = TransactionType.LEND))
            transactions.insert(aMovement(900L, type = TransactionType.TRANSFER))
            useCaseAt("2026-11-15T10:00:00Z").invoke()
            assertThat(notifier.alerts).isEmpty()
        }

    @Test
    fun `given spending before the period start when evaluated then it does not count (BR-010)`() =
        runBlocking {
            budgets.insert(aBudget(limit = 1_000L))
            transactions.insert(aMovement(900L, date = "2026-10-31"))
            useCaseAt("2026-11-15T10:00:00Z").invoke()
            assertThat(notifier.alerts).isEmpty()
        }

    @Test
    fun `given a new period when spending crosses 80 percent again then it alerts again (BR-021)`() =
        runBlocking {
            budgets.insert(aBudget(limit = 1_000L))
            transactions.insert(aMovement(800L))
            useCaseAt("2026-11-15T10:00:00Z").invoke()
            transactions.insert(aMovement(800L, date = "2026-12-03"))
            useCaseAt("2026-12-05T10:00:00Z").invoke()
            assertThat(notifier.alerts).hasSize(2)
        }

    @Test
    fun `given the overall budget when spending adds up to 90 percent then the alert has no category name (FR-074)`() =
        runBlocking {
            budgets.insert(aBudget(limit = 1_000L, categoryId = null))
            transactions.insert(aMovement(500L, categoryId = CATEGORY_FOOD))
            transactions.insert(aMovement(400L, categoryId = CATEGORY_TRANSPORT))
            useCaseAt("2026-11-15T10:00:00Z").invoke()
            assertThat(notifier.alerts.single().categoryName).isNull()
        }
}
