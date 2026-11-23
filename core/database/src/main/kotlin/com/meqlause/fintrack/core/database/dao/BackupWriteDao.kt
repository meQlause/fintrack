package com.meqlause.fintrack.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
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

/** DOC-11: bulk inserts for a restore; rows keep the ids stored in the backup. Parents are inserted before children. */
@Dao
interface BackupWriteDao {
    /** DOC-11: inserts wallets. */
    @Insert
    suspend fun wallets(rows: List<WalletEntity>)

    /** DOC-11: inserts categories. */
    @Insert
    suspend fun categories(rows: List<CategoryEntity>)

    /** DOC-11: inserts loans. */
    @Insert
    suspend fun loans(rows: List<LoanEntity>)

    /** DOC-11: inserts inbox items. */
    @Insert
    suspend fun inboxItems(rows: List<InboxItemEntity>)

    /** DOC-11: inserts transactions. */
    @Insert
    suspend fun transactions(rows: List<TransactionEntity>)

    /** DOC-11: inserts budgets. */
    @Insert
    suspend fun budgets(rows: List<BudgetEntity>)

    /** DOC-11: inserts budget alert logs. */
    @Insert
    suspend fun budgetAlertLogs(rows: List<BudgetAlertLogEntity>)

    /** DOC-11: inserts merchant rules. */
    @Insert
    suspend fun merchantRules(rows: List<MerchantRuleEntity>)

    /** DOC-11: inserts monitored apps. */
    @Insert
    suspend fun monitoredApps(rows: List<MonitoredAppEntity>)

    /** DOC-11: inserts instalment plans. */
    @Insert
    suspend fun installmentPlans(rows: List<InstallmentPlanEntity>)

    /** DOC-11: inserts valuations. */
    @Insert
    suspend fun valuations(rows: List<ValuationEntryEntity>)

    /** DOC-11: inserts quick entries. */
    @Insert
    suspend fun quickEntries(rows: List<QuickEntryEntity>)
    /** DOC-11: removes quick entries. */
    @Query("DELETE FROM quick_entry")
    suspend fun clearQuickEntries()

    /** DOC-11: removes valuations. */
    @Query("DELETE FROM valuation_entry")
    suspend fun clearValuations()

    /** DOC-11: removes instalment plans. */
    @Query("DELETE FROM installment_plan")
    suspend fun clearInstallmentPlans()

    /** DOC-11: removes monitored apps. */
    @Query("DELETE FROM monitored_app")
    suspend fun clearMonitoredApps()

    /** DOC-11: removes merchant rules. */
    @Query("DELETE FROM merchant_rule")
    suspend fun clearMerchantRules()

    /** DOC-11: removes budget alert logs. */
    @Query("DELETE FROM budget_alert_log")
    suspend fun clearBudgetAlertLogs()

    /** DOC-11: removes budgets. */
    @Query("DELETE FROM budget")
    suspend fun clearBudgets()

    /** DOC-11: removes transactions. */
    @Query("DELETE FROM `transaction`")
    suspend fun clearTransactions()

    /** DOC-11: removes inbox items. */
    @Query("DELETE FROM inbox_item")
    suspend fun clearInboxItems()

    /** DOC-11: removes loans. */
    @Query("DELETE FROM loan")
    suspend fun clearLoans()

    /** DOC-11: removes categories. */
    @Query("DELETE FROM category")
    suspend fun clearCategories()

    /** DOC-11: removes wallets. */
    @Query("DELETE FROM wallet")
    suspend fun clearWallets()
}
