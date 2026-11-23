package com.meqlause.fintrack.core.model

/** DOC-11: the ledger tables of a backup: wallets, categories, transactions, budgets, alert logs, merchant rules. */
public data class BackupLedger(
    public val wallets: List<Wallet>,
    public val categories: List<Category>,
    public val transactions: List<Transaction>,
    public val budgets: List<Budget>,
    public val budgetAlertLogs: List<BudgetAlertLog>,
    public val merchantRules: List<MerchantRule>,
)
