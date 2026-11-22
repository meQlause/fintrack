package com.meqlause.fintrack.core.ui.format

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.meqlause.fintrack.core.model.Money
import org.junit.Test

class MoneyFormatterTest {
    // NFR-011: amounts read as Rp1.250.000, with dot grouping and no decimals
    @Test
    fun `given whole rupiah when formatted then Rp prefix and dot grouping apply (NFR-011)`() {
        FULL_FORM.forEach { (rupiah, expected) ->
            assertThat(MoneyFormatter.format(Money(rupiah)), name = "$rupiah").isEqualTo(expected)
        }
    }

    // DOC-08 formatting rules: a negative amount is prefixed with the true minus sign U+2212
    @Test
    fun `given negative rupiah when formatted then U+2212 precedes Rp (DOC-08)`() {
        NEGATIVE_FULL_FORM.forEach { (rupiah, expected) ->
            assertThat(MoneyFormatter.format(Money(rupiah)), name = "$rupiah").isEqualTo(expected)
        }
    }

    // DOC-08 formatting rules: expense rows get −, income rows get +, transfers stay unsigned
    @Test
    fun `given an amount sign when formatted then the prefix follows the sign (DOC-08)`() {
        SIGNED.forEach { (rupiah, sign, expected) ->
            assertThat(MoneyFormatter.formatSigned(Money(rupiah), sign), name = "$rupiah $sign").isEqualTo(expected)
        }
    }

    // DOC-08 formatting rules: compact amounts for charts and axes, rounded half up
    @Test
    fun `given large amounts when formatted compactly then rb and jt suffixes apply (DOC-08)`() {
        COMPACT.forEach { (rupiah, expected) ->
            assertThat(MoneyFormatter.formatCompact(Money(rupiah)), name = "$rupiah").isEqualTo(expected)
        }
    }

    private companion object {
        val FULL_FORM =
            listOf(
                0L to "Rp0",
                999L to "Rp999",
                1_000L to "Rp1.000",
                25_000L to "Rp25.000",
                4_500L to "Rp4.500",
                1_250_000L to "Rp1.250.000",
                5_000_000L to "Rp5.000.000",
                999_999_999_999L to "Rp999.999.999.999",
            )

        val NEGATIVE_FULL_FORM =
            listOf(
                -25_000L to "−Rp25.000",
                -1L to "−Rp1",
            )

        val SIGNED =
            listOf(
                Triple(25_000L, AmountSign.MINUS, "−Rp25.000"),
                Triple(5_000_000L, AmountSign.PLUS, "+Rp5.000.000"),
                Triple(150_000L, AmountSign.NONE, "Rp150.000"),
                Triple(-1_000L, AmountSign.NONE, "−Rp1.000"),
            )

        val COMPACT =
            listOf(
                0L to "0",
                999L to "999",
                1_000L to "1rb",
                4_000L to "4rb",
                4_500L to "4,5rb",
                9_949L to "9,9rb",
                9_950L to "10rb",
                10_499L to "10rb",
                10_500L to "11rb",
                25_000L to "25rb",
                1_000_000L to "1jt",
                1_049_999L to "1jt",
                1_050_000L to "1,1jt",
                1_249_999L to "1,2jt",
                1_250_000L to "1,3jt",
                -4_500L to "−4,5rb",
            )
    }
}
