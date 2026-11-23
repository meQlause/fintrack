package com.meqlause.fintrack.feature.loan.detail

import android.content.Context
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import assertk.assertThat
import assertk.assertions.containsExactly
import com.meqlause.fintrack.core.model.LoanDirection
import com.meqlause.fintrack.core.model.LoanStatus
import com.meqlause.fintrack.feature.loan.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

// FR-152, FR-155, FR-158, S-17: the loan detail screen sends the right event for every action it shows
@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w390dp-h844dp")
class LoanDetailScreenUiTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val context = ApplicationProvider.getApplicationContext<Context>()

    private fun text(resId: Int): String = context.getString(resId)

    private fun show(
        state: LoanDetailUiState,
        events: MutableList<LoanDetailEvent> = mutableListOf(),
        onBack: () -> Unit = {},
    ) {
        composeRule.setContent {
            LoanDetailScreen(state = state, onEvent = { events += it }, onBack = onBack)
        }
    }

    @Test
    fun givenAnOpenLoanWhenRepayPressedThenTheRepaymentSheetIsRequested() {
        val events = mutableListOf<LoanDetailEvent>()
        show(LoanDetailUiState(entry = loanEntry(), isLoaded = true), events)

        composeRule.onNodeWithText(text(R.string.loan_repay)).performClick()

        assertThat(events).containsExactly(LoanDetailEvent.OpenRepayment)
    }

    @Test
    fun givenAnOpenLoanWhenWriteOffPressedThenTheWriteOffDialogIsRequested() {
        val events = mutableListOf<LoanDetailEvent>()
        show(LoanDetailUiState(entry = loanEntry(), isLoaded = true), events)

        composeRule.onNodeWithText(text(R.string.loan_write_off)).performClick()

        assertThat(events).containsExactly(LoanDetailEvent.DialogOpened(LoanDialog.WRITE_OFF))
    }

    @Test
    fun givenAnOpenLoanWhenDeletePressedThenTheDeleteDialogIsRequested() {
        val events = mutableListOf<LoanDetailEvent>()
        show(LoanDetailUiState(entry = loanEntry(), isLoaded = true), events)

        composeRule.onNodeWithText(text(R.string.loan_delete)).performClick()

        assertThat(events).containsExactly(LoanDetailEvent.DialogOpened(LoanDialog.DELETE))
    }

    @Test
    fun givenAnOpenLoanWhenDueRowPressedThenTheDueSheetIsRequested() {
        val events = mutableListOf<LoanDetailEvent>()
        show(LoanDetailUiState(entry = loanEntry(), isLoaded = true), events)

        composeRule.onNodeWithText(text(R.string.loan_due_change)).performClick()

        assertThat(events).containsExactly(LoanDetailEvent.OpenDueDate)
    }

    @Test
    fun givenASettledLoanThenItShowsItsStatusAndNoRepaymentAction() {
        show(LoanDetailUiState(entry = loanEntry(status = LoanStatus.SETTLED), isLoaded = true))

        composeRule.onNodeWithText(text(R.string.loan_status_settled)).assertExists()
        composeRule.onNodeWithText(text(R.string.loan_repay)).assertDoesNotExist()
    }

    @Test
    fun givenAWrittenOffLoanThenItShowsItsStatus() {
        show(LoanDetailUiState(entry = loanEntry(status = LoanStatus.WRITTEN_OFF), isLoaded = true))

        composeRule.onNodeWithText(text(R.string.loan_status_written_off)).assertExists()
    }

    @Test
    fun givenABorrowedLoanThenItShowsTheBorrowedStatus() {
        show(LoanDetailUiState(entry = loanEntry(direction = LoanDirection.BORROWED), isLoaded = true))

        composeRule.onNodeWithText(text(R.string.loan_status_borrowed)).assertExists()
        composeRule.onNodeWithText(text(R.string.loan_writeoff_title)).assertDoesNotExist()
    }

    @Test
    fun givenAWriteOffDialogWhenConfirmedThenTheConfirmationIsSent() {
        val events = mutableListOf<LoanDetailEvent>()
        show(
            LoanDetailUiState(
                entry = loanEntry(),
                isLoaded = true,
                overlay = LoanOverlay(dialog = LoanDialog.WRITE_OFF),
            ),
            events,
        )

        composeRule.onNodeWithText(text(R.string.loan_writeoff_body)).assertExists()
        composeRule.onNodeWithText(text(R.string.loan_writeoff_confirm)).performClick()

        assertThat(events).containsExactly(LoanDetailEvent.DialogConfirmed)
    }

    @Test
    fun givenADeleteDialogWhenCancelledThenTheDismissalIsSent() {
        val events = mutableListOf<LoanDetailEvent>()
        show(
            LoanDetailUiState(
                entry = loanEntry(),
                isLoaded = true,
                overlay = LoanOverlay(dialog = LoanDialog.DELETE),
            ),
            events,
        )

        composeRule.onNodeWithText(text(R.string.loan_delete_body)).assertExists()
        composeRule.onNodeWithText(text(R.string.loan_dialog_cancel)).performClick()

        assertThat(events).containsExactly(LoanDetailEvent.DialogDismissed)
    }
}
