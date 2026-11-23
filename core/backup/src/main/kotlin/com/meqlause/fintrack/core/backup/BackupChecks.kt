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
        ids: Set<Long>,
        id: Long,
    ) {
        ensure(id in ids)
    }

    fun optional(
        ids: Set<Long>,
        id: Long?,
    ) {
        if (id != null) present(ids, id)
    }

    fun amount(value: Money) {
        ensure(value.isTransactionAmount())
    }
}
