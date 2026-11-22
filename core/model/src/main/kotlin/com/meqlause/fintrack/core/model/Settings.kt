package com.meqlause.fintrack.core.model

/** DOC-02: app settings held in DataStore, not Room; grouped by concern. */
public data class Settings(
    public val preferences: Preferences = Preferences(),
    public val lastUsed: LastUsed = LastUsed(),
    public val security: Security = Security(),
    public val backup: Backup = Backup(),
)
