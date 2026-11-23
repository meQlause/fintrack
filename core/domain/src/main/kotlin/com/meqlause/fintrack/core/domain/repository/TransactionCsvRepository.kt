package com.meqlause.fintrack.core.domain.repository

import java.time.LocalDate
import java.time.ZoneId

/** FR-113, DOC-11: writes the transactions dated from [start] to [end], both inclusive, as CSV to the file at [uri]. */
public interface TransactionCsvRepository {
    /** FR-113: throws BackupException when the file cannot be written. */
    public suspend fun export(
        uri: String,
        start: LocalDate,
        end: LocalDate,
        zone: ZoneId,
    )
}
