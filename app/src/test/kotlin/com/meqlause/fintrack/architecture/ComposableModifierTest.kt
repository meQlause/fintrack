package com.meqlause.fintrack.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.declaration.KoFunctionDeclaration
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.Test

class ComposableModifierTest {
    // DOC-05 section 7: public UI composables take an optional modifier first, so callers can style them
    @Test
    fun `given public composables when inspected then they take a modifier with a default (DOC-05)`() {
        Konsist
            .scopeFromProject()
            .functions()
            .filter { function -> function.annotations.any { it.name == "Composable" } }
            .filter { function -> !isThemeProvider(function) }
            .filter { function -> function.hasPublicOrDefaultModifier }
            .assertTrue { function -> function.parameters.any { it.name == "modifier" && it.hasDefaultValue() } }
    }

    // DOC-08: the theme entry point (darkTheme, content) only provides CompositionLocals; it emits no UI to style
    private fun isThemeProvider(function: KoFunctionDeclaration): Boolean =
        function.parameters.map { it.name }.toSet() == setOf("darkTheme", "content")
}
