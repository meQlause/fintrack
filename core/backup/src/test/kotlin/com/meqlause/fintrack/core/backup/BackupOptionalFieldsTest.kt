package com.meqlause.fintrack.core.backup

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.meqlause.fintrack.core.model.Agreement
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.BackupAdjunct
import com.meqlause.fintrack.core.model.BackupLedger
import com.meqlause.fintrack.core.model.BackupSnapshot
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.InboxItem
import com.meqlause.fintrack.core.model.InboxItemId
import com.meqlause.fintrack.core.model.InboxStatus
import com.meqlause.fintrack.core.model.InstallmentPlan
import com.meqlause.fintrack.core.model.InstallmentPlanId
import com.meqlause.fintrack.core.model.InstallmentStatus
import com.meqlause.fintrack.core.model.Loan
import com.meqlause.fintrack.core.model.LoanDirection
import com.meqlause.fintrack.core.model.LoanId
import com.meqlause.fintrack.core.model.LoanStatus
import com.meqlause.fintrack.core.model.Memo
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.MoneyDirection
import com.meqlause.fintrack.core.model.Movement
import com.meqlause.fintrack.core.model.Origin
import com.meqlause.fintrack.core.model.ParsedAmount
import com.meqlause.fintrack.core.model.Purchase
import com.meqlause.fintrack.core.model.RawNotice
import com.meqlause.fintrack.core.model.Resolution
import com.meqlause.fintrack.core.model.Routing
import com.meqlause.fintrack.core.model.Schedule
import com.meqlause.fintrack.core.model.Suggestion
import com.meqlause.fintrack.core.model.Timing
import com.meqlause.fintrack.core.model.Transaction
import com.meqlause.fintrack.core.model.TransactionId
import com.meqlause.fintrack.core.model.TransactionSource
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.WalletId
import org.junit.Test
import java.time.YearMonth

/** DOC-11: the optional fields that the plain sample leaves empty survive a backup round trip. */
class BackupOptionalFieldsTest {
    private val password = "rahasia-123"
    private val time = SampleSnapshot.fixedTime
    private val audit = Audit(time, time)

    /** A snapshot with every optional reference and note filled restores unchanged. */
    @Test
    fun optionalFieldsRoundTrip() {
        val filled = filledSnapshot()
        val restored = BackupCodec.decode(BackupCodec.encode(filled, password, "1.0.0"), password)
        assertThat(restored).isEqualTo(filled)
    }

    private fun filledSnapshot(): BackupSnapshot {
        val base = SampleSnapshot.build()
        return base.copy(
            ledger = ledger(base.ledger),
            adjunct = adjunct(base.adjunct),
        )
    }

    private fun ledger(base: BackupLedger): BackupLedger =
        base.copy(transactions = listOf(expenseWithFee(), transferWithNote()))

    private fun adjunct(base: BackupAdjunct): BackupAdjunct =
        base.copy(
            inboxItems = listOf(confirmedInbox(), openInbox()),
            installmentPlans = listOf(planWithAdminFee()),
            loans = listOf(loanWithDueDate()),
        )

    private fun expenseWithFee(): Transaction =
        Transaction(
            TransactionId(1),
            Movement(TransactionType.EXPENSE, Money(25_000), Money(500)),
            Routing(WalletId(1), null, CategoryId(1), null),
            Memo("Kopi", "KOPI"),
            Origin(TransactionSource.NOTIFICATION, InboxItemId(1)),
            Timing(time, audit),
        )

    private fun transferWithNote(): Transaction =
        Transaction(
            TransactionId(2),
            Movement(TransactionType.TRANSFER, Money(50_000), Money(1_000)),
            Routing(WalletId(1), WalletId(2), null, null),
            Memo("Pindah", "BCA"),
            Origin(TransactionSource.MANUAL, null),
            Timing(time, audit),
        )

    private fun confirmedInbox(): InboxItem =
        InboxItem(
            InboxItemId(1),
            RawNotice("com.example.pay", "parser", "Pay", "Pembayaran Rp25.000", time, "fingerprint"),
            ParsedAmount(Money(25_000), MoneyDirection.DEBIT, "KOPI"),
            Suggestion(WalletId(1), CategoryId(1), null),
            Resolution(InboxStatus.CONFIRMED, TransactionId(1), time),
            audit,
        )

    private fun openInbox(): InboxItem =
        InboxItem(
            InboxItemId(2),
            RawNotice("com.example.pay", "parser", "Pay", "Transfer Rp10.000", time, "fingerprint-2"),
            ParsedAmount(Money(10_000), MoneyDirection.CREDIT, ""),
            Suggestion(null, null, null),
            Resolution(InboxStatus.PENDING, null, null),
            audit,
        )

    private fun planWithAdminFee(): InstallmentPlan =
        InstallmentPlan(
            InstallmentPlanId(1),
            Purchase(TransactionId(1), WalletId(2), "Laptop", Money(25_000), TransactionId(2)),
            Schedule(12, YearMonth.of(2026, 12), YearMonth.of(2027, 6)),
            InstallmentStatus.ACTIVE,
            audit,
        )

    private fun loanWithDueDate(): Loan =
        Loan(
            LoanId(1),
            Agreement(LoanDirection.LENT, "Budi", Money(100_000), time, time.plusSeconds(DUE_IN_SECONDS), "catatan"),
            LoanStatus.OPEN,
            null,
            audit,
        )

    private companion object {
        const val DUE_IN_SECONDS = 2_592_000L
    }
}
