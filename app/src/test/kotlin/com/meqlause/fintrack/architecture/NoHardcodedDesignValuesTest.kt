package com.meqlause.fintrack.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.verify.assertFalse
import org.junit.Test

class NoHardcodedDesignValuesTest {
    // DOC-05 section 5 and DOC-08: colours and dp literals belong only to the design system
    @Test
    fun `given feature and ui code when scanned then no raw colour or dp literal exists (DOC-08)`() {
        Konsist
            .scopeFromProject()
            .files
            .filterNot { file -> normalized(file.path).contains("/core/designsystem/") }
            .filterNot { file -> normalized(file.path).contains("/architecture/") }
            .assertFalse { file -> RAW_VALUE.containsMatchIn(file.text) }
    }

    private fun normalized(path: String): String = path.replace('\\', '/')

    private companion object {
        val RAW_VALUE = Regex("""Color\(0x|\b\d+(\.\d+)?\.dp\b""")
    }
}
