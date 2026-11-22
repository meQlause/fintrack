package com.meqlause.fintrack.feature.onboarding

import com.meqlause.fintrack.core.domain.onboarding.FirstWalletDraft
import com.meqlause.fintrack.core.domain.validation.WalletNameError
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.WalletType

/** FR-002: the first wallet as the user fills it on the wallet page; the balance is typed on the keypad. */
internal data class FirstWalletForm(
    val name: String,
    val type: WalletType,
    val balance: Money,
    val nameError: WalletNameError? = null,
) {
    /** FR-002: the wallet as the use case stores it. */
    fun toDraft(): FirstWalletDraft = FirstWalletDraft(name = name, type = type, initialBalance = balance)

    /** FR-002: appends typed digits; a balance that would exceed the amount limit (BR-001) ignores the digit. */
    fun withDigits(digits: String): FirstWalletForm = digits.fold(this) { form, digit -> form.appendDigit(digit) }

    /** FR-002: removes the last digit of the balance. */
    fun withBackspace(): FirstWalletForm = copy(balance = Money(balance.rupiah / DECIMAL_BASE))

    private fun appendDigit(digit: Char): FirstWalletForm {
        if (!digit.isDigit()) return this
        val next = balance.rupiah * DECIMAL_BASE + (digit - '0')
        return if (next > Money.MAX_TRANSACTION_RUPIAH) this else copy(balance = Money(next))
    }

    internal companion object {
        /** FR-002: the suggestion on a fresh install: "Tunai", cash, balance 0. */
        fun initial(): FirstWalletForm =
            FirstWalletForm(name = DEFAULT_NAME, type = WalletType.CASH, balance = Money(0L))

        private const val DEFAULT_NAME = "Tunai"
        private const val DECIMAL_BASE = 10L
    }
}
