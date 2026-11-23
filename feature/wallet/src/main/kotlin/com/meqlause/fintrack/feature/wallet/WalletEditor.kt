package com.meqlause.fintrack.feature.wallet

import com.meqlause.fintrack.core.domain.validation.WalletNameError
import com.meqlause.fintrack.core.model.InvestKind
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.model.WalletType

// FR-010, FR-011, FR-139, FR-170: the draft of the create and edit sheet; money fields are digits only
internal data class WalletEditor(
    val walletId: WalletId?,
    val name: String,
    val type: WalletType,
    val investKind: InvestKind,
    val initialBalance: String,
    val includeInTotal: Boolean,
    val creditLimit: String,
    val statementDay: Int,
    val dueDay: Int,
    val isArchived: Boolean,
    val nameError: WalletNameError?,
    val limitError: Boolean,
) {
    // FR-139: card and paylater wallets carry a limit and statement and due days
    val isCredit: Boolean
        get() = type == WalletType.CREDIT_CARD || type == WalletType.PAYLATER

    // FR-170: investment wallets name their kind
    val isInvestment: Boolean
        get() = type == WalletType.INVESTMENT

    companion object {
        private const val DEFAULT_STATEMENT_DAY = 25
        private const val DEFAULT_DUE_DAY = 10

        // FR-010: a new wallet starts as cash, with the prototype's day defaults for credit wallets
        fun forNew(): WalletEditor =
            WalletEditor(
                walletId = null,
                name = "",
                type = WalletType.CASH,
                investKind = InvestKind.MUTUAL_FUND,
                initialBalance = "",
                includeInTotal = true,
                creditLimit = "",
                statementDay = DEFAULT_STATEMENT_DAY,
                dueDay = DEFAULT_DUE_DAY,
                isArchived = false,
                nameError = null,
                limitError = false,
            )

        // FR-011: the draft of an existing wallet, with every field as it is stored
        fun of(item: WalletItem): WalletEditor {
            val wallet = item.wallet
            val credit = wallet.terms.credit
            return WalletEditor(
                walletId = wallet.walletId,
                name = wallet.profile.name,
                type = wallet.kind.type,
                investKind = wallet.kind.investKind ?: InvestKind.MUTUAL_FUND,
                initialBalance =
                    wallet.terms.initialBalance.rupiah
                        .toString(),
                includeInTotal = wallet.status.isIncludedInTotal,
                creditLimit =
                    credit
                        ?.creditLimit
                        ?.rupiah
                        ?.toString()
                        .orEmpty(),
                statementDay = credit?.statementDay ?: DEFAULT_STATEMENT_DAY,
                dueDay = credit?.dueDay ?: DEFAULT_DUE_DAY,
                isArchived = wallet.status.isArchived,
                nameError = null,
                limitError = false,
            )
        }
    }
}
