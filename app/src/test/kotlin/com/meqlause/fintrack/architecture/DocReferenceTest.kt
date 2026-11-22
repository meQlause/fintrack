package com.meqlause.fintrack.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.Test

class DocReferenceTest {
    // DOC-05 section 8 and ADR-014: use cases, repositories, parsers, ViewModels and screens name their doc ID
    @Test
    fun `given required declarations when scanned then their file cites a doc ID (ADR-014)`() {
        Konsist
            .scopeFromProject()
            .files
            .filter { file -> file.classes().any { REQUIRES_ID.containsMatchIn(it.name) } }
            .assertTrue { file -> DOC_ID.containsMatchIn(file.text) }
    }

    private companion object {
        val REQUIRES_ID = Regex("(UseCase|Repository|Parser|ViewModel|Screen)$")
        val DOC_ID = Regex("""\b(FR|NFR|BR|ADR|DOC)-\d{2,3}\b|\bS-\d{2}\b""")
    }
}
