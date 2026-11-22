package com.meqlause.fintrack.core.model

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.YearMonth

/** DOC-02: instalment, loan, valuation and quick entry entities keep the fields they are built with. */
class PlanEntitiesTest {
    private val moment = Instant.parse("2026-11-22T03:00:00Z")
    private val audit = Audit(createdAt = moment, updatedAt = moment)

    /** FR-140, BR-035: an instalment plan keeps its purchase and tenor; portions are not stored. */
    @Test
    fun `given instalment plan when built then purchase and tenor are kept (BR-035)`() {
        val purchase =
            Purchase(
                transactionId = TransactionId(10L),
                walletId = WalletId(1L),
                title = "Handphone",
                principal = Money(3_000_000L),
                adminFeeTransactionId = null,
            )
        val schedule = Schedule(6, YearMonth.of(2026, 12), earlyPayoffMonth = null)
        val plan =
            InstallmentPlan(
                installmentPlanId = InstallmentPlanId(11L),
                purchase = purchase,
                schedule = schedule,
                status = InstallmentStatus.ACTIVE,
                audit = audit,
            )

        assertThat(plan.purchase.principal).isEqualTo(Money(3_000_000L))
        assertThat(plan.schedule.tenorMonths).isEqualTo(6)
        assertThat(plan.schedule.earlyPayoffMonth).isNull()
    }

    /** FR-150, ADR-016: a loan agreement keeps direction, person and principal. */
    @Test
    fun `given loan when built then direction person and principal are kept (ADR-016)`() {
        val agreement =
            Agreement(
                direction = LoanDirection.LENT,
                personName = "Andi",
                principal = Money(200_000L),
                startDate = moment,
                dueDate = null,
                note = "",
            )
        val loan = Loan(LoanId(12L), agreement, LoanStatus.OPEN, closedAt = null, audit = audit)

        assertThat(loan.agreement.direction).isEqualTo(LoanDirection.LENT)
        assertThat(loan.agreement.principal).isEqualTo(Money(200_000L))
        assertThat(loan.closedAt).isNull()
    }

    /** FR-172, ADR-017: a valuation entry keeps the manually entered value of a wallet. */
    @Test
    fun `given valuation entry when built then value and wallet are kept (FR-172)`() {
        val valuation =
            ValuationEntry(
                valuationId = ValuationEntryId(13L),
                walletId = WalletId(4L),
                value = Money(2_080_000L),
                valuedAt = moment,
                createdAt = moment,
            )

        assertThat(valuation.value).isEqualTo(Money(2_080_000L))
        assertThat(valuation.walletId).isEqualTo(WalletId(4L))
    }

    /** FR-039, BR-047: a quick entry keeps the template it pre-fills the add sheet with. */
    @Test
    fun `given quick entry when built then template fields are kept (BR-047)`() {
        val template = QuickTemplate(title = "Kopi pagi", type = TransactionType.EXPENSE, amount = Money(18_000L))
        val quickEntry =
            QuickEntry(
                quickEntryId = QuickEntryId(14L),
                template = template,
                categoryId = CategoryId(2L),
                walletId = WalletId(1L),
                sortOrder = 1,
                audit = audit,
            )

        assertThat(quickEntry.template.amount).isEqualTo(Money(18_000L))
        assertThat(quickEntry.template.type).isEqualTo(TransactionType.EXPENSE)
    }
}
