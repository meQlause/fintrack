package com.meqlause.fintrack.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.verify.assertFalse
import org.junit.Test

class PureKotlinTest {
    // DOC-03 rule 3: model, common, domain and parser are pure Kotlin with no Android imports
    @Test
    fun `given pure kotlin modules when imports checked then no android import exists (DOC-03)`() {
        Konsist
            .scopeFromProject()
            .files
            .filter { file -> PURE_MODULES.any { normalized(file.path).contains(it) } }
            .assertFalse { file ->
                file.imports.any { importItem ->
                    importItem.name.startsWith("android.") || importItem.name.startsWith("androidx.")
                }
            }
    }

    private fun normalized(path: String): String = path.replace('\\', '/')

    private companion object {
        val PURE_MODULES = listOf("/core/model/", "/core/common/", "/core/domain/", "/core/parser/")
    }
}
