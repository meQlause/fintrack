package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.domain.reminder.CardBillReminders
import com.meqlause.fintrack.core.domain.reminder.InvestmentReminders
import com.meqlause.fintrack.core.domain.reminder.LoanReminders
import com.meqlause.fintrack.core.domain.reminder.Reminder
import com.meqlause.fintrack.core.domain.repository.InstallmentPlanRepository
import com.meqlause.fintrack.core.domain.repository.SettingsRepository
import com.meqlause.fintrack.core.domain.repository.TransactionRepository
import com.meqlause.fintrack.core.domain.repository.WalletRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

// BR-045, BR-055: gathers one snapshot and returns every reminder that is due today
public class TodayRemindersUseCase
    @Inject
    constructor(
        private val wallets: WalletRepository,
        private val transactions: TransactionRepository,
        private val plans: InstallmentPlanRepository,
        private val loans: ObserveLoansUseCase,
        private val settings: SettingsRepository,
    ) {
        /** BR-045: card, loan and (when enabled) investment reminders for [today]; nothing is stored. */
        public suspend operator fun invoke(
            today: LocalDate,
            zone: ZoneId,
        ): List<Reminder> {
            val walletRows = wallets.observeAll().first()
            val rows = transactions.observeAll().first()
            val card = CardBillReminders.of(walletRows, rows, plans.observeAll().first(), today, zone)
            val loanDue = LoanReminders.of(loans().first(), today, zone)
            val enabled =
                settings
                    .observe()
                    .first()
                    .preferences.investReminder
            val invest = InvestmentReminders.of(walletRows, enabled, today)
            return buildList {
                addAll(card)
                addAll(loanDue)
                addAll(invest)
            }
        }
    }
