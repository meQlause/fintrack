package com.meqlause.fintrack.feature.transaction.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meqlause.fintrack.core.domain.repository.CategoryRepository
import com.meqlause.fintrack.core.domain.repository.SettingsRepository
import com.meqlause.fintrack.core.domain.repository.TransactionRepository
import com.meqlause.fintrack.core.domain.repository.WalletRepository
import com.meqlause.fintrack.core.domain.statistics.StatisticsRequest
import com.meqlause.fintrack.core.domain.usecase.BuildPeriodStatisticsUseCase
import com.meqlause.fintrack.core.model.Category
import com.meqlause.fintrack.core.model.Transaction
import com.meqlause.fintrack.core.model.Wallet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.Clock
import java.time.YearMonth
import javax.inject.Inject

private const val STOP_TIMEOUT_MILLIS = 5_000L

// FR-036, FR-037, S-05: drives the Riwayat tab; stored transactions are filtered and grouped on every change
@HiltViewModel
internal class HistoryViewModel
    @Inject
    constructor(
        private val transactionRepository: TransactionRepository,
        private val walletRepository: WalletRepository,
        private val categoryRepository: CategoryRepository,
        private val settingsRepository: SettingsRepository,
        private val buildStatistics: BuildPeriodStatisticsUseCase,
        private val clock: Clock,
    ) : ViewModel() {
        private val selection = MutableStateFlow(HistorySelection(month = YearMonth.now(clock)))

        // FR-036: the list and totals of the picked month, emitted again after every change
        val state: StateFlow<HistoryUiState> =
            combine(
                selection,
                transactionRepository.observeAll(),
                walletRepository.observeAll(),
                categoryRepository.observeAll(),
                settingsRepository.observe(),
            ) { current, transactions, wallets, categories, settings ->
                compose(current, transactions, wallets, categories, settings.preferences.periodStartDay)
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = HistoryUiState(month = YearMonth.now(clock)),
            )

        // FR-036, FR-037: every change the screen asks for is reduced into the selection
        fun onEvent(event: HistoryEvent) {
            selection.update { it.reduce(event) }
        }

        private fun compose(
            current: HistorySelection,
            transactions: List<Transaction>,
            wallets: List<Wallet>,
            categories: List<Category>,
            startDay: Int,
        ): HistoryUiState {
            val period = monthPeriod(current.month, startDay)
            val names =
                HistoryNames(
                    wallets = wallets.associate { it.walletId to it.profile.name },
                    categories = categories.associate { it.categoryId to it.profile.name },
                    appearances = categories.associate { it.categoryId to it.profile.appearance },
                )
            val content = buildHistoryContent(transactions, current.filter, period, names, clock)
            val statistics =
                buildStatistics(
                    transactions = transactions,
                    request =
                        StatisticsRequest(
                            type = current.statisticsType,
                            period = period,
                            startDay = startDay,
                            zone = clock.zone,
                        ),
                )
            return HistoryUiState(
                month = current.month,
                period = period,
                filter = current.filter,
                isSearchOpen = current.isSearchOpen,
                sheet = current.sheet,
                pickerYear = current.pickerYear,
                wallets = wallets.filterNot { it.status.isArchived },
                categories = categories.filterNot { it.state.isArchived }.sortedBy { it.profile.sortOrder },
                groups = content.groups,
                totals = content.totals,
                hasRecords = content.hasRecords,
                activeMonths = transactions.map { YearMonth.from(it.timing.occurredAt.atZone(clock.zone)) }.toSet(),
                isLoaded = true,
                view = current.view,
                statisticsType = current.statisticsType,
                isCategoriesExpanded = current.isCategoriesExpanded,
                statistics = statistics,
            )
        }
    }
