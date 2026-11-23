package com.meqlause.fintrack.core.domain.reminder

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.Money
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

// BR-055: the monthly investment reminder is off by default and fires on day 1 only

/** Investment reminders tests. */
class InvestmentRemindersTest {
    private val firstOfMonth = LocalDate.parse("2026-12-01")
    private val investment = wallet(WalletType.INVESTMENT)

    /** Given enabled and investment wallet on first when of then one reminder. */
    @Test
    fun givenEnabledAndInvestmentWalletOnFirstWhenOfThenOneReminder() {
        assertThat(InvestmentReminders.collect(listOf(investment), enabled = true, today = firstOfMonth)).hasSize(1)
    }

    /** Given disabled setting when of then nothing. */
    @Test
    fun givenDisabledSettingWhenOfThenNothing() {
        assertThat(InvestmentReminders.collect(listOf(investment), enabled = false, today = firstOfMonth)).isEmpty()
    }

    /** Given no investment wallet when of then nothing. */
    @Test
    fun givenNoInvestmentWalletWhenOfThenNothing() {
        val cash = wallet(WalletType.CASH)
        assertThat(InvestmentReminders.collect(listOf(cash), enabled = true, today = firstOfMonth)).isEmpty()
    }

    /** Given not first day when of then nothing. */
    @Test
    fun givenNotFirstDayWhenOfThenNothing() {
        val second = LocalDate.parse("2026-12-02")
        assertThat(InvestmentReminders.collect(listOf(investment), enabled = true, today = second)).isEmpty()
    }

    private fun wallet(type: WalletType): Wallet =
        Wallet(
            walletId = WalletId(1L),
            kind = WalletKind(type, null),
            terms = WalletTerms(Money(0L), null),
            profile = WalletProfile("Saham", Appearance("chart", "green"), 0),
            status = WalletStatus(),
            audit = Audit(Instant.parse("2026-01-01T00:00:00Z"), Instant.parse("2026-01-01T00:00:00Z")),
        )
}
