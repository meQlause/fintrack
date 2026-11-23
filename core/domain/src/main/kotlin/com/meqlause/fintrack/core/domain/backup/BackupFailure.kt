package com.meqlause.fintrack.core.domain.backup

/** DOC-11: why a backup could not be written or restored; the UI maps each reason to a human message. */
public enum class BackupFailure {
    PASSWORD_TOO_SHORT,
    TOO_LARGE,
    NOT_BACKUP,
    UNSUPPORTED_VERSION,
    WRONG_PASSWORD,
    CORRUPT,
    READ_FAILED,
    WRITE_FAILED,
}
