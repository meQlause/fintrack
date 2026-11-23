package com.meqlause.fintrack.feature.wallet

import com.meqlause.fintrack.core.domain.validation.WalletNameError
import com.meqlause.fintrack.core.model.InvestKind
import com.meqlause.fintrack.core.model.Wallet
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.model.WalletType

// FR-010, FR-011, FR-139, FR-170: the draft of the create and edit sheet; money fields are digits only
internal data class WalletEditor(
    val identity: EditorIdentity,
    val kind: EditorKind,
    val money: EditorMoney,
    val days: EditorDays,
    val errors: EditorErrors = EditorErrors(),
) {
    val walletId: WalletId? get() = identity.walletId
    val name: String get() = identity.name
    val isArchived: Boolean get() = identity.isArchived
    val type: WalletType get() = kind.type
    val investKind: InvestKind get() = kind.investKind
    val initialBalance: String get() = money.initialBalance
    val includeInTotal: Boolean get() = money.includeInTotal
    val creditLimit: String get() = money.creditLimit
    val statementDay: Int get() = days.statementDay
    val dueDay: Int get() = days.dueDay
    val nameError: WalletNameError? get() = errors.nameError
    val limitError: Boolean get() = errors.limitError

    // FR-139: card and paylater wallets carry a limit and statement and due days
    val isCredit: Boolean
        get() = type == WalletType.CREDIT_CARD || type == WalletType.PAYLATER

    // FR-170: investment wallets name their kind
    val isInvestment: Boolean
        get() = type == WalletType.INVESTMENT

    // FR-010: typing the name clears the name error
    fun withName(text: String): WalletEditor =
        copy(identity = identity.copy(name = text), errors = errors.copy(nameError = null))

    // FR-010: the opening balance, digits only
    fun withInitialBalance(typed: String): WalletEditor = copy(money = money.copy(initialBalance = digitsOnly(typed)))

    // FR-139: typing the limit clears the limit error
    fun withCreditLimit(typed: String): WalletEditor =
        copy(money = money.copy(creditLimit = digitsOnly(typed)), errors = errors.copy(limitError = false))

    // FR-013: whether the wallet counts in the total
    fun withIncludeInTotal(include: Boolean): WalletEditor = copy(money = money.copy(includeInTotal = include))

    // FR-010: the wallet type
    fun withType(picked: WalletType): WalletEditor = copy(kind = kind.copy(type = picked))

    // FR-170: the investment kind
    fun withInvestKind(picked: InvestKind): WalletEditor = copy(kind = kind.copy(investKind = picked))

    // FR-139: the statement or due day, depending on which drawer was open
    fun withDay(
        picker: WalletPicker?,
        pickedDay: Int,
    ): WalletEditor =
        when (picker) {
            WalletPicker.STATEMENT_DAY -> copy(days = days.copy(statementDay = pickedDay))
            WalletPicker.DUE_DAY -> copy(days = days.copy(dueDay = pickedDay))
            else -> this
        }

    // FR-010: the errors the save found
    fun withErrors(
        nameProblem: WalletNameError?,
        limitProblem: Boolean,
    ): WalletEditor = copy(errors = EditorErrors(nameError = nameProblem, limitError = limitProblem))

    companion object {
        private const val DEFAULT_STATEMENT_DAY = 25
        private const val DEFAULT_DUE_DAY = 10
        private const val MAX_DIGITS = 12

        // FR-010: money fields keep digits only, at most twelve of them
        private fun digitsOnly(typed: String): String = typed.filter { it.isDigit() }.take(MAX_DIGITS)

        // FR-010: a new wallet starts as cash, with the prototype's day defaults for credit wallets
        fun forNew(): WalletEditor =
            WalletEditor(
                identity = EditorIdentity(walletId = null, name = "", isArchived = false),
                kind = EditorKind(type = WalletType.CASH, investKind = InvestKind.MUTUAL_FUND),
                money = EditorMoney(initialBalance = "", includeInTotal = true, creditLimit = ""),
                days = EditorDays(statementDay = DEFAULT_STATEMENT_DAY, dueDay = DEFAULT_DUE_DAY),
            )

        // FR-011: the draft of an existing wallet, with every field as it is stored
        fun forWallet(item: WalletItem): WalletEditor {
            val wallet = item.wallet
            val credit = wallet.terms.credit
            return WalletEditor(
                identity = EditorIdentity(wallet.walletId, wallet.profile.name, wallet.status.isArchived),
                kind = EditorKind(wallet.kind.type, wallet.kind.investKind ?: InvestKind.MUTUAL_FUND),
                money = moneyOf(wallet),
                days = EditorDays(credit?.statementDay ?: DEFAULT_STATEMENT_DAY, credit?.dueDay ?: DEFAULT_DUE_DAY),
            )
        }

        // FR-011: the stored amounts as the digits the fields show
        private fun moneyOf(wallet: Wallet): EditorMoney =
            EditorMoney(
                initialBalance =
                    wallet.terms.initialBalance.rupiah
                        .toString(),
                includeInTotal = wallet.status.isIncludedInTotal,
                creditLimit =
                    wallet.terms.credit
                        ?.creditLimit
                        ?.rupiah
                        ?.toString()
                        .orEmpty(),
            )
    }
}

// FR-011: which wallet the draft is, its name and whether it is archived
internal data class EditorIdentity(
    val walletId: WalletId?,
    val name: String,
    val isArchived: Boolean,
)

// FR-010, FR-170: the wallet type and, for an investment, its kind
internal data class EditorKind(
    val type: WalletType,
    val investKind: InvestKind,
)

// FR-010, FR-013, FR-139: the typed amounts and whether the wallet counts in the total
internal data class EditorMoney(
    val initialBalance: String,
    val includeInTotal: Boolean,
    val creditLimit: String,
)

// FR-139: the statement day and the due day of a credit wallet
internal data class EditorDays(
    val statementDay: Int,
    val dueDay: Int,
)

// FR-010, FR-139: the problems the last save found
internal data class EditorErrors(
    val nameError: WalletNameError? = null,
    val limitError: Boolean = false,
)
