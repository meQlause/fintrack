package com.meqlause.fintrack.core.model

/** DOC-11: side tables of a backup: monitored apps, pending inbox items, plans, loans, valuations, quick entries. */
public data class BackupAdjunct(
    public val monitoredApps: List<MonitoredApp>,
    public val inboxItems: List<InboxItem>,
    public val installmentPlans: List<InstallmentPlan>,
    public val loans: List<Loan>,
    public val valuations: List<ValuationEntry>,
    public val quickEntries: List<QuickEntry>,
)
