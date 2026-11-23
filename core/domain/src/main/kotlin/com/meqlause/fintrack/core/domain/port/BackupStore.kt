package com.meqlause.fintrack.core.domain.port

import com.meqlause.fintrack.core.model.BackupAdjunct
import com.meqlause.fintrack.core.model.BackupLedger
import com.meqlause.fintrack.core.model.BackupSnapshot

/** DOC-11: reads and replaces the Room tables that a backup covers; the data layer keeps them consistent. */
public interface BackupStore {
    /** DOC-11: the ledger and side tables, read as one consistent snapshot. */
    public suspend fun read(): Pair<BackupLedger, BackupAdjunct>

    /** DOC-11: deletes every backed-up row and inserts the snapshot rows with their ids, in one transaction. */
    public suspend fun replaceAll(snapshot: BackupSnapshot)
}
