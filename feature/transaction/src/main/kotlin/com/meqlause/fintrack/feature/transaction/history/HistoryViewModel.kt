package com.meqlause.fintrack.feature.transaction.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meqlause.fintrack.core.domain.period.Period
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
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit
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
                compose(current, HistoryData(transactions, wallets, categories), settings.preferences.periodStartDay)
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = HistoryUiState(selection = HistorySelection(month = YearMonth.now(clock))),
            )

        // FR-036, FR-037: every change the screen asks for is reduced into the selection
        fun onEvent(event: HistoryEvent) {
            selection.update { it.reduce(event) }
        }

        private fun compose(
            current: HistorySelection,
            data: HistoryData,
            startDay: Int,
        ): HistoryUiState {
            val period = monthPeriod(current.month, startDay)
            val names = namesOf(data)
            val query = HistoryQuery(current.filter, period)
            val content = buildHistoryContent(data.transactions, query, names, clock)
            val request = StatisticsRequest(current.statisticsType, period, startDay, clock.zone)
            return HistoryUiState(
                selection = current,
                period = period,
                lists =
                    HistoryLists(
                        wallets = data.wallets.filterNot { it.status.isArchived },
                        categories =
                            data.categories
                                .filterNot {
                                    it.state.isArchived
                                }.sortedBy { it.profile.sortOrder },
                        groups = content.groups,
                    ),
                status = statusOf(content, data, period),
                statistics = buildStatistics(data.transactions, request),
            )
        }

        // FR-061: the status carries the index of today within the period, null when today is outside it
        private fun statusOf(
            content: HistoryContent,
            data: HistoryData,
            period: Period,
        ): HistoryStatus {
            val today = LocalDate.now(clock)
            return HistoryStatus(
                totals = content.totals,
                hasRecords = content.hasRecords,
                isLoaded = true,
                activeMonths = data.transactions.map { monthOf(it) }.toSet(),
                days = HistoryDays(activeDays = data.transactions.map { it.dateIn(clock.zone) }.toSet(), today = today),
                todayIndex = if (today in period.startDate..period.endDate) daysSince(period, today) else null,
            )
        }

        private fun daysSince(
            period: Period,
            date: LocalDate,
        ): Int = ChronoUnit.DAYS.between(period.startDate, date).toInt()

        // FR-036: the month a transaction falls in, in the device's zone
        private fun monthOf(transaction: Transaction): YearMonth =
            YearMonth.from(transaction.timing.occurredAt.atZone(clock.zone))

        // FR-036: the names and looks the rows borrow from the wallets and categories
        private fun namesOf(data: HistoryData): HistoryNames =
            HistoryNames(
                wallets = data.wallets.associate { it.walletId to it.profile.name },
                categories = data.categories.associate { it.categoryId to it.profile.name },
                appearances = data.categories.associate { it.categoryId to it.profile.appearance },
            )

        // FR-036: what the Riwayat joins the selection with
        private data class HistoryData(
            val transactions: List<Transaction>,
            val wallets: List<Wallet>,
            val categories: List<Category>,
        )
    }
