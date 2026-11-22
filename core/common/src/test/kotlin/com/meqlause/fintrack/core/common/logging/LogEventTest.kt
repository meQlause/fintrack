package com.meqlause.fintrack.core.common.logging

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

/** DOC-10: verifies that log event names are fixed, non-sensitive identifiers. */
class LogEventTest {
    /** DOC-10: a dotted lowercase name is kept as written. */
    @Test
    fun `given dotted lowercase name when created then keeps the name (DOC-10)`() {
        val event = LogEvent("backup.restore_done")

        assertThat(event.name).isEqualTo("backup.restore_done")
    }

    /** DOC-10: digits could carry amounts, so they are rejected. */
    @Test
    fun `given name with digits when created then is rejected so amounts cannot be logged (DOC-10)`() {
        assertThrows<IllegalArgumentException> { LogEvent("amount.150000") }
    }

    /** DOC-10: spaces could carry free text, so they are rejected. */
    @Test
    fun `given name with spaces when created then is rejected so free text cannot be logged (DOC-10)`() {
        assertThrows<IllegalArgumentException> { LogEvent("merchant name") }
    }

    /** DOC-10: an empty name is not an event. */
    @Test
    fun `given empty name when created then is rejected (DOC-10)`() {
        assertThrows<IllegalArgumentException> { LogEvent("") }
    }
}
