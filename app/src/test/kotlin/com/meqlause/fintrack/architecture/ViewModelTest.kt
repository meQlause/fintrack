package com.meqlause.fintrack.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.verify.assertFalse
import org.junit.Test

class ViewModelTest {
    // DOC-05 section 7 and DOC-06: ViewModels expose immutable state and never take a Context
    @Test
    fun `given view models when scanned then none exposes a mutable state flow (DOC-06)`() {
        Konsist
            .scopeFromProject()
            .files
            .filter { file -> file.classes().any { it.name.endsWith("ViewModel") } }
            .assertFalse { file -> PUBLIC_MUTABLE.containsMatchIn(file.text) }
    }

    @Test
    fun `given view models when imported then android context is never used (DOC-06)`() {
        Konsist
            .scopeFromProject()
            .files
            .filter { file -> file.classes().any { it.name.endsWith("ViewModel") } }
            .assertFalse { file -> file.imports.any { it.name == "android.content.Context" } }
    }

    private companion object {
        val PUBLIC_MUTABLE =
            Regex("""^\s*(?!private|internal)(?:val|var)\s+\w+\s*[:=]\s*Mutable""", RegexOption.MULTILINE)
    }
}
