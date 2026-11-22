package com.meqlause.fintrack.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.verify.assertFalse
import org.junit.Test

class NoSuppressTest {
    // DOC-05 §5: suppressions are forbidden; the AI fixes the code instead
    @Test
    fun `given any kotlin file when scanned then it has no suppression annotations (DOC-05)`() {
        Konsist
            .scopeFromProject()
            .files
            .filterNot { file -> normalized(file.path).contains("/architecture/") }
            .assertFalse { file -> FORBIDDEN.any { file.text.contains(it) } }
    }

    private fun normalized(path: String): String = path.replace('\\', '/')

    private companion object {
        val FORBIDDEN = listOf("@Suppress", "@file:Suppress", "ktlint-disable")
    }
}
