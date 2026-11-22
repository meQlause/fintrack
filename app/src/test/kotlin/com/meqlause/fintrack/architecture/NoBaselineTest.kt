package com.meqlause.fintrack.architecture

import org.junit.Assert.assertFalse
import org.junit.Test

class NoBaselineTest {
    // DOC-05 §5: detekt and lint baselines would hide existing violations
    @Test
    fun `given the repository when searched then it has no baseline files (DOC-05)`() {
        val baselines =
            ProjectPaths.root
                .walkTopDown()
                .onEnter { directory -> directory.name != "build" && directory.name != ".gradle" }
                .filter { file -> file.name == "detekt-baseline.xml" || file.name == "lint-baseline.xml" }
                .toList()

        assertFalse("Baseline files found: $baselines", baselines.isNotEmpty())
    }
}
