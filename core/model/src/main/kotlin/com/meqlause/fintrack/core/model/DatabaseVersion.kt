package com.meqlause.fintrack.core.model

/** FR-111: single source of the Room schema version, shared by the database and the backup format. */
public object DatabaseVersion {
    /** The schema version of `FintrackDatabase`; bump together with a migration. */
    public const val CURRENT: Int = 2
}
