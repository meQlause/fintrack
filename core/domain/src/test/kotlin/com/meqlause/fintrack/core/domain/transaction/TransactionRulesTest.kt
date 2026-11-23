package com.meqlause.fintrack.core.domain.transaction

import assertk.assertThat
import assertk.assertions.containsOnly
import assertk.assertions.isEmpty
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.Category
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.CategoryKind
import com.meqlause.fintrack.core.model.CategoryProfile
import com.meqlause.fintrack.core.model.CategoryState
import com.meqlause.fintrack.core.model.CreditTerms
import com.meqlause.fintrack.core.model.Memo
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Movement
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.Wallet
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.model.WalletKind
import com.meqlause.fintrack.core.model.WalletProfile
import com.meqlause.fintrack.core.model.WalletStatus
import com.meqlause.fintrack.core.model.WalletTerms
import com.meqlause.fintrack.core.model.WalletType
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.temporal.ChronoUnit

/** BR-001..BR-004, BR-006, BR-009, BR-039: the draft checks run before anything is stored. */
class TransactionRulesTest {
    private val current = Instant.parse("2026-11-23T10:00:00Z")
    private val cash = WalletId(1L)
    private val bank = WalletId(2L)
    private val card = WalletId(3L)
    private val food = CategoryId(1L)
    private val salary = CategoryId(2L)
    private val wallets =
        listOf(wallet(cash, WalletType.CASH), wallet(bank, WalletType.BANK), wallet(card, WalletType.CREDIT_CARD))
            .associateBy { it.walletId }
    private val categories =
        listOf(category(food, CategoryKind.EXPENSE), category(salary, CategoryKind.INCOME))
            .associateBy { it.categoryId }

    /** BR-002: a valid expense from a cash wallet in an expense category has no broken rule. */
    @Test
    fun `given a valid expense when checked then no rule is broken`() {
        val result = check(draft(TransactionType.EXPENSE, 50_000L, DraftRouting(cash, null, food)))
        assertThat(result).isEmpty()
    }

    /** BR-001: a zero amount is required to be at least one rupiah. */
    @Test
    fun `given a zero amount when checked then the amount is required`() {
        val result = check(draft(TransactionType.EXPENSE, 0L, DraftRouting(cash, null, food)))
        assertThat(result).containsOnly(TransactionError.AMOUNT_REQUIRED)
    }

    /** BR-001: an amount above 999,999,999,999 rupiah is too large. */
    @Test
    fun `given an amount above the bound when checked then the amount is too large`() {
        val result = check(draft(TransactionType.EXPENSE, 1_000_000_000_000L, DraftRouting(cash, null, food)))
        assertThat(result).containsOnly(TransactionError.AMOUNT_TOO_LARGE)
    }

    /** BR-004: a transfer fee above the bound is invalid. */
    @Test
    fun `given a transfer fee above the bound when checked then the fee is invalid`() {
        val feeAmount = 1_000_000_000_000L
        val candidate =
            TransactionDraft(null, transferWithFee(feeAmount), DraftRouting(bank, cash, null), Memo(), current)
        val result = check(candidate)
        assertThat(result).containsOnly(TransactionError.FEE_INVALID)
    }

    /** BR-004: a fee on an expense is not checked, only a transfer uses it. */
    @Test
    fun `given a negative fee on an expense when checked then the fee is not checked`() {
        val candidate =
            TransactionDraft(
                null,
                Movement(TransactionType.EXPENSE, Money(10_000L), Money(-1L)),
                DraftRouting(cash, null, food),
                Memo(),
                current,
            )
        val result = check(candidate)
        assertThat(result).isEmpty()
    }

    /** BR-003: a transfer needs a source and a target wallet. */
    @Test
    fun `given a transfer without wallets when checked then both wallets are required`() {
        val result = check(draft(TransactionType.TRANSFER, 10_000L, DraftRouting(null, null, null)))
        assertThat(result).containsOnly(TransactionError.WALLET_REQUIRED, TransactionError.TARGET_WALLET_REQUIRED)
    }

    /** BR-003: a transfer must move money between two different wallets. */
    @Test
    fun `given a transfer to the same wallet when checked then it is refused`() {
        val result = check(draft(TransactionType.TRANSFER, 10_000L, DraftRouting(bank, bank, null)))
        assertThat(result).containsOnly(TransactionError.SAME_WALLET)
    }

    /** BR-039: a credit wallet cannot be the source of a transfer. */
    @Test
    fun `given a transfer from a credit wallet when checked then the source is refused`() {
        val result = check(draft(TransactionType.TRANSFER, 10_000L, DraftRouting(card, bank, null)))
        assertThat(result).containsOnly(TransactionError.CREDIT_SOURCE_NOT_ALLOWED)
    }

    /** BR-002: a transfer from a wallet that does not exist is refused as a missing source. */
    @Test
    fun `given a transfer from an unknown wallet when checked then the wallet is required`() {
        val result = check(draft(TransactionType.TRANSFER, 10_000L, DraftRouting(WalletId(99L), bank, null)))
        assertThat(result).containsOnly(TransactionError.WALLET_REQUIRED)
    }

    /** BR-002: an expense needs a wallet and a category. */
    @Test
    fun `given an expense without wallet or category when checked then both are required`() {
        val result = check(draft(TransactionType.EXPENSE, 10_000L, DraftRouting(null, null, null)))
        assertThat(result).containsOnly(TransactionError.WALLET_REQUIRED, TransactionError.CATEGORY_REQUIRED)
    }

    /** BR-006: an expense cannot use an income category. */
    @Test
    fun `given an expense in an income category when checked then the kind mismatches`() {
        val result = check(draft(TransactionType.EXPENSE, 10_000L, DraftRouting(cash, null, salary)))
        assertThat(result).containsOnly(TransactionError.CATEGORY_KIND_MISMATCH)
    }

    /** BR-006: an income needs a category that exists. */
    @Test
    fun `given an income in an unknown category when checked then the category is required`() {
        val result = check(draft(TransactionType.INCOME, 10_000L, DraftRouting(cash, null, CategoryId(77L))))
        assertThat(result).containsOnly(TransactionError.CATEGORY_REQUIRED)
    }

    /** BR-009: a transaction dated more than one day ahead is refused. */
    @Test
    fun `given a date two days ahead when checked then the date is too far`() {
        val later = current.plus(2L, ChronoUnit.DAYS)
        val result = check(draft(TransactionType.EXPENSE, 10_000L, DraftRouting(cash, null, food), occurredAt = later))
        assertThat(result).containsOnly(TransactionError.DATE_TOO_FAR)
    }

    /** BR-009: a date exactly one day ahead is still accepted. */
    @Test
    fun `given a date one day ahead when checked then it is accepted`() {
        val tomorrow = current.plus(1L, ChronoUnit.DAYS)
        val draft = draft(TransactionType.EXPENSE, 10_000L, DraftRouting(cash, null, food), occurredAt = tomorrow)
        assertThat(check(draft)).isEmpty()
    }

    /** BR-040: a loan movement has no wallet or category rule of its own here. */
    @Test
    fun `given a loan movement without routing when checked then no route rule applies`() {
        val result = check(draft(TransactionType.LEND, 10_000L, DraftRouting(null, null, null)))
        assertThat(result).isEmpty()
    }

    private fun check(draft: TransactionDraft): Set<TransactionError> =
        TransactionRules.check(draft, wallets, categories, current)

    private fun draft(
        type: TransactionType,
        amount: Long,
        routing: DraftRouting,
        occurredAt: Instant = current,
    ): TransactionDraft = TransactionDraft(null, Movement(type, Money(amount), Money(0L)), routing, Memo(), occurredAt)

    private fun transferWithFee(feeAmount: Long): Movement =
        Movement(TransactionType.TRANSFER, Money(10_000L), Money(feeAmount))

    private fun wallet(
        walletId: WalletId,
        type: WalletType,
    ): Wallet =
        Wallet(
            walletId = walletId,
            kind = WalletKind(type, investKind = null),
            terms = WalletTerms(Money(0L), credit = creditOf(type)),
            profile = WalletProfile("Dompet", Appearance("wallet", "green"), 0),
            status = WalletStatus(),
            audit = Audit(current, current),
        )

    private fun creditOf(type: WalletType): CreditTerms? =
        if (type == WalletType.CREDIT_CARD) CreditTerms(Money(5_000_000L), statementDay = 25, dueDay = 10) else null

    private fun category(
        categoryId: CategoryId,
        kind: CategoryKind,
    ): Category =
        Category(
            categoryId = categoryId,
            profile = CategoryProfile("Kategori", kind, Appearance("tag", "blue"), 0),
            state = CategoryState(isDefault = false, isArchived = false),
            audit = Audit(current, current),
        )
}
