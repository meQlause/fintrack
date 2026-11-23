package com.meqlause.fintrack.core.domain.repository

import java.time.LocalDate
import java.time.ZoneId

/** FR-113, DOC-11: writes the transactions dated from [start] to [endDate], both inclusive, as CSV to [fileUri]. */
public interface TransactionCsvRepository {
    /** FR-113: throws BackupException when the file cannot be written. */
    public suspend fun export(
        fileUri: String,
        start: LocalDate,
        endDate: LocalDate,
        zone: ZoneId,
    )
}
