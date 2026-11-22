package com.meqlause.fintrack.core.testing.data

import com.meqlause.fintrack.core.model.Agreement
import com.meqlause.fintrack.core.model.CategoryId
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
import com.meqlause.fintrack.core.model.MerchantRule
import com.meqlause.fintrack.core.model.MerchantRuleId
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.MoneyDirection
import com.meqlause.fintrack.core.model.MonitoredApp
import com.meqlause.fintrack.core.model.ParsedAmount
import com.meqlause.fintrack.core.model.Purchase
import com.meqlause.fintrack.core.model.QuickEntry
import com.meqlause.fintrack.core.model.QuickEntryId
import com.meqlause.fintrack.core.model.QuickTemplate
import com.meqlause.fintrack.core.model.RawNotice
import com.meqlause.fintrack.core.model.Resolution
import com.meqlause.fintrack.core.model.Schedule
import com.meqlause.fintrack.core.model.Suggestion
import com.meqlause.fintrack.core.model.TransactionId
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.ValuationEntry
import com.meqlause.fintrack.core.model.ValuationEntryId
import com.meqlause.fintrack.core.model.WalletId
import java.time.Instant
import java.time.YearMonth

/** DOC-02, FR-088: a sample merchant rule for a normalized merchant key. */
internal fun aMerchantRule(
    merchantKey: String,
    categoryId: CategoryId,
    walletId: WalletId? = null,
): MerchantRule =
    MerchantRule(
        merchantRuleId = MerchantRuleId(0L),
        merchantKey = merchantKey,
        categoryId = categoryId,
        walletId = walletId,
        hitCount = 3,
        audit = anAudit(),
    )

/** DOC-02, FR-090: a sample inbox item with a fingerprint and a posting time. */
internal fun anInboxItem(
    fingerprint: String,
    postedAt: Instant = anInstant(),
): InboxItem =
    InboxItem(
        inboxItemId = InboxItemId(0L),
        notice =
            RawNotice(
                packageName = "com.example.bank",
                parserId = "bank.debit.v1",
                rawTitle = "Transaksi Berhasil",
                rawText = "Pembayaran QRIS Rp25.000",
                postedAt = postedAt,
                fingerprint = fingerprint,
            ),
        parsed =
            ParsedAmount(
                amount = Money(rupiah = 25_000L),
                direction = MoneyDirection.DEBIT,
                merchant = "Indomaret",
            ),
        suggestion = Suggestion(suggestedWalletId = null, suggestedCategoryId = null, relatedInboxItemId = null),
        resolution = Resolution(status = InboxStatus.PENDING, transactionId = null, resolvedAt = null),
        audit = anAudit(),
    )

/** DOC-02, FR-088: a sample monitored app keyed by its package name. */
internal fun aMonitoredApp(
    packageName: String,
    defaultWalletId: WalletId? = null,
): MonitoredApp =
    MonitoredApp(
        packageName = packageName,
        displayName = "Bank Demo",
        isEnabled = true,
        defaultWalletId = defaultWalletId,
        lastNotificationAt = null,
    )

/** DOC-02, FR-140: a sample instalment plan on a credit wallet, split over [tenorMonths] statements. */
internal fun anInstallmentPlan(
    transactionId: TransactionId,
    walletId: WalletId,
    adminFeeTransactionId: TransactionId? = null,
    earlyPayoffMonth: YearMonth? = null,
): InstallmentPlan =
    InstallmentPlan(
        installmentPlanId = InstallmentPlanId(0L),
        purchase =
            Purchase(
                transactionId = transactionId,
                walletId = walletId,
                title = "Laptop",
                principal = Money(rupiah = 1_200_000L),
                adminFeeTransactionId = adminFeeTransactionId,
            ),
        schedule =
            Schedule(
                tenorMonths = 12,
                firstStatementMonth = YearMonth.parse("2026-12"),
                earlyPayoffMonth = earlyPayoffMonth,
            ),
        status = InstallmentStatus.ACTIVE,
        audit = anAudit(),
    )

/** DOC-02, FR-150: a sample open loan agreed with a person. */
internal fun aLoan(
    personName: String = "Budi",
    direction: LoanDirection = LoanDirection.LENT,
): Loan =
    Loan(
        loanId = LoanId(0L),
        agreement =
            Agreement(
                direction = direction,
                personName = personName,
                principal = Money(rupiah = 300_000L),
                startDate = anInstant(),
                dueDate = null,
                note = "",
            ),
        status = LoanStatus.OPEN,
        closedAt = null,
        audit = anAudit(),
    )

/** DOC-02, FR-172: a sample manual valuation of an investment wallet. */
internal fun aValuation(
    walletId: WalletId,
    valuedAt: Instant = anInstant(),
): ValuationEntry =
    ValuationEntry(
        valuationId = ValuationEntryId(0L),
        walletId = walletId,
        value = Money(rupiah = 1_500_000L),
        valuedAt = valuedAt,
        createdAt = anInstant(OFFSET_VALUATION_CREATED_MILLIS),
    )

/** DOC-02, FR-039: a sample quick entry that pre-fills an expense. */
internal fun aQuickEntry(
    categoryId: CategoryId,
    walletId: WalletId,
    sortOrder: Int = 0,
): QuickEntry =
    QuickEntry(
        quickEntryId = QuickEntryId(0L),
        template = QuickTemplate(title = "Kopi", type = TransactionType.EXPENSE, amount = Money(rupiah = 25_000L)),
        categoryId = categoryId,
        walletId = walletId,
        sortOrder = sortOrder,
        audit = anAudit(),
    )

private const val OFFSET_VALUATION_CREATED_MILLIS: Long = 500L
