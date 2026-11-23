package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.domain.repository.InstallmentPlanRepository
import com.meqlause.fintrack.core.domain.repository.WalletRepository
import com.meqlause.fintrack.core.model.InstallmentPlan
import com.meqlause.fintrack.core.model.InstallmentPlanId
import com.meqlause.fintrack.core.model.WalletId
import kotlinx.coroutines.flow.first

// FR-144, FR-145: the stored plan with the given id, or null when it no longer exists
internal suspend fun InstallmentPlanRepository.findPlan(installmentPlanId: InstallmentPlanId): InstallmentPlan? =
    observeAll().first().firstOrNull { it.installmentPlanId == installmentPlanId }

// BR-036: the statement day of the credit wallet a plan is billed on
internal suspend fun WalletRepository.statementDayOf(walletId: WalletId): Int {
    val wallet = observeAll().first().first { it.walletId == walletId }
    return requireNotNull(wallet.terms.credit) { "a plan belongs to a credit wallet (FR-139)" }.statementDay
}
