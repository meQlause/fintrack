package com.meqlause.fintrack.feature.investment

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.meqlause.fintrack.core.domain.investment.InvestmentValuation
import com.meqlause.fintrack.core.model.InvestKind
import com.meqlause.fintrack.core.model.Money
import org.junit.Test

// FR-170, FR-172, FR-173: the labels, the draft rules and the keypad of the investment screens
class InvestmentLogicTest {
    /** ADR-017: every investment kind has its own label. */
    @Test
    fun `given every kind when labelled then each has its own string (ADR-017)`() {
        val labels = InvestKind.entries.map { InvestmentLabels.kindRes(it) }
        assertThat(labels.toSet().size).isEqualTo(InvestKind.entries.size)
    }

    /** FR-173: a percentage reads with one decimal and a decimal comma; losses carry a minus. */
    @Test
    fun `given tenths of a percent when formatted then they read with a decimal comma (FR-173)`() {
        assertThat(InvestmentLabels.percentText(40L)).isEqualTo("4,0")
        assertThat(InvestmentLabels.percentText(-25L)).isEqualTo("-2,5")
    }

    /** FR-173: the gain line has a plus for a gain and none extra for a loss. */
    @Test
    fun `given a gain and a loss when the gain line is built then the signs follow (FR-173)`() {
        val gain = InvestmentValuation(Money(2_000_000L), Money(2_080_000L), null)
        val loss = InvestmentValuation(Money(2_000_000L), Money(1_900_000L), null)
        assertThat(InvestmentLabels.gainLine(gain).endsWith("(+4,0%)")).isTrue()
        assertThat(InvestmentLabels.gainLine(loss).endsWith("(-5,0%)")).isTrue()
    }

    /** FR-170: saving needs a name and a kind. */
    @Test
    fun `given a draft when name or kind is missing then it cannot be saved (FR-170)`() {
        assertThat(InvestmentDraft().canSave).isFalse()
        assertThat(InvestmentDraft(name = "Emas").canSave).isFalse()
        assertThat(InvestmentDraft(kind = InvestKind.GOLD).canSave).isFalse()
        assertThat(InvestmentDraft(name = "Emas", kind = InvestKind.GOLD).canSave).isTrue()
        assertThat(InvestmentDraft(name = "Emas") == InvestmentDraft(name = "Emas")).isTrue()
    }

    /** FR-172: a leading zero is replaced, long input is cut, and a blank value is zero. */
    @Test
    fun `given the keypad when digits are typed then zero is replaced and length is capped (FR-172)`() {
        assertThat(KeypadDigits.append("0", "7")).isEqualTo("7")
        assertThat(KeypadDigits.append("123456789012", "3")).isEqualTo("123456789012")
        assertThat(KeypadDigits.backspace("")).isEqualTo("")
        assertThat(KeypadDigits.toMoney("")).isEqualTo(Money(0L))
    }
}
