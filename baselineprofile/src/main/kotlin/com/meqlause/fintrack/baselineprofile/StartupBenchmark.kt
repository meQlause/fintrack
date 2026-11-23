package com.meqlause.fintrack.baselineprofile

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** NFR-004: cold start to the first screen, measured with and without the Baseline Profile. */
@RunWith(AndroidJUnit4::class)
class StartupBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    /** NFR-004: cold start with no profile, the baseline for the comparison. */
    @Test
    fun coldStartWithoutProfile() = measureColdStart(CompilationMode.None())

    /** NFR-004: cold start with the Baseline Profile installed. */
    @Test
    fun coldStartWithProfile() = measureColdStart(CompilationMode.Partial())

    private fun measureColdStart(compilation: CompilationMode) {
        benchmarkRule.measureRepeated(
            packageName = TARGET_PACKAGE,
            metrics = listOf(StartupTimingMetric()),
            compilationMode = compilation,
            startupMode = StartupMode.COLD,
            iterations = ITERATIONS,
            setupBlock = { pressHome() },
        ) {
            startActivityAndWait()
        }
    }
}

// NFR-004: the app under test; five cold starts keep the measurement stable on a device
private const val TARGET_PACKAGE = "com.meqlause.fintrack"
private const val ITERATIONS = 5
