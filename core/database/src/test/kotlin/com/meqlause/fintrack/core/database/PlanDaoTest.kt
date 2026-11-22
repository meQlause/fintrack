package com.meqlause.fintrack.core.database

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.meqlause.fintrack.core.model.InstallmentStatus
import com.meqlause.fintrack.core.model.LoanStatus
import com.meqlause.fintrack.core.model.TransactionType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** DOC-02: insert and read back installment, loan, valuation and quick entry tables */
@RunWith(RobolectricTestRunner::class)
class PlanDaoTest {
    private lateinit var database: FintrackDatabase

    /** DOC-02: a fresh in-memory database for each test. */
    @Before
    fun open() {
        database = openInMemoryDatabase()
    }

    /** DOC-02: the in-memory database is closed after each test. */
    @After
    fun close() {
        database.close()
    }

    /** DOC-02: an installment plan starts ACTIVE and keeps its tenor (FR-140). */
    @Test
    fun `installment plan inserted then observed starts active (FR-140, DOC-02)`() {
        runBlocking {
            val walletId = database.walletDao().insert(sampleWallet())
            val purchaseId = database.transactionDao().insert(sampleTransaction(walletId))
            database.installmentPlanDao().insert(sampleInstallmentPlan(purchaseId, walletId))
            val plan =
                database
                    .installmentPlanDao()
                    .observeAll()
                    .first()
                    .single()
            assertThat(plan.status).isEqualTo(InstallmentStatus.ACTIVE)
            assertThat(plan.schedule.tenorMonths).isEqualTo(6)
            assertThat(plan.adminFeeTransactionId).isNull()
        }
    }

    /** DOC-02: a loan keeps its direction and starts OPEN with no close date (FR-150). */
    @Test
    fun `loan inserted then observed starts open (FR-150, DOC-02)`() {
        runBlocking {
            database.loanDao().insert(sampleLoan())
            val loan =
                database
                    .loanDao()
                    .observeAll()
                    .first()
                    .single()
            assertThat(loan.status).isEqualTo(LoanStatus.OPEN)
            assertThat(loan.closedAt).isNull()
            assertThat(loan.terms.personName).isEqualTo("Budi")
        }
    }

    /** DOC-02: a valuation entry records the manual value of an investment wallet (FR-172). */
    @Test
    fun `valuation entry inserted then observed (FR-172, DOC-02)`() {
        runBlocking {
            val walletId = database.walletDao().insert(sampleWallet("Reksa"))
            database.valuationEntryDao().insert(sampleValuation(walletId))
            val valuation =
                database
                    .valuationEntryDao()
                    .observeAll()
                    .first()
                    .single()
            assertThat(valuation.value).isEqualTo(1_200_000L)
            assertThat(valuation.walletId).isEqualTo(walletId)
        }
    }

    /** DOC-02: a quick entry keeps its title and expense target (FR-039). */
    @Test
    fun `quick entry inserted then observed keeps its target (FR-039, DOC-02)`() {
        runBlocking {
            val walletId = database.walletDao().insert(sampleWallet())
            val categoryId = database.categoryDao().insert(sampleCategory())
            database.quickEntryDao().insert(sampleQuickEntry(categoryId, walletId))
            val entry =
                database
                    .quickEntryDao()
                    .observeAll()
                    .first()
                    .single()
            assertThat(entry.title).isEqualTo("Kopi pagi")
            assertThat(entry.target.type).isEqualTo(TransactionType.EXPENSE)
        }
    }
}
