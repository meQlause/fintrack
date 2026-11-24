package com.meqlause.fintrack.core.database

import com.meqlause.fintrack.core.database.entity.AppearanceColumns
import com.meqlause.fintrack.core.database.entity.AuditColumns
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
import com.meqlause.fintrack.core.model.CategoryKind
import com.meqlause.fintrack.core.model.InboxStatus
import com.meqlause.fintrack.core.model.InstallmentStatus
import com.meqlause.fintrack.core.model.LoanDirection
import com.meqlause.fintrack.core.model.LoanStatus
import com.meqlause.fintrack.core.model.MoneyDirection
import com.meqlause.fintrack.core.model.TransactionSource
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.WalletType

// DOC-02: minimal valid rows for the DAO tests; ids of 0 let Room assign them
private const val SAMPLE_TIME = 1_700_000_000_000L

internal fun sampleWallet(
    name: String = "Dompet",
    initialBalance: Long = 0L,
): WalletEntity =
    WalletEntity(
        identity = WalletEntity.Identity(name, WalletType.CASH, initialBalance),
        appearance = AppearanceColumns("wallet", "teal"),
        audit = AuditColumns(SAMPLE_TIME, SAMPLE_TIME),
    )

internal fun sampleCategory(name: String = "Makan"): CategoryEntity =
    CategoryEntity(
        identity = CategoryEntity.Identity(name, CategoryKind.EXPENSE),
        appearance = AppearanceColumns("food", "orange"),
        audit = AuditColumns(SAMPLE_TIME, SAMPLE_TIME),
    )

internal fun sampleTransaction(
    walletId: Long,
    categoryId: Long? = null,
): TransactionEntity =
    TransactionEntity(
        figures = TransactionEntity.Amounts(amount = 25_000L),
        accounts = TransactionEntity.Accounts(walletId = walletId, categoryId = categoryId),
        details = TransactionEntity.Details(TransactionType.EXPENSE, occurredAt = SAMPLE_TIME),
        origin = TransactionEntity.Origin(TransactionSource.MANUAL),
        audit = AuditColumns(SAMPLE_TIME, SAMPLE_TIME),
    )

internal fun sampleBudget(categoryId: Long?): BudgetEntity =
    BudgetEntity(categoryId = categoryId, limitAmount = 500_000L, audit = AuditColumns(SAMPLE_TIME, SAMPLE_TIME))

internal fun sampleBudgetAlert(budgetId: Long): BudgetAlertLogEntity =
    BudgetAlertLogEntity(budgetId = budgetId, periodStart = SAMPLE_TIME, threshold = 80, notifiedAt = SAMPLE_TIME)

internal fun sampleInboxItem(fingerprint: String = "fp-1"): InboxItemEntity =
    InboxItemEntity(
        source = InboxItemEntity.Source("com.bank.app", "bca.debit.v1", SAMPLE_TIME, "BCA", "Transfer"),
        parsed = InboxItemEntity.Parsed(25_000L, MoneyDirection.DEBIT, "Kopi"),
        lifecycle = InboxItemEntity.Lifecycle(InboxStatus.PENDING, fingerprint),
        audit = AuditColumns(SAMPLE_TIME, SAMPLE_TIME),
    )

internal fun sampleMerchantRule(categoryId: Long): MerchantRuleEntity =
    MerchantRuleEntity(
        merchantKey = "kopi kenangan",
        categoryId = categoryId,
        walletId = null,
        audit = AuditColumns(SAMPLE_TIME, SAMPLE_TIME),
    )

internal fun sampleMonitoredApp(packageName: String = "com.bank.app"): MonitoredAppEntity =
    MonitoredAppEntity(
        packageName = packageName,
        displayName = "Bank",
        isEnabled = true,
        defaultWalletId = null,
        lastNotificationAt = null,
    )

internal fun sampleInstallmentPlan(
    transactionId: Long,
    walletId: Long,
): InstallmentPlanEntity =
    InstallmentPlanEntity(
        purchase = InstallmentPlanEntity.Purchase(transactionId, walletId, "Laptop", 6_000_000L),
        schedule = InstallmentPlanEntity.Schedule(tenorMonths = 6, firstStatementMonth = "2026-12"),
        adminFeeTransactionId = null,
        status = InstallmentStatus.ACTIVE,
        audit = AuditColumns(SAMPLE_TIME, SAMPLE_TIME),
    )

internal fun sampleLoan(): LoanEntity =
    LoanEntity(
        terms = LoanEntity.Terms(LoanDirection.LENT, "Budi", 100_000L, SAMPLE_TIME, dueDate = null, note = ""),
        status = LoanStatus.OPEN,
        closedAt = null,
        audit = AuditColumns(SAMPLE_TIME, SAMPLE_TIME),
    )

internal fun sampleValuation(walletId: Long): ValuationEntryEntity =
    ValuationEntryEntity(walletId = walletId, value = 1_200_000L, valuedAt = SAMPLE_TIME, createdAt = SAMPLE_TIME)

internal fun sampleQuickEntry(
    categoryId: Long,
    walletId: Long,
): QuickEntryEntity =
    QuickEntryEntity(
        title = "Kopi pagi",
        note = "",
        sortOrder = 0,
        target = QuickEntryEntity.Target(TransactionType.EXPENSE, 18_000L, categoryId, walletId),
        audit = AuditColumns(SAMPLE_TIME, SAMPLE_TIME),
    )

internal fun sampleMovement(
    type: TransactionType,
    accounts: TransactionEntity.Accounts,
    figures: TransactionEntity.Amounts,
): TransactionEntity =
    TransactionEntity(
        figures = figures,
        accounts = accounts,
        details = TransactionEntity.Details(type, occurredAt = SAMPLE_TIME),
        origin = TransactionEntity.Origin(TransactionSource.MANUAL),
        audit = AuditColumns(SAMPLE_TIME, SAMPLE_TIME),
    )
