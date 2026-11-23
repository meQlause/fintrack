package com.meqlause.fintrack.feature.credit.instalment

import com.meqlause.fintrack.core.domain.repository.InstallmentPlanRepository
import com.meqlause.fintrack.core.domain.repository.TransactionRepository
import com.meqlause.fintrack.core.domain.repository.WalletRepository
import javax.inject.Inject

// FR-144, S-16: the three stores the instalment detail reads from
internal class InstalmentSources
    @Inject
    constructor(
        val plans: InstallmentPlanRepository,
        val transactions: TransactionRepository,
        val wallets: WalletRepository,
    )
