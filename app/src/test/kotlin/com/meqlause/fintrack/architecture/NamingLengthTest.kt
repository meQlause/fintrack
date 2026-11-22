package com.meqlause.fintrack.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.Test

class NamingLengthTest {
    // DOC-05 section 3: properties and parameters have at least four characters (detekt covers locals)
    @Test
    fun `given properties when named then each has at least four characters (DOC-05)`() {
        Konsist
            .scopeFromProject()
            .properties()
            .assertTrue { it.name.length >= MIN_LENGTH || it.name == "_" }
    }

    @Test
    fun `given function parameters when named then each has at least four characters (DOC-05)`() {
        Konsist
            .scopeFromProject()
            .functions()
            .flatMap { it.parameters }
            .assertTrue { it.name.length >= MIN_LENGTH || it.name == "_" || it.name == "it" }
    }

    private companion object {
        const val MIN_LENGTH = 4
    }
}
