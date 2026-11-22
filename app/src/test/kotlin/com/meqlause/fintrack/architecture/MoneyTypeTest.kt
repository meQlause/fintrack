package com.meqlause.fintrack.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.Test

class MoneyTypeTest {
    // DOC-05 section 5 and BR-001: money is never Double or Float
    @Test
    fun `given money named properties when typed then they are Long or Money (BR-001)`() {
        Konsist
            .scopeFromProject()
            .properties()
            .filter { property -> isMoneyName(property.name) }
            .assertTrue { property -> property.type?.name?.removeSuffix("?") in MONEY_TYPES }
    }

    private fun isMoneyName(name: String): Boolean =
        name.startsWith("amount") || name.endsWith("Amount") || name.startsWith("balance")

    private companion object {
        val MONEY_TYPES = setOf("Long", "Money")
    }
}
