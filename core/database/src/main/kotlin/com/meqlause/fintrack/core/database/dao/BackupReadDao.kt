package com.meqlause.fintrack.core.database.dao

import androidx.room.Dao
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

/** DOC-11: whole-table reads for the backup export; read inside one transaction for a consistent snapshot. */
@Dao
interface BackupReadDao {
    /** DOC-11: every wallet row. */
    @Query("SELECT * FROM wallet ORDER BY walletId")
    suspend fun wallets(): List<WalletEntity>

    /** DOC-11: every category row. */
    @Query("SELECT * FROM category ORDER BY categoryId")
    suspend fun categories(): List<CategoryEntity>

    /** DOC-11: every transaction row. */
    @Query("SELECT * FROM `transaction` ORDER BY transactionId")
    suspend fun transactions(): List<TransactionEntity>

    /** DOC-11: every budget row. */
    @Query("SELECT * FROM budget ORDER BY budgetId")
    suspend fun budgets(): List<BudgetEntity>

    /** DOC-11: every budget alert log row. */
    @Query("SELECT * FROM budget_alert_log ORDER BY notifiedAt")
    suspend fun budgetAlertLogs(): List<BudgetAlertLogEntity>

    /** DOC-11: every merchant rule row. */
    @Query("SELECT * FROM merchant_rule ORDER BY merchantRuleId")
    suspend fun merchantRules(): List<MerchantRuleEntity>

    /** DOC-11: every monitored app row. */
    @Query("SELECT * FROM monitored_app ORDER BY packageName")
    suspend fun monitoredApps(): List<MonitoredAppEntity>

    /** DOC-11: only the PENDING inbox items, raw text included. */
    @Query("SELECT * FROM inbox_item WHERE status = 'PENDING' ORDER BY inboxItemId")
    suspend fun pendingInboxItems(): List<InboxItemEntity>

    /** DOC-11: every instalment plan row. */
    @Query("SELECT * FROM installment_plan ORDER BY installmentPlanId")
    suspend fun installmentPlans(): List<InstallmentPlanEntity>

    /** DOC-11: every loan row. */
    @Query("SELECT * FROM loan ORDER BY loanId")
    suspend fun loans(): List<LoanEntity>

    /** DOC-11: every valuation row. */
    @Query("SELECT * FROM valuation_entry ORDER BY valuationId")
    suspend fun valuations(): List<ValuationEntryEntity>

    /** DOC-11: every quick entry row. */
    @Query("SELECT * FROM quick_entry ORDER BY quickEntryId")
    suspend fun quickEntries(): List<QuickEntryEntity>
}
