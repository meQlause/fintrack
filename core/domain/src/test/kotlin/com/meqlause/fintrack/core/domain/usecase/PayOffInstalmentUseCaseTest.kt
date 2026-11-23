package com.meqlause.fintrack.core.domain.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.CreditTerms
import com.meqlause.fintrack.core.model.InstallmentPlan
import com.meqlause.fintrack.core.model.InstallmentPlanId
import com.meqlause.fintrack.core.model.InstallmentStatus
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Purchase
import com.meqlause.fintrack.core.model.Schedule
import com.meqlause.fintrack.core.model.TransactionId
import com.meqlause.fintrack.core.model.Wallet
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.model.WalletKind
import com.meqlause.fintrack.core.model.WalletProfile
import com.meqlause.fintrack.core.model.WalletStatus
import com.meqlause.fintrack.core.model.WalletTerms
import com.meqlause.fintrack.core.model.WalletType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.YearMonth
import java.time.ZoneOffset

/** FR-144, BR-037: early payoff bills the remaining portions on the next statement. */
class PayOffInstalmentUseCaseTest {
    // 2026-11-26 is after the cut-off of the 25th, so the next statement is December
    private val currentTime = Instant.parse("2026-11-26T10:00:00Z")
    private val clock = Clock.fixed(currentTime, ZoneOffset.UTC)
    private val wallets = InMemoryWalletRepository()
    private val plans = InMemoryInstallmentPlanRepository()
    private val useCase = PayOffInstalmentUseCase(plans, wallets, clock)

    /** BR-037: the payoff month is the next statement after today's cut-off. */
    @Test
    fun `given active plan when paying off then the payoff is the next statement (BR-037)`() =
        runBlocking {
            val walletId = seedCard()
            val newId = plans.insert(plan(walletId))
            useCase(newId)
            val stored = plans.observeAll().first().single { it.installmentPlanId == newId }
            assertThat(stored.schedule.earlyPayoffMonth).isEqualTo(YearMonth.of(2026, 12))
        }

    private fun plan(walletId: WalletId): InstallmentPlan =
        InstallmentPlan(
            installmentPlanId = InstallmentPlanId(0L),
            purchase =
                Purchase(
                    transactionId = TransactionId(1L),
                    walletId = walletId,
                    title = "HP",
                    principal = Money(3_000_000L),
                    adminFeeTransactionId = null,
                ),
            schedule = Schedule(tenorMonths = 6, firstStatementMonth = YearMonth.of(2026, 11), earlyPayoffMonth = null),
            status = InstallmentStatus.ACTIVE,
            audit = Audit(createdAt = currentTime, updatedAt = currentTime),
        )

    private fun seedCard(): WalletId =
        runBlocking {
            wallets.insert(
                Wallet(
                    walletId = WalletId(0L),
                    kind = WalletKind(WalletType.CREDIT_CARD, null),
                    terms = WalletTerms(Money(0L), CreditTerms(Money(10_000_000L), statementDay = 25, dueDay = 10)),
                    profile = WalletProfile("Kartu", Appearance("card", "teal"), 0),
                    status = WalletStatus(),
                    audit = Audit(currentTime, currentTime),
                ),
            )
        }
}
