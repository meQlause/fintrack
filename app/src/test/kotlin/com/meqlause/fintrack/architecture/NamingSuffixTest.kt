package com.meqlause.fintrack.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.Test

class NamingSuffixTest {
    // DOC-05 section 3: suffixes by layer, so the kind of a class is visible from its name
    @Test
    fun `given use case files when named then each class ends with UseCase (DOC-05)`() {
        Konsist
            .scopeFromProject()
            .files
            .filter { file -> normalized(file.path).contains("/core/domain/src/main/kotlin/") }
            .filter { file -> normalized(file.path).contains("/usecase/") }
            .assertTrue { file -> file.classes().all { it.name.endsWith("UseCase") } }
    }

    @Test
    fun `given repository interfaces when named then each ends with Repository (DOC-05)`() {
        Konsist
            .scopeFromProject()
            .interfaces()
            .filter { normalized(it.containingFile.path).contains("/core/domain/") }
            .assertTrue { it.name.endsWith("Repository") }
    }

    @Test
    fun `given local repositories when named then each starts with Local and ends with Repository (DOC-05)`() {
        Konsist
            .scopeFromProject()
            .classes()
            .filter { normalized(it.containingFile.path).contains("/core/data/") }
            .filter { it.name.endsWith("Repository") }
            .assertTrue { it.name.startsWith("Local") }
    }

    @Test
    fun `given fakes when named then each starts with Fake (DOC-05)`() {
        Konsist
            .scopeFromProject()
            .classes()
            .filter { normalized(it.containingFile.path).contains("/core/testing/src/main/") }
            .filter { it.name.endsWith("Repository") }
            .assertTrue { it.name.startsWith("Fake") }
    }

    private fun normalized(path: String): String = path.replace('\\', '/')
}
