package com.meqlause.fintrack.core.backup

import com.meqlause.fintrack.core.domain.backup.BackupException
import com.meqlause.fintrack.core.domain.backup.BackupFailure
import java.time.Instant
import java.time.YearMonth
import java.time.format.DateTimeParseException

// DOC-11: value codecs for the JSON payload; anything that does not parse is a corrupt backup
internal object BackupValues {
    fun instant(text: String): Instant =
        try {
            Instant.parse(text)
        } catch (error: DateTimeParseException) {
            throw BackupException(BackupFailure.CORRUPT, error)
        }

    fun yearMonth(text: String): YearMonth =
        try {
            YearMonth.parse(text)
        } catch (error: DateTimeParseException) {
            throw BackupException(BackupFailure.CORRUPT, error)
        }

    inline fun <reified E : Enum<E>> enumNamed(name: String): E =
        enumValues<E>().firstOrNull { it.name == name } ?: throw BackupException(BackupFailure.CORRUPT)
}
