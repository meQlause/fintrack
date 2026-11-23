package com.meqlause.fintrack.feature.loan.list

import android.content.Context
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import com.meqlause.fintrack.core.model.LoanDirection
import com.meqlause.fintrack.core.model.LoanStatus
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.feature.loan.R
import com.meqlause.fintrack.feature.loan.loanDateLabel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.LocalDate

// FR-153, S-17: the loan list sends the segment, row and back actions to its callbacks
@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w390dp-h844dp")
class LoanListScreenUiTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val context = ApplicationProvider.getApplicationContext<Context>()

    private fun text(resId: Int): String = context.getString(resId)

    private fun row(
        loanId: Long,
        person: String,
        status: LoanStatus = LoanStatus.OPEN,
    ): LoanListRow =
        LoanListRow(
            loanId = loanId,
            personName = person,
            outstanding = Money(150_000L),
            progress = 0.5f,
            dueInfo = LoanDue(date = null, overdue = false),
            status = status,
        )

    @Test
    fun givenLoansWhenARowIsPressedThenItsLoanIsOpened() {
        val opened = mutableListOf<Long>()
        val state =
            LoanListUiState(
                direction = LoanDirection.LENT,
                groups = listOf(LoanPersonGroup(personName = "Budi", rows = listOf(row(7L, "Budi")))),
                settled = emptyList(),
            )
        composeRule.setContent {
            LoanListScreen(state = state, callbacks = LoanListCallbacks({}, {}, { opened += it }))
        }

        composeRule.onNodeWithText("Budi").performClick()

        assertThat(opened).containsExactly(7L)
    }

    @Test
    fun givenSettledLoansThenTheyAreListedUnderTheirSection() {
        val state =
            LoanListUiState(
                direction = LoanDirection.LENT,
                groups = emptyList(),
                settled = listOf(row(9L, "Sari", LoanStatus.SETTLED)),
            )
        composeRule.setContent {
            LoanListScreen(state = state, callbacks = LoanListCallbacks({}, {}, {}))
        }

        composeRule.onNodeWithText(text(R.string.loan_section_settled), substring = true).assertExists()
        composeRule.onNodeWithText("Sari").assertExists()
    }

    @Test
    fun givenTheSegmentsWhenBorrowedPressedThenTheDirectionIsPicked() {
        val picked = mutableListOf<LoanDirection>()
        composeRule.setContent {
            LoanListScreen(
                state = LoanListUiState(direction = LoanDirection.LENT, groups = emptyList(), settled = emptyList()),
                callbacks = LoanListCallbacks({}, { picked += it }, {}),
            )
        }

        composeRule.onNodeWithText(text(R.string.loan_tab_borrowed)).performClick()

        assertThat(picked.single()).isEqualTo(LoanDirection.BORROWED)
    }

    @Test
    fun givenTheScreenWhenBackPressedThenBackIsCalled() {
        var backed = false
        composeRule.setContent {
            LoanListScreen(
                state = LoanListUiState(direction = LoanDirection.LENT, groups = emptyList(), settled = emptyList()),
                callbacks = LoanListCallbacks({ backed = true }, {}, {}),
            )
        }

        composeRule.onNodeWithContentDescription(text(R.string.loan_back)).performClick()

        assertThat(backed).isEqualTo(true)
    }

    @Test
    fun givenBorrowedLoansWithoutAnyOpenRowThenTheTotalAndEmptyCopyAreShown() {
        composeRule.setContent {
            LoanListScreen(
                state =
                    LoanListUiState(
                        direction = LoanDirection.BORROWED,
                        groups = emptyList(),
                        settled = emptyList(),
                    ),
                callbacks = LoanListCallbacks({}, {}, {}),
            )
        }

        composeRule.onNodeWithText(text(R.string.loan_total_borrowed)).assertExists()
        composeRule.onNodeWithText(text(R.string.loan_list_empty)).assertExists()
    }

    @Test
    fun givenSeveralLoansWhenListedThenEachIsOpenable() {
        val opened = mutableListOf<Long>()
        val state =
            LoanListUiState(
                direction = LoanDirection.LENT,
                groups =
                    listOf(
                        LoanPersonGroup(personName = "Budi", rows = listOf(row(1L, "Budi"), row(2L, "Budi"))),
                    ),
                settled = listOf(row(3L, "Sari", LoanStatus.SETTLED), row(4L, "Rini", LoanStatus.SETTLED)),
            )
        composeRule.setContent {
            LoanListScreen(state = state, callbacks = LoanListCallbacks({}, {}, { opened += it }))
        }

        composeRule.onNodeWithText("Rini").performClick()

        assertThat(opened).containsExactly(4L)
    }

    @Test
    fun givenTheBorrowedSegmentWhenLentPressedThenTheLentDirectionIsPicked() {
        val picked = mutableListOf<LoanDirection>()
        composeRule.setContent {
            LoanListScreen(
                state =
                    LoanListUiState(
                        direction = LoanDirection.BORROWED,
                        groups = emptyList(),
                        settled = emptyList(),
                    ),
                callbacks = LoanListCallbacks({}, { picked += it }, {}),
            )
        }

        composeRule.onNodeWithText(text(R.string.loan_tab_lent)).performClick()

        assertThat(picked.single()).isEqualTo(LoanDirection.LENT)
    }

    @Test
    fun givenAFutureDueDateThenTheRowShowsThatDate() {
        val date = LocalDate.of(2026, 12, 5)
        val state =
            LoanListUiState(
                direction = LoanDirection.LENT,
                groups =
                    listOf(
                        LoanPersonGroup(
                            personName = "Andi",
                            rows = listOf(row(5L, "Andi").copy(dueInfo = LoanDue(date = date, overdue = false))),
                        ),
                    ),
                settled = emptyList(),
            )
        composeRule.setContent {
            LoanListScreen(state = state, callbacks = LoanListCallbacks({}, {}, {}))
        }

        composeRule.onNodeWithText(context.getString(R.string.loan_due_on, date.loanDateLabel())).assertExists()
    }

    @Test
    fun givenOverdueAndWrittenOffLoansThenEachRowSaysItsState() {
        val overdue =
            row(3L, "Andi").copy(dueInfo = LoanDue(date = LocalDate.of(2026, 11, 1), overdue = true))
        val state =
            LoanListUiState(
                direction = LoanDirection.LENT,
                groups = listOf(LoanPersonGroup(personName = "Andi", rows = listOf(overdue))),
                settled = listOf(row(4L, "Rudi", LoanStatus.WRITTEN_OFF)),
            )
        composeRule.setContent {
            LoanListScreen(state = state, callbacks = LoanListCallbacks({}, {}, {}))
        }

        composeRule.onNodeWithText(text(R.string.loan_due_overdue)).assertExists()
        composeRule.onNodeWithText(text(R.string.loan_status_written_off)).assertExists()
    }
}
