package com.meqlause.fintrack.core.backup

import com.meqlause.fintrack.core.model.BackupSnapshot

// DOC-11: every rule runs before the database is touched; one violation aborts the whole restore
internal object BackupValidator {
    private const val MAX_PERIOD_START_DAY = 28

    fun validate(snapshot: BackupSnapshot) {
        val ids = BackupIds(snapshot)
        LedgerRules.validate(snapshot.ledger, ids)
        AdjunctRules.validate(snapshot.adjunct, ids)
        BackupChecks.ensure(snapshot.preferences.periodStartDay in 1..MAX_PERIOD_START_DAY)
        BackupChecks.ensure(snapshot.backupReminderDays >= 0)
    }
}
