package com.meqlause.fintrack.core.database.dao

import androidx.room.Dao
import androidx.room.Query

/** DOC-11: clears the backed-up tables for a restore; children are cleared before the parents they reference. */
@Dao
interface BackupClearDao {
    /** DOC-11: removes quick entries. */
    @Query("DELETE FROM quick_entry")
    suspend fun quickEntries()

    /** DOC-11: removes valuations. */
    @Query("DELETE FROM valuation_entry")
    suspend fun valuations()

    /** DOC-11: removes instalment plans. */
    @Query("DELETE FROM installment_plan")
    suspend fun installmentPlans()

    /** DOC-11: removes monitored apps. */
    @Query("DELETE FROM monitored_app")
    suspend fun monitoredApps()

    /** DOC-11: removes merchant rules. */
    @Query("DELETE FROM merchant_rule")
    suspend fun merchantRules()

    /** DOC-11: removes budget alert logs. */
    @Query("DELETE FROM budget_alert_log")
    suspend fun budgetAlertLogs()

    /** DOC-11: removes budgets. */
    @Query("DELETE FROM budget")
    suspend fun budgets()

    /** DOC-11: removes transactions. */
    @Query("DELETE FROM `transaction`")
    suspend fun transactions()

    /** DOC-11: removes inbox items. */
    @Query("DELETE FROM inbox_item")
    suspend fun inboxItems()

    /** DOC-11: removes loans. */
    @Query("DELETE FROM loan")
    suspend fun loans()

    /** DOC-11: removes categories. */
    @Query("DELETE FROM category")
    suspend fun categories()

    /** DOC-11: removes wallets. */
    @Query("DELETE FROM wallet")
    suspend fun wallets()
}
