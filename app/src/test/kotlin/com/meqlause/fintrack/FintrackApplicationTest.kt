package com.meqlause.fintrack

import assertk.assertThat
import assertk.assertions.contains
import com.meqlause.fintrack.architecture.ProjectPaths
import org.junit.Test
import java.io.File

// The production FintrackApplication opens the encrypted database when it starts, which needs the Android Keystore
// that Robolectric does not provide, so the test reads the application the merged manifest registers instead
class FintrackApplicationTest {
    // NFR-001: the manifest registers FintrackApplication, the Hilt root of the app graph
    @Test
    fun `given the merged manifest when the app starts then FintrackApplication is the application (NFR-001)`() {
        val manifest =
            File(
                ProjectPaths.root,
                "app/build/intermediates/merged_manifest/release/processReleaseMainManifest/AndroidManifest.xml",
            ).readText()

        assertThat(manifest).contains("android:name=\"${FintrackApplication::class.java.name}\"")
    }
}
