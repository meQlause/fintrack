package com.meqlause.fintrack.architecture

import org.junit.Assert.assertFalse
import org.junit.Test

class ForbiddenDependenciesTest {
    // DOC-04 forbidden list and DOC-06: no mocking, network or analytics libraries anywhere in the build
    @Test
    fun `given the build files when scanned then no forbidden library is declared (DOC-04)`() {
        val buildFiles =
            ProjectPaths.root
                .walkTopDown()
                .onEnter { directory -> directory.name != "build" && directory.name != ".gradle" }
                .filter { file -> file.name.endsWith(".kts") || file.name == "libs.versions.toml" }
                .toList()

        val hits =
            buildFiles.filter { file ->
                val text = file.readText().lowercase()
                FORBIDDEN.any { text.contains(it) }
            }

        assertFalse("Forbidden library declared in: $hits", hits.isNotEmpty())
    }

    private companion object {
        val FORBIDDEN = listOf("mockk", "mockito", "firebase", "okhttp", "retrofit", "crashlytics")
    }
}
