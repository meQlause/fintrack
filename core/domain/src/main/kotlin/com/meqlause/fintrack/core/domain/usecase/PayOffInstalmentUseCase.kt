package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.domain.credit.StatementDates
import com.meqlause.fintrack.core.domain.repository.InstallmentPlanRepository
import com.meqlause.fintrack.core.domain.repository.WalletRepository
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.InstallmentPlanId
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject

/** FR-144, BR-037: early payoff bills every unbilled portion on the next statement */
public class PayOffInstalmentUseCase
    @Inject
    constructor(
        private val plans: InstallmentPlanRepository,
        private val wallets: WalletRepository,
        private val clock: Clock,
    ) {
        /** BR-037: the payoff statement is the first one whose cut-off is not yet past; unknown plans are ignored. */
        public suspend operator fun invoke(installmentPlanId: InstallmentPlanId) {
            val plan = plans.findPlan(installmentPlanId) ?: return
            val statementDay = wallets.statementDayOf(plan.purchase.walletId)
            val payoff = StatementDates.firstStatementMonth(LocalDate.now(clock), statementDay)
            val nowInstant = Instant.now(clock)
            plans.update(
                plan.copy(
                    schedule = plan.schedule.copy(earlyPayoffMonth = payoff),
                    audit = Audit(createdAt = plan.audit.createdAt, updatedAt = nowInstant),
                ),
            )
        }
    }
