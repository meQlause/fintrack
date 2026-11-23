package com.meqlause.fintrack.core.backup

import com.meqlause.fintrack.core.domain.backup.BackupException
import com.meqlause.fintrack.core.domain.backup.BackupFailure
import com.meqlause.fintrack.core.model.Money

// DOC-11: the primitive checks of restore validation; any violation makes the whole backup "Cadangan rusak"
internal object BackupChecks {
    fun ensure(valid: Boolean) {
        if (!valid) throw BackupException(BackupFailure.CORRUPT)
    }

    fun unique(values: List<Any>) {
        ensure(values.toSet().size == values.size)
    }

    fun present(
        knownIds: Set<Long>,
        targetId: Long,
    ) {
        ensure(targetId in knownIds)
    }

    fun optional(
        knownIds: Set<Long>,
        targetId: Long?,
    ) {
        if (targetId != null) present(knownIds, targetId)
    }

    fun amount(value: Money) {
        ensure(value.isTransactionAmount())
    }
}
