package com.meqlause.fintrack.core.model

/** DOC-02: whole rupiah in a Long, never Double or Float (NFR-015). Formatted only by the UI formatter. */
@JvmInline
public value class Money(
    public val rupiah: Long,
) : Comparable<Money> {
    /** DOC-02: true when the amount is exactly zero rupiah. */
    public val isZero: Boolean
        get() = rupiah == 0L

    /** DOC-02: sum of two amounts. */
    public operator fun plus(other: Money): Money = Money(rupiah + other.rupiah)

    /** DOC-02: difference of two amounts; may be negative for balances. */
    public operator fun minus(other: Money): Money = Money(rupiah - other.rupiah)

    /** DOC-02: orders amounts by their rupiah value. */
    override operator fun compareTo(other: Money): Int = rupiah.compareTo(other.rupiah)

    /** BR-001: true when 1 ≤ amount ≤ 999,999,999,999 rupiah. */
    public fun isTransactionAmount(): Boolean = rupiah in MIN_TRANSACTION_RUPIAH..MAX_TRANSACTION_RUPIAH

    /** BR-001: the bounds of a transaction amount. */
    public companion object {
        /** BR-001: smallest allowed transaction amount in rupiah. */
        public const val MIN_TRANSACTION_RUPIAH: Long = 1L

        /** BR-001: largest allowed transaction amount in rupiah. */
        public const val MAX_TRANSACTION_RUPIAH: Long = 999_999_999_999L
    }
}
