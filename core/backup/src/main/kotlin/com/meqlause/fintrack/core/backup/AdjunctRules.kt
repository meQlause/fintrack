package com.meqlause.fintrack.core.backup

import com.meqlause.fintrack.core.model.BackupAdjunct

// DOC-11, BR-035, BR-046: side-table rules a crafted or damaged backup must not break
internal object AdjunctRules {
    fun validate(
        adjunct: BackupAdjunct,
        backupIds: BackupIds,
    ) {
        validateInbox(adjunct, backupIds)
        validateMonitoredApps(adjunct, backupIds)
        validatePlansAndLoans(adjunct, backupIds)
        validateValuationsAndQuickEntries(adjunct, backupIds)
    }

    private fun validateInbox(
        adjunct: BackupAdjunct,
        backupIds: BackupIds,
    ) {
        BackupChecks.unique(adjunct.inboxItems.map { it.inboxItemId.value })
        adjunct.inboxItems.forEach { item ->
            BackupChecks.amount(item.parsed.amount)
            BackupChecks.optional(backupIds.walletIds, item.suggestion.suggestedWalletId?.value)
            BackupChecks.optional(backupIds.categoryIds, item.suggestion.suggestedCategoryId?.value)
            BackupChecks.optional(backupIds.inboxItemIds, item.suggestion.relatedInboxItemId?.value)
            BackupChecks.optional(backupIds.transactionIds, item.resolution.transactionId?.value)
        }
    }

    private fun validateMonitoredApps(
        adjunct: BackupAdjunct,
        backupIds: BackupIds,
    ) {
        BackupChecks.unique(adjunct.monitoredApps.map { it.packageName })
        adjunct.monitoredApps.forEach { BackupChecks.optional(backupIds.walletIds, it.defaultWalletId?.value) }
    }

    private fun validatePlansAndLoans(
        adjunct: BackupAdjunct,
        backupIds: BackupIds,
    ) {
        BackupChecks.unique(adjunct.installmentPlans.map { it.installmentPlanId.value })
        adjunct.installmentPlans.forEach { plan ->
            BackupChecks.ensure(plan.schedule.tenorMonths > 0)
            BackupChecks.amount(plan.purchase.principal)
            BackupChecks.present(backupIds.walletIds, plan.purchase.walletId.value)
            BackupChecks.present(backupIds.transactionIds, plan.purchase.transactionId.value)
            BackupChecks.optional(backupIds.transactionIds, plan.purchase.adminFeeTransactionId?.value)
        }
        BackupChecks.unique(adjunct.loans.map { it.loanId.value })
        adjunct.loans.forEach { loan ->
            BackupChecks.amount(loan.agreement.principal)
            BackupChecks.ensure(loan.agreement.personName.isNotBlank())
        }
    }

    private fun validateValuationsAndQuickEntries(
        adjunct: BackupAdjunct,
        backupIds: BackupIds,
    ) {
        BackupChecks.unique(adjunct.valuations.map { it.valuationId.value })
        adjunct.valuations.forEach { entry ->
            BackupChecks.ensure(entry.value.rupiah >= 0)
            BackupChecks.present(backupIds.walletIds, entry.walletId.value)
        }
        BackupChecks.unique(adjunct.quickEntries.map { it.quickEntryId.value })
        adjunct.quickEntries.forEach { entry ->
            BackupChecks.amount(entry.template.amount)
            BackupChecks.present(backupIds.categoryIds, entry.categoryId.value)
            BackupChecks.present(backupIds.walletIds, entry.walletId.value)
        }
    }
}
