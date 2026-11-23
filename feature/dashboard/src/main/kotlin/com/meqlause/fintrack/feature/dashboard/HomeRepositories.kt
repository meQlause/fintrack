package com.meqlause.fintrack.feature.dashboard

import com.meqlause.fintrack.core.domain.repository.CategoryRepository
import com.meqlause.fintrack.core.domain.repository.InboxRepository
import com.meqlause.fintrack.core.domain.repository.QuickEntryRepository
import com.meqlause.fintrack.core.domain.repository.SettingsRepository
import com.meqlause.fintrack.core.domain.repository.TransactionRepository
import com.meqlause.fintrack.core.domain.repository.WalletRepository
import javax.inject.Inject

// DOC-03: the repositories Beranda reads, injected as one bundle so the view model keeps its parameter count low
internal class HomeRepositories
    @Inject
    constructor(
        val wallets: WalletRepository,
        val transactions: TransactionRepository,
        val categories: CategoryRepository,
        val settings: SettingsRepository,
        val inboxItems: InboxRepository,
        val quickEntries: QuickEntryRepository,
    )
