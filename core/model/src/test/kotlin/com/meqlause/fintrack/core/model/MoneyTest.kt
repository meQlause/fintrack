package com.meqlause.fintrack.core.model

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isGreaterThan
import assertk.assertions.isLessThan
import assertk.assertions.isTrue
import org.junit.jupiter.api.Test

/** DOC-02: Money is whole rupiah in a Long; BR-001 bounds the transaction amount. */
class MoneyTest {
    /** BR-001: adding two amounts gives their exact sum. */
    @Test
    fun `given two amounts when added then sum is exact (BR-001)`() {
        val first = Money(rupiah = 25_000L)
        val second = Money(rupiah = 1_500L)

        val total = first + second

        assertThat(total).isEqualTo(Money(rupiah = 26_500L))
    }

    /** BR-001: subtracting two amounts gives their exact difference. */
    @Test
    fun `given two amounts when subtracted then difference is exact (BR-001)`() {
        val larger = Money(rupiah = 100_000L)
        val smaller = Money(rupiah = 40_000L)

        val difference = larger - smaller

        assertThat(difference).isEqualTo(Money(rupiah = 60_000L))
    }

    /** BR-031: a balance can go negative when money out exceeds money in. */
    @Test
    fun `given a smaller amount subtracted from a larger one when read then result is negative (BR-031)`() {
        val balance = Money(rupiah = 10_000L) - Money(rupiah = 15_000L)

        assertThat(balance).isEqualTo(Money(rupiah = -5_000L))
    }

    /** BR-001: amounts compare by their rupiah value. */
    @Test
    fun `given two amounts when compared then order follows the rupiah value (BR-001)`() {
        val smallAmount = Money(rupiah = 1_000L)
        val biggerAmount = Money(rupiah = 2_000L)

        assertThat(smallAmount).isLessThan(biggerAmount)
        assertThat(biggerAmount).isGreaterThan(smallAmount)
        assertThat(smallAmount.compareTo(Money(rupiah = 1_000L))).isEqualTo(0)
    }

    /** DOC-02: zero rupiah reports as zero. */
    @Test
    fun `given zero amount when checked then it is zero`() {
        assertThat(Money(rupiah = 0L).isZero).isTrue()
    }

    /** DOC-02: any non-zero amount does not report as zero. */
    @Test
    fun `given non-zero amount when checked then it is not zero`() {
        assertThat(Money(rupiah = 1L).isZero).isFalse()
    }

    /** BR-001: the minimum amount of one rupiah is accepted. */
    @Test
    fun `given amount of one rupiah when checked then it is a valid transaction amount (BR-001)`() {
        assertThat(Money(rupiah = 1L).isTransactionAmount()).isTrue()
    }

    /** BR-001: the maximum amount of 999,999,999,999 rupiah is accepted. */
    @Test
    fun `given amount of maximum bound when checked then it is a valid transaction amount (BR-001)`() {
        assertThat(Money(rupiah = 999_999_999_999L).isTransactionAmount()).isTrue()
    }

    /** BR-001: zero is below the minimum and is rejected. */
    @Test
    fun `given zero amount when checked then it is not a valid transaction amount (BR-001)`() {
        assertThat(Money(rupiah = 0L).isTransactionAmount()).isFalse()
    }

    /** BR-001: one rupiah above the maximum is rejected. */
    @Test
    fun `given amount above maximum bound when checked then it is not a valid transaction amount (BR-001)`() {
        assertThat(Money(rupiah = 1_000_000_000_000L).isTransactionAmount()).isFalse()
    }

    /** BR-001: a negative amount is rejected. */
    @Test
    fun `given negative amount when checked then it is not a valid transaction amount (BR-001)`() {
        assertThat(Money(rupiah = -1L).isTransactionAmount()).isFalse()
    }
}
