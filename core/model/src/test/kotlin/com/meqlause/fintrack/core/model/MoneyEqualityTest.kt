package com.meqlause.fintrack.core.model

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

/** DOC-02, NFR-015: money is a whole-rupiah value class, so equal amounts are equal and print their amount. */
class MoneyEqualityTest {
    /** NFR-015: two amounts with the same rupiah are equal and have the same hash. */
    @Test
    fun `given two equal amounts when compared then they are equal with the same hash (NFR-015)`() {
        assertThat(Money(25_000L)).isEqualTo(Money(25_000L))
        assertThat(Money(25_000L).hashCode()).isEqualTo(Money(25_000L).hashCode())
    }

    /** DOC-02: the text form of an amount shows its rupiah value. */
    @Test
    fun `given an amount when printed then the rupiah value is shown (DOC-02)`() {
        assertThat(Money(7L).toString()).isEqualTo("Money(rupiah=7)")
    }
}
