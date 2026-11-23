package com.meqlause.fintrack.core.domain.credit

import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.hasMessage
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.meqlause.fintrack.core.model.Money
import org.junit.jupiter.api.Test

// FR-140: an instalment plan runs from two to thirty-six months

/** FR-140: instalment request tests. */
class InstalmentRequestTest {
    /** FR-140: the shortest and longest tenor are both accepted. */
    @Test
    fun givenTenorAtBoundsWhenCreatedThenTenorIsKept() {
        assertThat(InstalmentRequest(2, Money(0L)).tenorMonths).isEqualTo(2)
        assertThat(InstalmentRequest(36, Money(0L)).tenorMonths).isEqualTo(36)
    }

    /** FR-140: a single month is not an instalment plan. */
    @Test
    fun givenTenorBelowTwoWhenCreatedThenRejected() {
        assertFailure { InstalmentRequest(1, Money(0L)) }
            .isInstanceOf(IllegalArgumentException::class)
            .hasMessage("tenor must be 2..36 (FR-140)")
    }

    /** FR-140: a tenor above thirty-six months is rejected. */
    @Test
    fun givenTenorAboveThirtySixWhenCreatedThenRejected() {
        assertFailure { InstalmentRequest(37, Money(0L)) }
            .isInstanceOf(IllegalArgumentException::class)
    }
}
