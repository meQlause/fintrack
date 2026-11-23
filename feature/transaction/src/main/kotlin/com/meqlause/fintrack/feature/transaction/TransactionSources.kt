package com.meqlause.fintrack.feature.transaction

import com.meqlause.fintrack.core.domain.repository.CategoryRepository
import com.meqlause.fintrack.core.domain.repository.SettingsRepository
import com.meqlause.fintrack.core.domain.repository.TransactionRepository
import com.meqlause.fintrack.core.domain.repository.WalletRepository
import javax.inject.Inject

// DOC-03: the four stores the Riwayat and the add sheet read from, injected as one
internal class TransactionSources
    @Inject
    constructor(
        val transactions: TransactionRepository,
        val wallets: WalletRepository,
        val categories: CategoryRepository,
        val settings: SettingsRepository,
    )
