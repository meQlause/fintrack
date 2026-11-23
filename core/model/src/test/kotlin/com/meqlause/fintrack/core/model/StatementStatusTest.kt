package com.meqlause.fintrack.core.model

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

/** BR-034: a credit statement is computed as not issued, due, paid or overdue; it is never stored. */
class StatementStatusTest {
    /** BR-034: the four statement states are listed in their lifecycle order. */
    @Test
    fun `given statement statuses when listed then they follow the BR-034 lifecycle (BR-034)`() {
        assertThat(StatementStatus.entries.map { it.name }).isEqualTo(listOf("NOT_ISSUED", "DUE", "PAID", "OVERDUE"))
    }
}
