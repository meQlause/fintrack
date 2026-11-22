package com.meqlause.fintrack.core.ui.transaction

import androidx.compose.ui.test.junit4.v2.createComposeRule
import com.meqlause.fintrack.core.ui.captureUi
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w411dp-h720dp")
class TransactionRowScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    // DOC-08: a transaction row shows icon, title, wallet and time, and the coloured amount
    @Test
    fun `given light theme when rendered then the transaction row matches the reference (DOC-08)`() {
        composeRule.captureUi(name = "TransactionRow", darkTheme = false) { TransactionRowSample() }
    }

    @Test
    fun `given dark theme when rendered then the transaction row matches the reference (DOC-08)`() {
        composeRule.captureUi(name = "TransactionRow", darkTheme = true) { TransactionRowSample() }
    }
}
