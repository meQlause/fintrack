package com.meqlause.fintrack.di

import com.meqlause.fintrack.core.backup.FileTransactionCsvRepository
import com.meqlause.fintrack.core.common.coroutines.IoDispatcher
import com.meqlause.fintrack.core.domain.port.BackupFiles
import com.meqlause.fintrack.core.domain.repository.CategoryRepository
import com.meqlause.fintrack.core.domain.repository.LoanRepository
import com.meqlause.fintrack.core.domain.repository.TransactionCsvRepository
import com.meqlause.fintrack.core.domain.repository.TransactionRepository
import com.meqlause.fintrack.core.domain.repository.WalletRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/** FR-113, DOC-11: builds the CSV export repository from the injected data sources. */
internal class CsvRepositoryFactory
    @Inject
    constructor(
        private val transactions: TransactionRepository,
        private val wallets: WalletRepository,
        private val categories: CategoryRepository,
        private val loans: LoanRepository,
        private val files: BackupFiles,
        @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    ) {
        /** FR-113: the CSV repository that writes the chosen range through the picked file. */
        fun create(): TransactionCsvRepository =
            FileTransactionCsvRepository(transactions, wallets, categories, loans, files, ioDispatcher)
    }
