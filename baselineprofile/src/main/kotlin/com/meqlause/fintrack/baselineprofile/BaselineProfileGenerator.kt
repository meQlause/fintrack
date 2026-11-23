package com.meqlause.fintrack.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** NFR-004, NFR-005: records the classes used at cold start and while the Riwayat list is scrolled. */
@RunWith(AndroidJUnit4::class)
class BaselineProfileGenerator {
    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    /** NFR-004: collects the startup and Riwayat classes into the profile. */
    @Test
    fun generateStartupAndHistoryProfile() =
        baselineProfileRule.collect(packageName = TARGET_PACKAGE) {
            pressHome()
            startActivityAndWait()
            device.wait(Until.hasObject(By.text(HISTORY_TAB_LABEL)), WAIT_MILLIS)
            device.findObject(By.text(HISTORY_TAB_LABEL))?.click()
            device.waitForIdle()
            device.findObject(By.scrollable(true))?.apply {
                repeat(SCROLL_COUNT) { fling(Direction.DOWN) }
            }
            device.waitForIdle()
        }
}

// NFR-004: the app under test and the Riwayat tab label (tab_history, Indonesian copy)
private const val TARGET_PACKAGE = "com.meqlause.fintrack"
private const val HISTORY_TAB_LABEL = "Riwayat"
private const val SCROLL_COUNT = 5
private const val WAIT_MILLIS = 5_000L
