package com.meqlause.fintrack.core.testing.coroutine

import assertk.assertThat
import assertk.assertions.isTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.Rule
import org.junit.Test

class MainDispatcherRuleTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `given main dispatcher rule when main is used then the test dispatcher runs it (DOC-06)`() =
        runTest {
            var ranOnMain = false

            withContext(Dispatchers.Main) { ranOnMain = true }

            assertThat(ranOnMain).isTrue()
        }
}
