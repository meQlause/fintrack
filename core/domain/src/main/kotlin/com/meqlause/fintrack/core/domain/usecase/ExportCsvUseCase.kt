package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.domain.repository.TransactionCsvRepository
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

/** FR-113: writes the transactions of a date range to the CSV file the user picked. */
public class ExportCsvUseCase
    @Inject
    constructor(
        private val csv: TransactionCsvRepository,
    ) {
        /** FR-113: dates are read in the device zone; throws BackupException when the file cannot be written. */
        public suspend operator fun invoke(
            uri: String,
            start: LocalDate,
            end: LocalDate,
        ) {
            csv.export(uri, start, end, ZoneId.systemDefault())
        }
    }
