package com.meqlause.fintrack.core.model

/** DOC-02: user preferences kept in DataStore. */
public data class Preferences(
    public val onboardingCompleted: Boolean = false,
    public val themeMode: ThemeMode = ThemeMode.SYSTEM,
    public val periodStartDay: Int = 1,
    public val hideAmountsByDefault: Boolean = false,
    public val headsUpForInbox: Boolean = true,
    public val investReminder: Boolean = false,
)
