package com.meqlause.fintrack.core.domain.usecase

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import assertk.assertions.isInstanceOf
import com.meqlause.fintrack.core.domain.budget.InMemorySettingsRepository
import com.meqlause.fintrack.core.domain.reminder.InvestmentUpdateReminder
import com.meqlause.fintrack.core.domain.reminder.LoanDueReminder
import com.meqlause.fintrack.core.model.Agreement
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.Loan
import com.meqlause.fintrack.core.model.LoanDirection
import com.meqlause.fintrack.core.model.LoanId
import com.meqlause.fintrack.core.model.LoanStatus
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Wallet
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.model.WalletKind
import com.meqlause.fintrack.core.model.WalletProfile
import com.meqlause.fintrack.core.model.WalletStatus
import com.meqlause.fintrack.core.model.WalletTerms
import com.meqlause.fintrack.core.model.WalletType
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

// BR-045, BR-055: the daily job gathers loan and investment reminders for today and stores nothing

/** Today reminders use case tests. */
class TodayRemindersUseCaseTest {
    private val zone = ZoneId.of("Asia/Jakarta")
    private val stamp = Instant.parse("2026-11-01T00:00:00Z")
    private val loans = MemoryLoanRepository()
    private val movements = MemoryTransactionRepository()
    private val wallets = InMemoryWalletRepository()
    private val settings = InMemorySettingsRepository()
    private val useCase =
        TodayRemindersUseCase(
            wallets = wallets,
            transactions = movements,
            plans = InMemoryInstallmentPlanRepository(),
            loans = ObserveLoansUseCase(loans, movements),
            settings = settings,
        )

    /** FR-154: given a loan due tomorrow when collected then its loan reminder is returned. */
    @Test
    fun givenLoanDueTomorrowWhenCollectedThenLoanReminderIsReturned() {
        runBlocking {
            addLoan(dueOn = LocalDate.parse("2026-11-24"))
            val reminders = useCase(LocalDate.parse("2026-11-23"), zone)
            assertThat(reminders).hasSize(1)
            assertThat(reminders.single()).isInstanceOf(LoanDueReminder::class)
        }
    }

    /** FR-154: given a loan due on another day when collected then no loan reminder is returned. */
    @Test
    fun givenLoanDueOnAnotherDayWhenCollectedThenNoLoanReminderIsReturned() {
        runBlocking {
            addLoan(dueOn = LocalDate.parse("2026-12-20"))
            assertThat(useCase(LocalDate.parse("2026-11-23"), zone)).isEmpty()
        }
    }

    /** BR-055: given investment reminder on and an investment wallet first when collected then it is returned. */
    @Test
    fun givenInvestReminderOnAndInvestmentOnFirstWhenCollectedThenMonthlyReminderIsReturned() {
        runBlocking {
            settings.update { it.copy(preferences = it.preferences.copy(investReminder = true)) }
            wallets.insert(wallet(WalletType.INVESTMENT))
            val reminders = useCase(LocalDate.parse("2026-12-01"), zone)
            assertThat(reminders.single()).isInstanceOf(InvestmentUpdateReminder::class)
        }
    }

    /** BR-055: given investment reminder off when collected then no investment reminder is returned. */
    @Test
    fun givenInvestReminderOffWhenCollectedThenNoInvestmentReminderIsReturned() {
        runBlocking {
            wallets.insert(wallet(WalletType.INVESTMENT))
            assertThat(useCase(LocalDate.parse("2026-12-01"), zone)).isEmpty()
        }
    }

    private suspend fun addLoan(dueOn: LocalDate) {
        val dueDate = dueOn.atStartOfDay(zone).toInstant()
        val agreement = Agreement(LoanDirection.LENT, "Andi", Money(100_000L), stamp, dueDate, "")
        loans.insert(Loan(LoanId(0L), agreement, LoanStatus.OPEN, null, Audit(stamp, stamp)))
    }

    private fun wallet(type: WalletType): Wallet =
        Wallet(
            walletId = WalletId(0L),
            kind = WalletKind(type, null),
            terms = WalletTerms(Money(0L), null),
            profile = WalletProfile("Saham", Appearance("chart", "green"), 0),
            status = WalletStatus(),
            audit = Audit(stamp, stamp),
        )
}
