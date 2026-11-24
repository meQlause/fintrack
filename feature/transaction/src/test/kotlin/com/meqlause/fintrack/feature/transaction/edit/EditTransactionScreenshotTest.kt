package com.meqlause.fintrack.feature.transaction.edit

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

// FR-030..FR-033, S-04, DOC-08: screenshot references of the add and edit sheet at the mockup size
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w390dp-h844dp")
class EditTransactionScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun givenExpenseSampleInLightThemeWhenRenderedThenItMatchesReference() {
        composeRule.setContent { EditTransactionLightPreview(state = expenseSample()) }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/EditTransaction_light.png")
    }

    @Test
    fun givenExpenseSampleInDarkThemeWhenRenderedThenItMatchesReference() {
        composeRule.setContent { EditTransactionDarkPreview(state = expenseSample()) }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/EditTransaction_dark.png")
    }

    @Test
    fun givenTransferSampleWhenRenderedThenItMatchesReference() {
        composeRule.setContent { EditTransactionLightPreview(state = transferSample()) }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/EditTransactionTransfer_light.png")
    }

    @Test
    fun givenLoanSampleWhenRenderedThenItMatchesReference() {
        composeRule.setContent { EditTransactionLightPreview(state = loanSample()) }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/EditTransactionLoan_light.png")
    }

    @Test
    fun givenCategoryListWhenRenderedThenItMatchesReference() {
        composeRule.setContent { EditCategoryListPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/EditCategoryList_light.png")
    }

    @Test
    fun givenCategoryFormWhenRenderedThenItMatchesReference() {
        composeRule.setContent { EditCategoryFormPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/EditCategoryForm_light.png")
    }
}
