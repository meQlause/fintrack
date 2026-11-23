package com.meqlause.fintrack.core.backup

import com.meqlause.fintrack.core.model.BackupAdjunct

// DOC-11, BR-035, BR-046: side-table rules a crafted or damaged backup must not break
internal object AdjunctRules {
    fun validate(
        adjunct: BackupAdjunct,
        ids: BackupIds,
    ) {
        validateInbox(adjunct, ids)
        validateMonitoredApps(adjunct, ids)
        validatePlansAndLoans(adjunct, ids)
        validateValuationsAndQuickEntries(adjunct, ids)
    }

    private fun validateInbox(
        adjunct: BackupAdjunct,
        ids: BackupIds,
    ) {
        BackupChecks.unique(adjunct.inboxItems.map { it.inboxItemId.value })
        adjunct.inboxItems.forEach { item ->
            BackupChecks.amount(item.parsed.amount)
            BackupChecks.optional(ids.walletIds, item.suggestion.suggestedWalletId?.value)
            BackupChecks.optional(ids.categoryIds, item.suggestion.suggestedCategoryId?.value)
            BackupChecks.optional(ids.inboxItemIds, item.suggestion.relatedInboxItemId?.value)
            BackupChecks.optional(ids.transactionIds, item.resolution.transactionId?.value)
        }
    }

    private fun validateMonitoredApps(
        adjunct: BackupAdjunct,
        ids: BackupIds,
    ) {
        BackupChecks.unique(adjunct.monitoredApps.map { it.packageName })
        adjunct.monitoredApps.forEach { BackupChecks.optional(ids.walletIds, it.defaultWalletId?.value) }
    }

    private fun validatePlansAndLoans(
        adjunct: BackupAdjunct,
        ids: BackupIds,
    ) {
        BackupChecks.unique(adjunct.installmentPlans.map { it.installmentPlanId.value })
        adjunct.installmentPlans.forEach { plan ->
            BackupChecks.ensure(plan.schedule.tenorMonths > 0)
            BackupChecks.amount(plan.purchase.principal)
            BackupChecks.present(ids.walletIds, plan.purchase.walletId.value)
            BackupChecks.present(ids.transactionIds, plan.purchase.transactionId.value)
            BackupChecks.optional(ids.transactionIds, plan.purchase.adminFeeTransactionId?.value)
        }
        BackupChecks.unique(adjunct.loans.map { it.loanId.value })
        adjunct.loans.forEach { loan ->
            BackupChecks.amount(loan.agreement.principal)
            BackupChecks.ensure(loan.agreement.personName.isNotBlank())
        }
    }

    private fun validateValuationsAndQuickEntries(
        adjunct: BackupAdjunct,
        ids: BackupIds,
    ) {
        BackupChecks.unique(adjunct.valuations.map { it.valuationId.value })
        adjunct.valuations.forEach { entry ->
            BackupChecks.ensure(entry.value.rupiah >= 0)
            BackupChecks.present(ids.walletIds, entry.walletId.value)
        }
        BackupChecks.unique(adjunct.quickEntries.map { it.quickEntryId.value })
        adjunct.quickEntries.forEach { entry ->
            BackupChecks.amount(entry.template.amount)
            BackupChecks.present(ids.categoryIds, entry.categoryId.value)
            BackupChecks.present(ids.walletIds, entry.walletId.value)
        }
    }
}
