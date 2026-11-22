package com.meqlause.fintrack.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.verify.assertFalse
import org.junit.Test

class NoNonNullAssertionTest {
    // DOC-05 section 5: the not-null assertion operator is forbidden; use ?.let, ?: or error("reason")
    @Test
    fun `given any kotlin file when scanned then it has no not-null assertion operator (DOC-05)`() {
        Konsist
            .scopeFromProject()
            .files
            .filterNot { file -> normalized(file.path).contains("/architecture/") }
            .assertFalse { file -> file.text.contains(NOT_NULL_ASSERTION) }
    }

    private fun normalized(path: String): String = path.replace('\\', '/')

    private companion object {
        const val NOT_NULL_ASSERTION = "!!"
    }
}
