package com.meqlause.fintrack.core.domain.backup

/** DOC-11: the failure of a backup operation, carrying the reason the UI shows. */
public class BackupException(
    public val failure: BackupFailure,
    cause: Throwable? = null,
) : RuntimeException(failure.name, cause)
