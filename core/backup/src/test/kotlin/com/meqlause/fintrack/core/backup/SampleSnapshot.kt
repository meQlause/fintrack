package com.meqlause.fintrack.core.backup

import com.meqlause.fintrack.core.model.Agreement
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.BackupAdjunct
import com.meqlause.fintrack.core.model.BackupLedger
import com.meqlause.fintrack.core.model.BackupSnapshot
import com.meqlause.fintrack.core.model.Budget
import com.meqlause.fintrack.core.model.BudgetAlertLog
import com.meqlause.fintrack.core.model.BudgetId
import com.meqlause.fintrack.core.model.Category
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.CategoryKind
import com.meqlause.fintrack.core.model.CategoryProfile
import com.meqlause.fintrack.core.model.CategoryState
import com.meqlause.fintrack.core.model.CreditTerms
import com.meqlause.fintrack.core.model.InboxItem
import com.meqlause.fintrack.core.model.InboxItemId
import com.meqlause.fintrack.core.model.InboxStatus
import com.meqlause.fintrack.core.model.InstallmentPlan
import com.meqlause.fintrack.core.model.InstallmentPlanId
import com.meqlause.fintrack.core.model.InstallmentStatus
import com.meqlause.fintrack.core.model.Loan
import com.meqlause.fintrack.core.model.LoanDirection
import com.meqlause.fintrack.core.model.LoanId
import com.meqlause.fintrack.core.model.LoanStatus
import com.meqlause.fintrack.core.model.Memo
import com.meqlause.fintrack.core.model.MerchantRule
import com.meqlause.fintrack.core.model.MerchantRuleId
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.MoneyDirection
import com.meqlause.fintrack.core.model.MonitoredApp
import com.meqlause.fintrack.core.model.Movement
import com.meqlause.fintrack.core.model.Origin
import com.meqlause.fintrack.core.model.ParsedAmount
import com.meqlause.fintrack.core.model.Preferences
import com.meqlause.fintrack.core.model.Purchase
import com.meqlause.fintrack.core.model.QuickEntry
import com.meqlause.fintrack.core.model.QuickEntryId
import com.meqlause.fintrack.core.model.QuickTemplate
import com.meqlause.fintrack.core.model.RawNotice
import com.meqlause.fintrack.core.model.Resolution
import com.meqlause.fintrack.core.model.Routing
import com.meqlause.fintrack.core.model.Schedule
import com.meqlause.fintrack.core.model.Suggestion
import com.meqlause.fintrack.core.model.ThemeMode
import com.meqlause.fintrack.core.model.Timing
import com.meqlause.fintrack.core.model.Transaction
import com.meqlause.fintrack.core.model.TransactionId
import com.meqlause.fintrack.core.model.TransactionSource
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.ValuationEntry
import com.meqlause.fintrack.core.model.ValuationEntryId
import com.meqlause.fintrack.core.model.Wallet
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.model.WalletKind
import com.meqlause.fintrack.core.model.WalletProfile
import com.meqlause.fintrack.core.model.WalletStatus
import com.meqlause.fintrack.core.model.WalletTerms
import com.meqlause.fintrack.core.model.WalletType
import java.time.Instant
import java.time.YearMonth

// DOC-11: a snapshot where every table holds one valid row with every foreign reference filled in
internal object SampleSnapshot {
    val fixedTime: Instant = Instant.parse("2026-11-22T08:00:00Z")
    private val audit = Audit(fixedTime, fixedTime)
    private val appearance = Appearance("wallet", "green")

    fun build(): BackupSnapshot =
        BackupSnapshot(
            createdAt = fixedTime,
            ledger = ledger(),
            adjunct = adjunct(),
            preferences = Preferences(themeMode = ThemeMode.DARK, periodStartDay = 25, hideAmountsByDefault = true),
            backupReminderDays = 7,
        )

    private fun ledger(): BackupLedger =
        BackupLedger(
            wallets = listOf(cash(), card()),
            categories = listOf(food()),
            transactions = listOf(expense(), transfer()),
            budgets = listOf(budget()),
            budgetAlertLogs = listOf(BudgetAlertLog(BudgetId(1), fixedTime, 80, fixedTime)),
            merchantRules = listOf(rule()),
        )

    private fun adjunct(): BackupAdjunct =
        BackupAdjunct(
            monitoredApps = listOf(MonitoredApp("com.example.pay", "Pay", true, WalletId(1), fixedTime)),
            inboxItems = listOf(inbox()),
            installmentPlans = listOf(plan()),
            loans = listOf(loan()),
            valuations = listOf(ValuationEntry(ValuationEntryId(1), WalletId(2), Money(750_000), fixedTime, fixedTime)),
            quickEntries = listOf(quick()),
        )

    private fun budget(): Budget = Budget(BudgetId(1), CategoryId(1), Money(500_000), true, fixedTime, fixedTime)

    private fun rule(): MerchantRule = MerchantRule(MerchantRuleId(1), "KOPI", CategoryId(1), WalletId(1), 3, audit)

    private fun quick(): QuickEntry =
        QuickEntry(
            QuickEntryId(1),
            QuickTemplate("Kopi", TransactionType.EXPENSE, Money(20_000)),
            CategoryId(1),
            WalletId(1),
            0,
            audit,
        )

    private fun cash(): Wallet =
        Wallet(
            WalletId(1),
            WalletKind(WalletType.CASH, null),
            WalletTerms(Money(100_000), null),
            WalletProfile("Tunai", appearance, 0),
            WalletStatus(),
            audit,
        )

    private fun card(): Wallet =
        Wallet(
            WalletId(2),
            WalletKind(WalletType.CREDIT_CARD, null),
            WalletTerms(Money(0), CreditTerms(Money(5_000_000), 25, 5)),
            WalletProfile("Kartu", appearance, 1),
            WalletStatus(),
            audit,
        )

    private fun food(): Category =
        Category(
            CategoryId(1),
            CategoryProfile("Makanan", CategoryKind.EXPENSE, appearance, 0),
            CategoryState(true, false),
            audit,
        )

    private fun expense(): Transaction =
        Transaction(
            TransactionId(1),
            Movement(TransactionType.EXPENSE, Money(25_000)),
            Routing(WalletId(1), null, CategoryId(1), null),
            Memo("Kopi", "KOPI"),
            Origin(TransactionSource.MANUAL, null),
            Timing(fixedTime, audit),
        )

    private fun transfer(): Transaction =
        Transaction(
            TransactionId(2),
            Movement(TransactionType.TRANSFER, Money(50_000), Money(1_000)),
            Routing(WalletId(1), WalletId(2), null, null),
            Memo(),
            Origin(TransactionSource.MANUAL, null),
            Timing(fixedTime, audit),
        )

    private fun inbox(): InboxItem =
        InboxItem(
            InboxItemId(1),
            RawNotice("com.example.pay", "parser", "Pay", "Pembayaran Rp25.000", fixedTime, "fingerprint"),
            ParsedAmount(Money(25_000), MoneyDirection.DEBIT, "KOPI"),
            Suggestion(WalletId(1), CategoryId(1), null),
            Resolution(InboxStatus.PENDING, null, null),
            audit,
        )

    private fun plan(): InstallmentPlan =
        InstallmentPlan(
            InstallmentPlanId(1),
            Purchase(TransactionId(1), WalletId(2), "Laptop", Money(25_000), null),
            Schedule(12, YearMonth.of(2026, 12), null),
            InstallmentStatus.ACTIVE,
            audit,
        )

    private fun loan(): Loan =
        Loan(
            LoanId(1),
            Agreement(LoanDirection.LENT, "Budi", Money(100_000), fixedTime, null, "catatan"),
            LoanStatus.OPEN,
            null,
            audit,
        )
}
