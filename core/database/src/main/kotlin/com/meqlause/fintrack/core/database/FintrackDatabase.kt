package com.meqlause.fintrack.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.meqlause.fintrack.core.database.converter.CoreEnumConverters
import com.meqlause.fintrack.core.database.converter.PlanEnumConverters
import com.meqlause.fintrack.core.database.dao.BackupReadDao
import com.meqlause.fintrack.core.database.dao.BackupWriteDao
import com.meqlause.fintrack.core.database.dao.BudgetAlertLogDao
import com.meqlause.fintrack.core.database.dao.BudgetDao
import com.meqlause.fintrack.core.database.dao.CategoryDao
import com.meqlause.fintrack.core.database.dao.InboxItemDao
import com.meqlause.fintrack.core.database.dao.InstallmentPlanDao
import com.meqlause.fintrack.core.database.dao.LoanDao
import com.meqlause.fintrack.core.database.dao.MerchantRuleDao
import com.meqlause.fintrack.core.database.dao.MonitoredAppDao
import com.meqlause.fintrack.core.database.dao.QuickEntryDao
import com.meqlause.fintrack.core.database.dao.TransactionDao
import com.meqlause.fintrack.core.database.dao.ValuationEntryDao
import com.meqlause.fintrack.core.database.dao.WalletDao
import com.meqlause.fintrack.core.database.entity.BudgetAlertLogEntity
import com.meqlause.fintrack.core.database.entity.BudgetEntity
import com.meqlause.fintrack.core.database.entity.CategoryEntity
import com.meqlause.fintrack.core.database.entity.InboxItemEntity
import com.meqlause.fintrack.core.database.entity.InstallmentPlanEntity
import com.meqlause.fintrack.core.database.entity.LoanEntity
import com.meqlause.fintrack.core.database.entity.MerchantRuleEntity
import com.meqlause.fintrack.core.database.entity.MonitoredAppEntity
import com.meqlause.fintrack.core.database.entity.QuickEntryEntity
import com.meqlause.fintrack.core.database.entity.TransactionEntity
import com.meqlause.fintrack.core.database.entity.ValuationEntryEntity
import com.meqlause.fintrack.core.database.entity.WalletEntity

/** DOC-02, NFR-009: the Fintrack database, schema version 1; every change needs a new version and a migration. */
@Database(
    entities = [
        WalletEntity::class,
        CategoryEntity::class,
        TransactionEntity::class,
        BudgetEntity::class,
        BudgetAlertLogEntity::class,
        InboxItemEntity::class,
        MerchantRuleEntity::class,
        MonitoredAppEntity::class,
        InstallmentPlanEntity::class,
        LoanEntity::class,
        ValuationEntryEntity::class,
        QuickEntryEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(CoreEnumConverters::class, PlanEnumConverters::class)
abstract class FintrackDatabase : RoomDatabase() {
    /** DOC-02: wallet table access. */
    abstract fun walletDao(): WalletDao

    /** DOC-02: category table access. */
    abstract fun categoryDao(): CategoryDao

    /** DOC-02: transaction table access. */
    abstract fun transactionDao(): TransactionDao

    /** DOC-02: budget table access. */
    abstract fun budgetDao(): BudgetDao

    /** DOC-02: budget alert log table access. */
    abstract fun budgetAlertLogDao(): BudgetAlertLogDao

    /** DOC-02: inbox table access. */
    abstract fun inboxItemDao(): InboxItemDao

    /** DOC-02: merchant rule table access. */
    abstract fun merchantRuleDao(): MerchantRuleDao

    /** DOC-02: monitored app table access. */
    abstract fun monitoredAppDao(): MonitoredAppDao

    /** DOC-02: installment plan table access. */
    abstract fun installmentPlanDao(): InstallmentPlanDao

    /** DOC-02: loan table access. */
    abstract fun loanDao(): LoanDao

    /** DOC-02: valuation table access. */
    abstract fun valuationEntryDao(): ValuationEntryDao

    /** DOC-02: quick entry table access. */
    abstract fun quickEntryDao(): QuickEntryDao

    /** DOC-11: whole-table reads for the backup export. */
    abstract fun backupReadDao(): BackupReadDao

    /** DOC-11: bulk inserts for a restore. */
    abstract fun backupWriteDao(): BackupWriteDao
}
