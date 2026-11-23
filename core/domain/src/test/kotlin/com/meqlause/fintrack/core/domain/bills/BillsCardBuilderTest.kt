package com.meqlause.fintrack.core.domain.bills

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import com.meqlause.fintrack.core.domain.period.Period
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.CreditTerms
import com.meqlause.fintrack.core.model.InstallmentPlan
import com.meqlause.fintrack.core.model.InstallmentPlanId
import com.meqlause.fintrack.core.model.InstallmentStatus
import com.meqlause.fintrack.core.model.Memo
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Movement
import com.meqlause.fintrack.core.model.Origin
import com.meqlause.fintrack.core.model.Purchase
import com.meqlause.fintrack.core.model.Routing
import com.meqlause.fintrack.core.model.Schedule
import com.meqlause.fintrack.core.model.StatementStatus
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
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneOffset

// FR-137, FR-146: the Tagihan card lists the next statement per credit wallet and the instalments due this period
class BillsCardBuilderTest {
    private val card = WalletId(1L)
    private val bank = WalletId(2L)
    private val today = LocalDate.of(2026, 11, 5)
    private val december = Period(LocalDate.of(2026, 12, 1), LocalDate.of(2026, 12, 31))

    // FR-137: the October statement is due on 10 Nov; FR-146: the November portion of 300.000 over 3 is due 10 Dec
    @Test
    fun givenStatementDueAndPortionDueInPeriodWhenBuiltThenBothAreReported() {
        val result = BillsCardBuilder.build(wallets(), transactions(), plans(), december, today, ZoneOffset.UTC)

        assertThat(result.bills).hasSize(1)
        assertThat(result.bills[0].walletName).isEqualTo("Kartu")
        assertThat(result.bills[0].statement.status).isEqualTo(StatementStatus.DUE)
        assertThat(
            result.bills[0]
                .statement.breakdown.remainingDue,
        ).isEqualTo(Money(1_000_000L))
        assertThat(result.instalmentsDue).isEqualTo(Money(100_000L))
    }

    // FR-146: a period with no statement falling due holds no instalment amount
    @Test
    fun givenPeriodWithoutDuePortionWhenBuiltThenInstalmentsAreZero() {
        val november = Period(LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 30))
        val result = BillsCardBuilder.build(wallets(), transactions(), plans(), november, today, ZoneOffset.UTC)

        assertThat(result.instalmentsDue).isEqualTo(Money(0L))
    }

    // FR-137: wallets without credit terms are not bills
    @Test
    fun givenPlainWalletWhenBuiltThenItIsNotListed() {
        val plain = wallets() + wallet(WalletId(3L), terms = WalletTerms(Money(0L), credit = null))
        val result = BillsCardBuilder.build(plain, transactions(), plans(), december, today, ZoneOffset.UTC)

        assertThat(result.bills.map { it.walletId }).isEqualTo(listOf(card))
        assertThat(result.bills).hasSize(1)
    }

    private fun wallets(): List<Wallet> =
        listOf(wallet(card, WalletTerms(Money(0L), CreditTerms(Money(10_000_000L), statementDay = 25, dueDay = 10))))

    private fun wallet(
        id: WalletId,
        terms: WalletTerms,
    ): Wallet =
        Wallet(
            walletId = id,
            kind = WalletKind(WalletType.CREDIT_CARD, null),
            terms = terms,
            profile = WalletProfile("Kartu", Appearance("card", "blue"), 0),
            status = WalletStatus(),
            audit = Audit(Instant.parse("2026-01-01T00:00:00Z"), Instant.parse("2026-01-01T00:00:00Z")),
        )

    // an expense of 1.000.000 on 10 Oct and a 300.000 purchase on 10 Oct that is split into three portions
    private fun transactions(): List<Transaction> =
        listOf(
            row(1L, TransactionType.EXPENSE, 1_000_000L, "2026-10-10T10:00:00Z"),
            row(2L, TransactionType.EXPENSE, 300_000L, "2026-10-10T11:00:00Z"),
        )

    private fun plans(): List<InstallmentPlan> =
        listOf(
            InstallmentPlan(
                installmentPlanId = InstallmentPlanId(1L),
                purchase = Purchase(TransactionId(2L), card, "Sepatu", Money(300_000L), adminFeeTransactionId = null),
                schedule =
                    Schedule(tenorMonths = 3, firstStatementMonth = YearMonth.of(2026, 11), earlyPayoffMonth = null),
                status = InstallmentStatus.ACTIVE,
                audit = Audit(Instant.EPOCH, Instant.EPOCH),
            ),
        )

    private fun row(
        id: Long,
        type: TransactionType,
        amount: Long,
        at: String,
    ): Transaction =
        Transaction(
            transactionId = TransactionId(id),
            movement = Movement(type, Money(amount)),
            routing = Routing(card, null, categoryId = null, loanId = null),
            memo = Memo(),
            origin = Origin(TransactionSource.MANUAL, null),
            timing = Timing(Instant.parse(at), Audit(Instant.parse(at), Instant.parse(at))),
        )
}
