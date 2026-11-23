package com.meqlause.fintrack.core.data.repository

import androidx.room.withTransaction
import com.meqlause.fintrack.core.data.mapper.toBudget
import com.meqlause.fintrack.core.data.mapper.toCategory
import com.meqlause.fintrack.core.data.mapper.toEntity
import com.meqlause.fintrack.core.data.mapper.toInboxItem
import com.meqlause.fintrack.core.data.mapper.toInstallmentPlan
import com.meqlause.fintrack.core.data.mapper.toLoan
import com.meqlause.fintrack.core.data.mapper.toLog
import com.meqlause.fintrack.core.data.mapper.toMerchantRule
import com.meqlause.fintrack.core.data.mapper.toMonitoredApp
import com.meqlause.fintrack.core.data.mapper.toQuickEntry
import com.meqlause.fintrack.core.data.mapper.toTransaction
import com.meqlause.fintrack.core.data.mapper.toValuationEntry
import com.meqlause.fintrack.core.data.mapper.toWallet
import com.meqlause.fintrack.core.database.FintrackDatabase
import com.meqlause.fintrack.core.domain.port.BackupStore
import com.meqlause.fintrack.core.model.BackupAdjunct
import com.meqlause.fintrack.core.model.BackupLedger
import com.meqlause.fintrack.core.model.BackupSnapshot

// DOC-11, FR-111: Room access for backups; replaceAll clears children first and inserts parents first
public class LocalBackupStore(
    private val database: FintrackDatabase,
) : BackupStore {
    override suspend fun read(): Pair<BackupLedger, BackupAdjunct> =
        database.withTransaction { readLedger() to readAdjunct() }

    override suspend fun replaceAll(snapshot: BackupSnapshot): Unit =
        database.withTransaction {
            clearAll()
            insertAll(snapshot)
        }

    private suspend fun readLedger(): BackupLedger {
        val reads = database.backupReadDao()
        return BackupLedger(
            wallets = reads.wallets().map { it.toWallet() },
            categories = reads.categories().map { it.toCategory() },
            transactions = reads.transactions().map { it.toTransaction() },
            budgets = reads.budgets().map { it.toBudget() },
            budgetAlertLogs = reads.budgetAlertLogs().map { it.toLog() },
            merchantRules = reads.merchantRules().map { it.toMerchantRule() },
        )
    }

    private suspend fun readAdjunct(): BackupAdjunct {
        val reads = database.backupReadDao()
        return BackupAdjunct(
            monitoredApps = reads.monitoredApps().map { it.toMonitoredApp() },
            inboxItems = reads.pendingInboxItems().map { it.toInboxItem() },
            installmentPlans = reads.installmentPlans().map { it.toInstallmentPlan() },
            loans = reads.loans().map { it.toLoan() },
            valuations = reads.valuations().map { it.toValuationEntry() },
            quickEntries = reads.quickEntries().map { it.toQuickEntry() },
        )
    }

    private suspend fun clearAll() {
        val clear = database.backupClearDao()
        clear.quickEntries()
        clear.valuations()
        clear.installmentPlans()
        clear.monitoredApps()
        clear.merchantRules()
        clear.budgetAlertLogs()
        clear.budgets()
        clear.transactions()
        clear.inboxItems()
        clear.loans()
        clear.categories()
        clear.wallets()
    }

    private suspend fun insertAll(snapshot: BackupSnapshot) {
        val write = database.backupWriteDao()
        val ledger = snapshot.ledger
        val adjunct = snapshot.adjunct
        write.wallets(ledger.wallets.map { it.toEntity() })
        write.categories(ledger.categories.map { it.toEntity() })
        write.loans(adjunct.loans.map { it.toEntity() })
        write.inboxItems(adjunct.inboxItems.map { it.toEntity() })
        write.transactions(ledger.transactions.map { it.toEntity() })
        write.budgets(ledger.budgets.map { it.toEntity() })
        write.budgetAlertLogs(ledger.budgetAlertLogs.map { it.toEntity() })
        write.merchantRules(ledger.merchantRules.map { it.toEntity() })
        write.monitoredApps(adjunct.monitoredApps.map { it.toEntity() })
        write.installmentPlans(adjunct.installmentPlans.map { it.toEntity() })
        write.valuations(adjunct.valuations.map { it.toEntity() })
        write.quickEntries(adjunct.quickEntries.map { it.toEntity() })
    }
}
