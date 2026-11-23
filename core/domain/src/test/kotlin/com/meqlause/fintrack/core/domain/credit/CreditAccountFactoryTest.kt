package com.meqlause.fintrack.core.domain.credit

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
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
import org.junit.jupiter.api.assertThrows
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneOffset

// BR-031, BR-033, BR-035: the factory maps stored rows to the statement input with the same signs as the balance query

/** Credit account factory tests. */
class CreditAccountFactoryTest {
    private val card = WalletId(1L)
    private val bank = WalletId(2L)

    /** Given expense and transfer in when built then entries carry signed deltas. */
    @Test
    fun givenExpenseAndTransferInWhenBuiltThenEntriesCarrySignedDeltas() {
        val account = CreditAccountFactory.build(creditWallet(), transactions(), emptyList(), ZoneOffset.UTC)

        assertThat(account.entries).hasSize(2)
        assertThat(account.entries[0].delta).isEqualTo(Money(-100_000L))
        assertThat(account.entries[0].isTransferIn).isFalse()
        assertThat(account.entries[1].delta).isEqualTo(Money(40_000L))
        assertThat(account.entries[1].isTransferIn).isTrue()
    }

    /** Given wallet without credit terms when built then rejected. */
    @Test
    fun givenWalletWithoutCreditTermsWhenBuiltThenRejected() {
        val plain = creditWallet().copy(terms = WalletTerms(Money(0L), credit = null))

        assertThrows<IllegalArgumentException> {
            CreditAccountFactory.build(plain, emptyList(), emptyList(), ZoneOffset.UTC)
        }
    }

    /** Given plan of this card when built then instalment uses the purchase date. */
    @Test
    fun givenPlanOfThisCardWhenBuiltThenInstalmentUsesThePurchaseDate() {
        val purchase = transactions()[0]
        val plan = plan(purchase.transactionId, walletId = card)

        val account = CreditAccountFactory.build(creditWallet(), transactions(), listOf(plan), ZoneOffset.UTC)

        assertThat(account.instalments).hasSize(1)
        assertThat(account.instalments[0].purchaseDate).isEqualTo(LocalDate.of(2026, 3, 5))
        assertThat(account.instalments[0].principal).isEqualTo(Money(100_000L))
        assertThat(account.instalments[0].tenorMonths).isEqualTo(3)
    }

    /** Given plan of another wallet when built then it is ignored. */
    @Test
    fun givenPlanOfAnotherWalletWhenBuiltThenItIsIgnored() {
        val plan = plan(transactions()[0].transactionId, walletId = bank)

        val account = CreditAccountFactory.build(creditWallet(), transactions(), listOf(plan), ZoneOffset.UTC)

        assertThat(account.instalments).hasSize(0)
    }

    private fun creditWallet(): Wallet =
        Wallet(
            walletId = card,
            kind = WalletKind(WalletType.CREDIT_CARD, null),
            terms = WalletTerms(Money(0L), CreditTerms(Money(1_000_000L), statementDay = 25, dueDay = 10)),
            profile = WalletProfile("Kartu", Appearance("card", "blue"), 0),
            status = WalletStatus(),
            audit = Audit(Instant.parse("2026-01-01T00:00:00Z"), Instant.parse("2026-01-01T00:00:00Z")),
        )

    private fun transactions(): List<Transaction> =
        listOf(
            rowOf(
                1L,
                Movement(TransactionType.EXPENSE, Money(100_000L)),
                Routing(card, null, null, null),
                "2026-03-05T10:00:00Z",
            ),
            rowOf(
                2L,
                Movement(TransactionType.TRANSFER, Money(40_000L)),
                Routing(bank, card, null, null),
                "2026-03-20T10:00:00Z",
            ),
            rowOf(
                3L,
                Movement(TransactionType.EXPENSE, Money(9_000L)),
                Routing(bank, null, null, null),
                "2026-03-21T10:00:00Z",
            ),
        )

    private fun rowOf(
        transactionNumber: Long,
        movement: Movement,
        routing: Routing,
        occurredAt: String,
    ): Transaction =
        Transaction(
            transactionId = TransactionId(transactionNumber),
            movement = movement,
            routing = routing,
            memo = Memo(),
            origin = Origin(TransactionSource.MANUAL, null),
            timing = Timing(Instant.parse(occurredAt), Audit(Instant.parse(occurredAt), Instant.parse(occurredAt))),
        )

    private fun plan(
        purchaseId: TransactionId,
        walletId: WalletId,
    ): InstallmentPlan =
        InstallmentPlan(
            installmentPlanId = InstallmentPlanId(1L),
            purchase = Purchase(purchaseId, walletId, "Sepatu", Money(100_000L), adminFeeTransactionId = null),
            schedule = Schedule(tenorMonths = 3, firstStatementMonth = YearMonth.of(2026, 4), earlyPayoffMonth = null),
            status = InstallmentStatus.ACTIVE,
            audit = Audit(Instant.EPOCH, Instant.EPOCH),
        )
}
