package com.meqlause.fintrack.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.Test

class FileLengthTest {
    // DOC-05 §2: every Kotlin file is at most 250 lines
    @Test
    fun `given any kotlin file when counted then it has at most 250 lines (DOC-05)`() {
        Konsist.scopeFromProject().files.assertTrue { file -> file.text.lines().size <= MAX_LINES }
    }

    private companion object {
        const val MAX_LINES = 250
    }
}
