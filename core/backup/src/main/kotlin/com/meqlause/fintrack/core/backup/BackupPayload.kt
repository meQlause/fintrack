package com.meqlause.fintrack.core.backup

import com.meqlause.fintrack.core.domain.backup.BackupException
import com.meqlause.fintrack.core.domain.backup.BackupFailure
import com.meqlause.fintrack.core.model.BackupAdjunct
import com.meqlause.fintrack.core.model.BackupLedger
import com.meqlause.fintrack.core.model.BackupSnapshot
import kotlinx.serialization.Serializable

// DOC-11: the JSON root of a backup, schema version 1; body properties keep the flat layout of the spec
@Serializable
internal class BackupPayload {
    var schemaVersion: Int = BackupFormat.SCHEMA_VERSION
    var appVersionName: String = ""
    var databaseVersion: Int = BackupFormat.DATABASE_VERSION
    var createdAt: String = ""
    var wallets: List<WalletDto> = emptyList()
    var categories: List<CategoryDto> = emptyList()
    var transactions: List<TransactionDto> = emptyList()
    var budgets: List<BudgetDto> = emptyList()
    var budgetAlertLogs: List<BudgetAlertLogDto> = emptyList()
    var merchantRules: List<MerchantRuleDto> = emptyList()
    var monitoredApps: List<MonitoredAppDto> = emptyList()
    var inboxItems: List<InboxItemDto> = emptyList()
    var installmentPlans: List<InstallmentPlanDto> = emptyList()
    var loans: List<LoanDto> = emptyList()
    var valuations: List<ValuationDto> = emptyList()
    var quickEntries: List<QuickEntryDto> = emptyList()
    var settings: SettingsDto? = null

    fun toSnapshot(): BackupSnapshot {
        val stored = settings ?: throw BackupException(BackupFailure.CORRUPT)
        return BackupSnapshot(
            createdAt = BackupValues.instant(createdAt),
            ledger = ledger(),
            adjunct = adjunct(),
            preferences = stored.toPreferences(),
            backupReminderDays = stored.backupReminderDays,
        )
    }

    private fun ledger(): BackupLedger =
        BackupLedger(
            wallets = wallets.map { it.toModel() },
            categories = categories.map { it.toModel() },
            transactions = transactions.map { it.toModel() },
            budgets = budgets.map { it.toModel() },
            budgetAlertLogs = budgetAlertLogs.map { it.toModel() },
            merchantRules = merchantRules.map { it.toModel() },
        )

    private fun adjunct(): BackupAdjunct =
        BackupAdjunct(
            monitoredApps = monitoredApps.map { it.toModel() },
            inboxItems = inboxItems.map { it.toModel() },
            installmentPlans = installmentPlans.map { it.toModel() },
            loans = loans.map { it.toModel() },
            valuations = valuations.map { it.toModel() },
            quickEntries = quickEntries.map { it.toModel() },
        )

    companion object {
        fun from(
            snapshot: BackupSnapshot,
            appVersionName: String,
        ): BackupPayload {
            val payload = BackupPayload()
            payload.appVersionName = appVersionName
            payload.createdAt = snapshot.createdAt.toString()
            payload.fillLedger(snapshot.ledger)
            payload.fillAdjunct(snapshot.adjunct)
            payload.settings = SettingsDto.from(snapshot.preferences, snapshot.backupReminderDays)
            return payload
        }
    }

    private fun fillLedger(ledger: BackupLedger) {
        wallets = ledger.wallets.map(WalletDto::from)
        categories = ledger.categories.map(CategoryDto::from)
        transactions = ledger.transactions.map(TransactionDto::from)
        budgets = ledger.budgets.map(BudgetDto::from)
        budgetAlertLogs = ledger.budgetAlertLogs.map(BudgetAlertLogDto::from)
        merchantRules = ledger.merchantRules.map(MerchantRuleDto::from)
    }

    private fun fillAdjunct(adjunct: BackupAdjunct) {
        monitoredApps = adjunct.monitoredApps.map(MonitoredAppDto::from)
        inboxItems = adjunct.inboxItems.map(InboxItemDto::from)
        installmentPlans = adjunct.installmentPlans.map(InstallmentPlanDto::from)
        loans = adjunct.loans.map(LoanDto::from)
        valuations = adjunct.valuations.map(ValuationDto::from)
        quickEntries = adjunct.quickEntries.map(QuickEntryDto::from)
    }
}
