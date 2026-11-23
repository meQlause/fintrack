package com.meqlause.fintrack.feature.lock.navigation

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEqualTo
import org.junit.Test

// FR-100: the PIN setup route is a value; the same mode is the same destination
class PinSetupDestinationTest {
    /** FR-100: equality and hash follow the mode. */
    @Test
    fun `given two destinations when compared then equality and hash follow the mode (FR-100)`() {
        val setup = PinSetupDestination(PinSetupMode.SET)
        assertThat(setup).isEqualTo(PinSetupDestination(PinSetupMode.SET))
        assertThat(setup.hashCode()).isEqualTo(PinSetupDestination(PinSetupMode.SET).hashCode())
        assertThat(setup).isNotEqualTo(PinSetupDestination(PinSetupMode.REMOVE))
        assertThat(setup.equals("SET" as Any?)).isEqualTo(false)
        assertThat(setup.copy(mode = PinSetupMode.CHANGE).mode).isEqualTo(PinSetupMode.CHANGE)
        assertThat(PinSetupDestination()).isEqualTo(setup)
        assertThat(
            PinSetupDestination.serializer().descriptor.serialName,
        ).isEqualTo(PinSetupDestination::class.qualifiedName)
        assertThat(setup.toString()).isEqualTo("PinSetupDestination(mode=SET)")
    }
}
