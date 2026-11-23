package com.meqlause.fintrack.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
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
}
