package com.meqlause.fintrack.core.domain.budget

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import com.meqlause.fintrack.core.model.BudgetState
import com.meqlause.fintrack.core.model.Money
import org.junit.jupiter.api.Test

// BR-020, BR-021: percent, band and threshold crossings of a budget
internal class BudgetRulesTest {
    @Test
    fun `given spent 799 of 1000 when percent is computed then it is 79 (BR-020)`() {
        assertThat(BudgetRules.percentOf(Money(799L), Money(1_000L))).isEqualTo(79L)
    }

    @Test
    fun `given a zero limit when percent is computed then it is 0 (BR-020)`() {
        assertThat(BudgetRules.percentOf(Money(500L), Money(0L))).isEqualTo(0L)
    }

    @Test
    fun `given percents around the bands when state is computed then 79 is NORMAL and 80 is WARNING (BR-020)`() {
        assertThat(BudgetRules.stateOf(79L)).isEqualTo(BudgetState.NORMAL)
        assertThat(BudgetRules.stateOf(80L)).isEqualTo(BudgetState.WARNING)
        assertThat(BudgetRules.stateOf(99L)).isEqualTo(BudgetState.WARNING)
    }

    @Test
    fun `given 100 percent when state is computed then it is EXCEEDED (BR-020)`() {
        assertThat(BudgetRules.stateOf(100L)).isEqualTo(BudgetState.EXCEEDED)
    }

    @Test
    fun `given 79 percent when crossings are computed then nothing is alerted (BR-021)`() {
        assertThat(BudgetAlertRules.unsentCrossings(79L, emptySet())).isEmpty()
    }

    @Test
    fun `given 120 percent and nothing sent when crossings are computed then 80 and 100 are returned (BR-021)`() {
        assertThat(BudgetAlertRules.unsentCrossings(120L, emptySet())).containsExactly(80, 100)
    }

    @Test
    fun `given 120 percent and 80 already sent when crossings are computed then only 100 is returned (BR-021)`() {
        assertThat(BudgetAlertRules.unsentCrossings(120L, setOf(80))).containsExactly(100)
    }
}
