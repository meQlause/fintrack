package com.meqlause.fintrack

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FintrackApplicationTest {
    // NFR-001: the manifest registers FintrackApplication, the Hilt root of the app graph
    @Test
    fun `given the merged manifest when the app starts then FintrackApplication is the application (NFR-001)`() {
        val application = ApplicationProvider.getApplicationContext<Application>()

        assertTrue(application is FintrackApplication)
    }
}
