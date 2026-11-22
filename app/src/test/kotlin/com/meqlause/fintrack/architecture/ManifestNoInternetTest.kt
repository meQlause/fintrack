package com.meqlause.fintrack.architecture

import org.junit.Assert.assertFalse
import org.junit.Test
import java.io.File

class ManifestNoInternetTest {
    // NFR-001: the merged release manifest never requests network permissions
    @Test
    fun `given the merged release manifest when read then it has no network permission (NFR-001)`() {
        val manifest =
            File(
                ProjectPaths.root,
                "app/build/intermediates/merged_manifest/release/processReleaseMainManifest/AndroidManifest.xml",
            )
        val text = manifest.readText()

        assertFalse(text.contains("android.permission.INTERNET"))
        assertFalse(text.contains("android.permission.ACCESS_NETWORK_STATE"))
    }
}
