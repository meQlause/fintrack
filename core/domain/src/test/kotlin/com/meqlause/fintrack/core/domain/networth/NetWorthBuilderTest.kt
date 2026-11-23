package com.meqlause.fintrack.core.domain.networth

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.meqlause.fintrack.core.domain.loan.LoanEntry
import com.meqlause.fintrack.core.domain.period.DayContext
import com.meqlause.fintrack.core.model.Agreement
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.CreditTerms
import com.meqlause.fintrack.core.model.Loan
import com.meqlause.fintrack.core.model.LoanDirection
import com.meqlause.fintrack.core.model.LoanId
import com.meqlause.fintrack.core.model.LoanStatus
import com.meqlause.fintrack.core.model.Memo
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Movement
import com.meqlause.fintrack.core.model.Origin
import com.meqlause.fintrack.core.model.Routing
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
import java.time.ZoneOffset

// BR-043, BR-053: Saldo, Investasi, Piutang, Utang kartu and Hutang are summed by their own rules

/** Net worth builder tests. */
class NetWorthBuilderTest {
    private val cash = WalletId(1L)
    private val bank = WalletId(2L)
    private val card = WalletId(3L)
    private val invest = WalletId(4L)
    private val archived = WalletId(5L)
    private val excluded = WalletId(6L)
    private val today = LocalDate.of(2026, 11, 23)
    private val isoTimestamp = "2026-11-01T10:00:00Z"

    // BR-043, BR-053: Saldo leaves out credit and investment wallets, archived wallets and wallets not in the total

    /** Given mixed wallets when built then saldo counts only cash like wallets. */
    @Test
    fun givenMixedWalletsWhenBuiltThenSaldoCountsOnlyCashLikeWallets() {
        val result = NetWorthBuilder.build(ledger(), emptyList(), Money(900_000L), DayContext(today, ZoneOffset.UTC))
        assertThat(result.saldo).isEqualTo(Money(150_000L))
    }

    // BR-053: Investasi is the investment total given by the caller and Aset adds Piutang

    /** Given lent loan and investment when built then assets include both. */
    @Test
    fun givenLentLoanAndInvestmentWhenBuiltThenAssetsIncludeBoth() {
        val loans = listOf(loan(LoanDirection.LENT, 200_000L))
        val result = NetWorthBuilder.build(ledger(), loans, Money(900_000L), DayContext(today, ZoneOffset.UTC))
        assertThat(result.investasi).isEqualTo(Money(900_000L))
        assertThat(result.assets).isEqualTo(Money(150_000L + 900_000L + 200_000L))
    }

    // BR-043: Utang kartu is the outstanding of the credit wallet, which is the negative of its balance

    /** Given credit wallet with spending when built then card debt is outstanding. */
    @Test
    fun givenCreditWalletWithSpendingWhenBuiltThenCardDebtIsOutstanding() {
        val result = NetWorthBuilder.build(ledger(), emptyList(), Money(0L), DayContext(today, ZoneOffset.UTC))
        assertThat(result.utangKartu).isEqualTo(Money(400_000L))
    }

    // BR-043, BR-041: only OPEN loans count; a settled loan adds nothing and BORROWED loans are Hutang

    /** Given settled and borrowed loans when built then only open ones count. */
    @Test
    fun givenSettledAndBorrowedLoansWhenBuiltThenOnlyOpenOnesCount() {
        val loans =
            listOf(
                loan(LoanDirection.LENT, 300_000L, status = LoanStatus.SETTLED),
                loan(LoanDirection.BORROWED, 500_000L),
            )
        val result = NetWorthBuilder.build(ledger(), loans, Money(0L), DayContext(today, ZoneOffset.UTC))
        assertThat(result.piutang).isEqualTo(Money(0L))
        assertThat(result.hutang).isEqualTo(Money(500_000L))
    }

    // BR-053: Kekayaan bersih is Aset minus Utang

    /** Given parts when built then net worth is assets minus debts. */
    @Test
    fun givenPartsWhenBuiltThenNetWorthIsAssetsMinusDebts() {
        val loans = listOf(loan(LoanDirection.BORROWED, 100_000L))
        val result = NetWorthBuilder.build(ledger(), loans, Money(0L), DayContext(today, ZoneOffset.UTC))
        assertThat(result.total).isEqualTo(Money(150_000L - 400_000L - 100_000L))
    }

    private fun ledger(): NetWorthLedger {
        val wallets =
            listOf(
                wallet(cash, WalletType.CASH),
                wallet(bank, WalletType.BANK),
                wallet(card, WalletType.CREDIT_CARD, CreditTerms(Money(5_000_000L), statementDay = 25, dueDay = 10)),
                wallet(invest, WalletType.INVESTMENT),
                wallet(archived, WalletType.OTHER, status = WalletStatus(isArchived = true)),
                wallet(excluded, WalletType.OTHER, status = WalletStatus(isIncludedInTotal = false)),
            )
        return NetWorthLedger(wallets, balances(), listOf(cardSpend()), plans = emptyList())
    }

    private fun balances(): Map<WalletId, Money> =
        mapOf(
            cash to Money(50_000L),
            bank to Money(100_000L),
            card to Money(-400_000L),
            invest to Money(900_000L),
            archived to Money(70_000L),
            excluded to Money(80_000L),
        )

    private fun cardSpend(): Transaction =
        Transaction(
            transactionId = TransactionId(1L),
            movement = Movement(TransactionType.EXPENSE, Money(400_000L)),
            routing = Routing(card, null, categoryId = null, loanId = null),
            memo = Memo(),
            origin = Origin(TransactionSource.MANUAL, null),
            timing =
                Timing(
                    Instant.parse(isoTimestamp),
                    Audit(Instant.parse(isoTimestamp), Instant.parse(isoTimestamp)),
                ),
        )

    private fun wallet(
        walletId: WalletId,
        type: WalletType,
        credit: CreditTerms? = null,
        status: WalletStatus = WalletStatus(),
    ): Wallet =
        Wallet(
            walletId = walletId,
            kind = WalletKind(type, investKind = null),
            terms = WalletTerms(Money(0L), credit),
            profile = WalletProfile("Dompet ${walletId.value}", Appearance("wallet", "gray"), walletId.value.toInt()),
            status = status,
            audit = Audit(Instant.parse(isoTimestamp), Instant.parse(isoTimestamp)),
        )

    private fun loan(
        direction: LoanDirection,
        principal: Long,
        status: LoanStatus = LoanStatus.OPEN,
    ): LoanEntry {
        val agreement =
            Agreement(
                direction,
                "Andi",
                Money(principal),
                Instant.parse(isoTimestamp),
                dueDate = null,
                note = "",
            )
        val audit = Audit(Instant.parse(isoTimestamp), Instant.parse(isoTimestamp))
        val loan = Loan(LoanId(direction.ordinal.toLong() + 1), agreement, status, closedAt = null, audit)
        return LoanEntry(loan, movements = emptyList())
    }
}
