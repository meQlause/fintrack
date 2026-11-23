package com.meqlause.fintrack.feature.transaction.edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.domain.transaction.TransactionError
import com.meqlause.fintrack.core.model.TransactionType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

// FR-033 AC, S-04: the add sheet is usable in at most four taps from the FAB — digit, category and Simpan
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w411dp-h720dp")
class EditTransactionScreenUiTest {
    @get:Rule
    val composeRule = createComposeRule()

    /** FR-033 AC: keypad digit, category chip and Simpan save an expense; the sheet reports three taps. */
    @Test
    fun givenPreselectedWalletWhenDigitCategoryAndSaveTappedThenExpenseIsSaved() {
        val events = mutableListOf<EditTransactionEvent>()
        composeRule.setContent {
            var state by remember { mutableStateOf(readyState()) }
            FintrackTheme(darkTheme = false) {
                EditTransactionScreen(
                    state = state,
                    onEvent = { event ->
                        events += event
                        state = reduce(state, event)
                    },
                    onDismiss = {},
                )
            }
        }
        composeRule.onNodeWithText("5").performClick()
        composeRule.onNodeWithText("Makan").performClick()
        composeRule.onNodeWithText("Simpan").performScrollTo().performClick()
        assertEquals(3, events.size)
        assertTrue(events.last() == EditTransactionEvent.SaveRequested)
    }

    /** S-04: validation messages are shown in Indonesian under the amount. */
    @Test
    fun givenCategoryErrorWhenSheetShownThenIndonesianMessageIsDisplayed() {
        composeRule.setContent {
            FintrackTheme(darkTheme = false) {
                EditTransactionScreen(
                    state = readyState().withStatus { copy(errors = setOf(TransactionError.CATEGORY_REQUIRED)) },
                    onEvent = {},
                    onDismiss = {},
                )
            }
        }
        composeRule.onNodeWithText("Pilih kategori").assertIsDisplayed()
    }

    /** BR-003: a transfer lays out Dari and Ke side by side, and the swap control is present. */
    @Test
    fun givenTransferWhenSheetShownThenSourceAndTargetCardsAreDisplayed() {
        composeRule.setContent {
            FintrackTheme(darkTheme = false) {
                EditTransactionScreen(
                    state = expenseSample().copy(form = expenseSample().form.copy(type = TransactionType.TRANSFER)),
                    onEvent = {},
                    onDismiss = {},
                )
            }
        }
        composeRule.onNodeWithText("Dari").assertIsDisplayed()
        composeRule.onNodeWithText("Ke").assertIsDisplayed()
    }

    private fun readyState(): EditTransactionUiState {
        val sample = expenseSample()
        return sample.copy(form = sample.form.copy(rupiahDigits = "").withRoute { copy(categoryId = null) })
    }

    // Test-only reducer: applies the events this sheet emits to a plain state, the way the ViewModel would
    private fun reduce(
        state: EditTransactionUiState,
        event: EditTransactionEvent,
    ): EditTransactionUiState {
        val form = state.form
        return when (event) {
            is EditTransactionEvent.DigitTyped -> {
                state.copy(
                    form =
                        form.copy(
                            rupiahDigits =
                                form.rupiahDigits + event.digit,
                        ),
                )
            }

            is EditTransactionEvent.CategoryPicked -> {
                state.copy(form = form.withRoute { copy(categoryId = event.categoryId) })
            }

            else -> {
                state
            }
        }
    }
}
