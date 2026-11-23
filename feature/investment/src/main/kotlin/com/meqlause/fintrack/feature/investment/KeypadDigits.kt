package com.meqlause.fintrack.feature.investment

import com.meqlause.fintrack.core.model.Money

// FR-172: the digits typed on the keypad; twelve digits is the largest amount a transaction can hold
internal object KeypadDigits {
    private const val MAX_DIGITS = 12

    // FR-172: a typed digit is appended, a leading zero is replaced
    fun append(
        digits: String,
        digit: String,
    ): String = if (digits == "0") digit.take(MAX_DIGITS) else (digits + digit).take(MAX_DIGITS)

    // FR-172: the last digit is removed; an empty value reads as zero
    fun backspace(digits: String): String = digits.dropLast(1)

    // FR-172: the amount in rupiah; a blank value is zero
    fun toMoney(digits: String): Money = Money(digits.toLongOrNull() ?: 0L)
}
