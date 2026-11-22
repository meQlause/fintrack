package com.meqlause.fintrack.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.verify.assertFalse
import org.junit.Test

class NoHardcodedTextTest {
    // DOC-05 section 5: UI strings are forbidden as literals; use stringResource (Indonesian strings.xml)
    @Test
    fun `given production composables when scanned then no Text call has a string literal (DOC-05)`() {
        Konsist
            .scopeFromProject()
            .files
            .filter { file -> normalized(file.path).contains("/src/main/") }
            .assertFalse { file -> HARDCODED_TEXT.containsMatchIn(file.text) }
    }

    private fun normalized(path: String): String = path.replace('\\', '/')

    private companion object {
        val HARDCODED_TEXT = Regex("\\bText\\(\\s*\"")
    }
}
