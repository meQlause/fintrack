package com.meqlause.fintrack.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.Test

class OneTopLevelDeclarationTest {
    // DOC-06 architecture list: one public top-level declaration per file, named after the file
    @Test
    fun `given any file when counted then it has at most one public top-level declaration (DOC-06)`() {
        Konsist
            .scopeFromProject()
            .files
            .assertTrue { file -> publicDeclarations(file.text).size <= 1 }
    }

    @Test
    fun `given a public top-level declaration when named then the file has the same name (DOC-06)`() {
        Konsist
            .scopeFromProject()
            .files
            .filter { file -> publicDeclarations(file.text).size == 1 }
            .assertTrue { file ->
                val declared = publicDeclarations(file.text).single()
                file.path.replace('\\', '/').substringAfterLast('/') == "$declared.kt"
            }
    }

    private fun publicDeclarations(text: String): List<String> =
        DECLARATION
            .findAll(text)
            .map { it.groupValues[2] }
            .toList()

    private companion object {
        val DECLARATION =
            Regex(
                """^(?!private|internal\s)(?:public\s+)?""" +
                    """(?:(?:data|sealed|enum|abstract|open|value|annotation|inline)\s+)*""" +
                    """(class|interface|object|fun|val|var|typealias)\s+(?:[A-Za-z]+\.)?(\w+)""",
                RegexOption.MULTILINE,
            )
    }
}
