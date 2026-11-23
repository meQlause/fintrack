package com.meqlause.fintrack.core.domain.transaction

import com.meqlause.fintrack.core.model.Category
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.CategoryKind
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.Wallet
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.model.WalletType
import java.time.Instant
import java.time.temporal.ChronoUnit

/** BR-001..BR-004, BR-006, BR-009, BR-039: the checks a draft passes before anything is stored. */
internal object TransactionRules {
    private const val MAX_FUTURE_DAYS = 1L

    /** Every broken rule; an empty set means the draft may be stored. */
    fun check(
        draft: TransactionDraft,
        wallets: Map<WalletId, Wallet>,
        categories: Map<CategoryId, Category>,
        currentTime: Instant,
    ): Set<TransactionError> =
        buildSet {
            addAll(amountErrors(draft.movement.amount))
            addAll(feeErrors(draft))
            addAll(routeErrors(draft, wallets, categories))
            if (draft.occurredAt.isAfter(currentTime.plus(MAX_FUTURE_DAYS, ChronoUnit.DAYS))) {
                add(TransactionError.DATE_TOO_FAR)
            }
        }

    // BR-001: 1 ≤ amount ≤ 999,999,999,999
    private fun amountErrors(amount: Money): Set<TransactionError> =
        when {
            amount.isTransactionAmount() -> emptySet()
            amount.rupiah < Money.MIN_TRANSACTION_RUPIAH -> setOf(TransactionError.AMOUNT_REQUIRED)
            else -> setOf(TransactionError.AMOUNT_TOO_LARGE)
        }

    // BR-004: the fee is only used by TRANSFER and is deducted from the source
    private fun feeErrors(draft: TransactionDraft): Set<TransactionError> {
        val fee = draft.movement.feeAmount.rupiah
        val isTransfer = draft.movement.type == TransactionType.TRANSFER
        val isValid = fee in 0L..Money.MAX_TRANSACTION_RUPIAH
        return if (isTransfer && !isValid) setOf(TransactionError.FEE_INVALID) else emptySet()
    }

    private fun routeErrors(
        draft: TransactionDraft,
        wallets: Map<WalletId, Wallet>,
        categories: Map<CategoryId, Category>,
    ): Set<TransactionError> =
        when (draft.movement.type) {
            TransactionType.TRANSFER -> transferErrors(draft.routing, wallets)
            TransactionType.EXPENSE -> ledgerErrors(CategoryKind.EXPENSE, draft.routing, wallets, categories)
            TransactionType.INCOME -> ledgerErrors(CategoryKind.INCOME, draft.routing, wallets, categories)
            else -> emptySet()
        }

    // BR-003, BR-039: a transfer needs two different wallets and a non-credit source
    private fun transferErrors(
        routing: DraftRouting,
        wallets: Map<WalletId, Wallet>,
    ): Set<TransactionError> =
        buildSet {
            val source = routing.walletId?.let(wallets::get)
            if (source == null) add(TransactionError.WALLET_REQUIRED)
            if (routing.targetWalletId == null) add(TransactionError.TARGET_WALLET_REQUIRED)
            if (routing.walletId != null && routing.walletId == routing.targetWalletId) {
                add(TransactionError.SAME_WALLET)
            }
            if (source != null && source.kind.type.isCredit()) add(TransactionError.CREDIT_SOURCE_NOT_ALLOWED)
        }

    // BR-002, BR-006: an expense or income needs a wallet and a category of its own kind
    private fun ledgerErrors(
        kind: CategoryKind,
        routing: DraftRouting,
        wallets: Map<WalletId, Wallet>,
        categories: Map<CategoryId, Category>,
    ): Set<TransactionError> =
        buildSet {
            if (routing.walletId?.let(wallets::get) == null) add(TransactionError.WALLET_REQUIRED)
            val category = routing.categoryId?.let(categories::get)
            when {
                category == null -> add(TransactionError.CATEGORY_REQUIRED)
                category.profile.kind != kind -> add(TransactionError.CATEGORY_KIND_MISMATCH)
            }
        }

    // BR-031: the credit wallets are CREDIT_CARD and PAYLATER
    private fun WalletType.isCredit(): Boolean = this == WalletType.CREDIT_CARD || this == WalletType.PAYLATER
}
