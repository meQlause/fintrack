package com.meqlause.fintrack.core.model

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import org.junit.jupiter.api.Test
import java.time.Instant

/** DOC-02: wallet, category, transaction and notification entities keep the fields they are built with. */
class LedgerEntitiesTest {
    private val moment = Instant.parse("2026-11-22T03:00:00Z")
    private val audit = Audit(createdAt = moment, updatedAt = moment)

    /** FR-010, BR-031: a credit wallet keeps its kind and credit terms together. */
    @Test
    fun `given credit wallet when built then kind and credit terms are kept (BR-031)`() {
        val credit = CreditTerms(creditLimit = Money(5_000_000L), statementDay = 25, dueDay = 5)
        val wallet =
            Wallet(
                walletId = WalletId(1L),
                kind = WalletKind(type = WalletType.CREDIT_CARD, investKind = null),
                terms = WalletTerms(initialBalance = Money(-1_000_000L), credit = credit),
                profile = WalletProfile(name = "BCA Card", appearance = Appearance("card", "blue"), sortOrder = 1),
                status = WalletStatus(),
                audit = audit,
            )

        assertThat(wallet.kind.type).isEqualTo(WalletType.CREDIT_CARD)
        assertThat(wallet.terms.credit).isEqualTo(credit)
    }

    /** DOC-02: an investment wallet names its investment kind. */
    @Test
    fun `given investment wallet when built then investment kind is kept (ADR-017)`() {
        val kind = WalletKind(type = WalletType.INVESTMENT, investKind = InvestKind.MUTUAL_FUND)

        assertThat(kind.investKind).isEqualTo(InvestKind.MUTUAL_FUND)
    }

    /** FR-004: a category keeps its profile and state. */
    @Test
    fun `given expense category when built then profile and state are kept (FR-004)`() {
        val profile = CategoryProfile("Makanan & Minuman", CategoryKind.EXPENSE, Appearance("food", "orange"), 1)
        val category =
            Category(
                categoryId = CategoryId(2L),
                profile = profile,
                state = CategoryState(isDefault = true, isArchived = false),
                audit = audit,
            )

        assertThat(category.profile.kind).isEqualTo(CategoryKind.EXPENSE)
        assertThat(category.state.isDefault).isEqualTo(true)
    }

    /** BR-004, BR-002: a transfer keeps both wallets and its fee, with no category. */
    @Test
    fun `given transfer when built then source target and fee are kept (BR-004)`() {
        val transaction =
            Transaction(
                transactionId = TransactionId(3L),
                movement = Movement(TransactionType.TRANSFER, Money(100_000L), Money(2_500L)),
                routing = Routing(WalletId(1L), WalletId(4L), categoryId = null, loanId = null),
                memo = Memo(note = "top up", merchant = ""),
                origin = Origin(TransactionSource.MANUAL, inboxItemId = null),
                timing = Timing(occurredAt = moment, audit = audit),
            )

        assertThat(transaction.movement.feeAmount).isEqualTo(Money(2_500L))
        assertThat(transaction.routing.targetWalletId).isEqualTo(WalletId(4L))
        assertThat(transaction.routing.categoryId).isNull()
    }

    /** BR-002, ADR-016: a loan repayment keeps its loan link and has no category. */
    @Test
    fun `given repayment when built then loan link is kept and category is empty (BR-002)`() {
        val routing = Routing(WalletId(1L), targetWalletId = null, categoryId = null, loanId = LoanId(9L))

        assertThat(routing.loanId).isEqualTo(LoanId(9L))
    }

    /** FR-091, BR-011: an inbox item keeps the notice, parsed amount and resolution it was built with. */
    @Test
    fun `given inbox item when built then notice parsed amount and resolution are kept (BR-011)`() {
        val notice =
            RawNotice("com.bca.mybca", "bca.debit.v1", "Transaksi Berhasil", "Pembayaran", moment, "fp-1")
        val inboxItem =
            InboxItem(
                inboxItemId = InboxItemId(5L),
                notice = notice,
                parsed = ParsedAmount(Money(25_000L), MoneyDirection.DEBIT, "INDOMARET"),
                suggestion = Suggestion(WalletId(1L), CategoryId(2L), relatedInboxItemId = null),
                resolution = Resolution(InboxStatus.PENDING, transactionId = null, resolvedAt = null),
                audit = audit,
            )

        assertThat(inboxItem.parsed.amount).isEqualTo(Money(25_000L))
        assertThat(inboxItem.resolution.status).isEqualTo(InboxStatus.PENDING)
    }

    /** FR-088, BR-012: a merchant rule remembers the category for a normalised merchant key. */
    @Test
    fun `given merchant rule when built then merchant key and category are kept (FR-088)`() {
        val rule =
            MerchantRule(
                merchantRuleId = MerchantRuleId(6L),
                merchantKey = "indomaret",
                categoryId = CategoryId(2L),
                walletId = null,
                hitCount = 0,
                audit = audit,
            )

        assertThat(rule.merchantKey).isEqualTo("indomaret")
        assertThat(rule.hitCount).isEqualTo(0)
    }

    /** FR-090: a monitored app keeps its package name and optional default wallet. */
    @Test
    fun `given monitored app when built then package and default wallet are kept (FR-090)`() {
        val monitored =
            MonitoredApp("com.gojek.app", "GoPay", isEnabled = true, WalletId(1L), lastNotificationAt = null)

        assertThat(monitored.packageName).isEqualTo("com.gojek.app")
        assertThat(monitored.defaultWalletId).isEqualTo(WalletId(1L))
    }

    /** FR-074: an overall budget has no category; a category budget keeps its limit. */
    @Test
    fun `given category budget when built then limit is kept and overall budget has no category (FR-074)`() {
        val overall = Budget(BudgetId(7L), categoryId = null, Money(3_000_000L), createdAt = moment, updatedAt = moment)
        val groceries = Budget(BudgetId(8L), CategoryId(2L), Money(500_000L), true, moment, moment)

        assertThat(overall.categoryId).isNull()
        assertThat(groceries.limitAmount).isEqualTo(Money(500_000L))
    }

    /** BR-021, FR-073: an alert log entry is keyed by budget, period start and threshold. */
    @Test
    fun `given budget alert log when built then period and threshold are kept (BR-021)`() {
        val alert = BudgetAlertLog(BudgetId(7L), periodStart = moment, threshold = 80, notifiedAt = moment)

        assertThat(alert.threshold).isEqualTo(80)
        assertThat(alert.periodStart).isEqualTo(moment)
    }
}
