package com.meqlause.fintrack.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.verify.assertFalse
import org.junit.Test

class ModuleDependencyTest {
    // DOC-03 rules 1 and 2: features never import another feature, nor data, database or datastore
    @Test
    fun `given feature sources when imports checked then no cross-feature or data import exists (DOC-03)`() {
        Konsist
            .scopeFromProject()
            .files
            .filter { file -> normalized(file.path).contains("/feature/") }
            .assertFalse { file ->
                val ownFeature =
                    FEATURE_PACKAGE
                        .find(file.text)
                        ?.groupValues
                        ?.get(1)
                        ?.plus(".") ?: NO_FEATURE
                file.imports.any { importItem ->
                    val name = importItem.name
                    val otherFeature = name.startsWith(FEATURE_ROOT) && !name.startsWith(ownFeature)
                    otherFeature || FORBIDDEN_FOR_FEATURE.any { name.startsWith(it) }
                }
            }
    }

    private fun normalized(path: String): String = path.replace('\\', '/')

    private companion object {
        const val FEATURE_ROOT = "com.meqlause.fintrack.feature."
        const val NO_FEATURE = "<no feature package>"
        val FEATURE_PACKAGE = Regex("""package (com\.meqlause\.fintrack\.feature\.\w+)""")
        val FORBIDDEN_FOR_FEATURE =
            listOf(
                "com.meqlause.fintrack.core.data.",
                "com.meqlause.fintrack.core.database.",
                "com.meqlause.fintrack.core.datastore.",
            )
    }
}
