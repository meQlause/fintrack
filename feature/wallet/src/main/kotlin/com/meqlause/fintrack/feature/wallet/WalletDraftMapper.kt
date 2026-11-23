package com.meqlause.fintrack.feature.wallet

import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.CreditTerms
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Wallet
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.model.WalletKind
import com.meqlause.fintrack.core.model.WalletProfile
import com.meqlause.fintrack.core.model.WalletStatus
import com.meqlause.fintrack.core.model.WalletTerms
import java.time.Instant

// FR-010, FR-011: turns the editor draft into the wallet model the repository stores
internal object WalletDraftMapper {
    // FR-010: a new wallet gets the default appearance of its type and the next place in the manual order
    fun newWallet(
        draft: WalletEditor,
        sortOrder: Int,
        currentTime: Instant,
    ): Wallet =
        Wallet(
            walletId = WalletId(0L),
            kind = kindOf(draft),
            terms = termsOf(draft),
            profile =
                WalletProfile(
                    name = draft.name.trim(),
                    appearance = WalletAppearance.of(draft.type),
                    sortOrder = sortOrder,
                ),
            status = WalletStatus(isIncludedInTotal = draft.includeInTotal),
            audit = Audit(createdAt = currentTime, updatedAt = currentTime),
        )

    // FR-011: an edit keeps the id, appearance, order, archive state and creation time of the stored wallet
    fun applyTo(
        wallet: Wallet,
        draft: WalletEditor,
        currentTime: Instant,
    ): Wallet =
        wallet.copy(
            kind = kindOf(draft),
            terms = termsOf(draft),
            profile = wallet.profile.copy(name = draft.name.trim()),
            status = wallet.status.copy(isIncludedInTotal = draft.includeInTotal),
            audit = wallet.audit.copy(updatedAt = currentTime),
        )

    // FR-170: the investment kind applies to investment wallets only
    private fun kindOf(draft: WalletEditor): WalletKind =
        WalletKind(
            type = draft.type,
            investKind = if (draft.isInvestment) draft.investKind else null,
        )

    // FR-010: the opening balance (FR-011 recalculates balances from it) and the credit terms, if any
    private fun termsOf(draft: WalletEditor): WalletTerms =
        WalletTerms(
            initialBalance = Money(draft.initialBalance.toLongOrNull() ?: 0L),
            credit = creditOf(draft),
        )

    // FR-139: card and paylater wallets store their limit and days; a missing limit is rejected before this point
    private fun creditOf(draft: WalletEditor): CreditTerms? {
        if (!draft.isCredit) return null
        return CreditTerms(
            creditLimit = Money(draft.creditLimit.toLongOrNull() ?: 0L),
            statementDay = draft.statementDay,
            dueDay = draft.dueDay,
        )
    }
}
