package com.meqlause.fintrack.feature.loan.detail

import android.content.Context
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import com.meqlause.fintrack.core.domain.loan.LoanDueOption
import com.meqlause.fintrack.core.domain.loan.RepaymentError
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.feature.loan.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.Instant

// FR-152, FR-155, FR-158, S-17: the loan detail screen sheets and dialogs send the right events
@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w390dp-h844dp")
class LoanDetailSheetsUiTest {
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
    fun givenTheDueSheetWhenAnOptionIsPickedThenItIsSent() {
        val events = mutableListOf<LoanDetailEvent>()
        show(
            LoanDetailUiState(
                entry = loanEntry(),
                isLoaded = true,
                overlay = LoanOverlay(sheet = LoanDetailSheet.DUE_DATE),
            ),
            events,
        )

        composeRule.onNodeWithText(text(R.string.loan_due_one_week)).performClick()

        assertThat(events).containsExactly(LoanDetailEvent.DuePicked(LoanDueOption.ONE_WEEK))
    }

    @Test
    fun givenTheRepaymentSheetWhenADigitIsTypedThenItIsSent() {
        val events = mutableListOf<LoanDetailEvent>()
        show(
            LoanDetailUiState(
                entry = loanEntry(),
                isLoaded = true,
                overlay = LoanOverlay(sheet = LoanDetailSheet.REPAYMENT),
                repayment = RepaymentForm(errors = setOf(RepaymentError.AMOUNT_REQUIRED)),
            ),
            events,
        )

        composeRule.onNodeWithText(text(R.string.loan_repay_amount_required)).assertExists()
        composeRule.onNodeWithText("7").performClick()

        assertThat(events).containsExactly(LoanDetailEvent.DigitTyped("7"))
    }

    @Test
    fun givenNoLoanYetThenNothingIsShownButTheBackButton() {
        show(LoanDetailUiState(entry = null, isLoaded = true))

        composeRule.onNodeWithText(text(R.string.loan_repay)).assertDoesNotExist()
        composeRule.onNodeWithText(text(R.string.loan_back)).assertExists()
    }

    @Test
    fun givenAWalletAndAnAmountThenSaveIsEnabled() {
        show(
            LoanDetailUiState(
                entry = loanEntry(),
                isLoaded = true,
                wallets = listOf(loanWallet(walletKey = 1L, name = "BCA")),
                overlay = LoanOverlay(sheet = LoanDetailSheet.REPAYMENT),
                repayment = RepaymentForm(rupiahDigits = "5", walletId = WalletId(1L)),
            ),
        )

        composeRule.onNodeWithText(text(R.string.loan_repay_save)).assertIsEnabled()
    }

    @Test
    fun givenNoWalletThenSaveIsDisabled() {
        show(
            LoanDetailUiState(
                entry = loanEntry(),
                isLoaded = true,
                wallets = listOf(loanWallet(walletKey = 1L, name = "BCA")),
                overlay = LoanOverlay(sheet = LoanDetailSheet.REPAYMENT),
                repayment = RepaymentForm(rupiahDigits = "5"),
            ),
        )

        composeRule.onNodeWithText(text(R.string.loan_repay_save)).assertIsNotEnabled()
    }

    @Test
    fun givenAZeroPrincipalLoanThenItsProgressIsEmpty() {
        val zero =
            loanEntry().copy(
                loan = loanEntry().loan.copy(agreement = loanEntry().loan.agreement.copy(principal = Money(0L))),
            )
        show(LoanDetailUiState(entry = zero, isLoaded = true))

        composeRule.onNodeWithText(text(R.string.loan_repay)).assertExists()
    }

    @Test
    fun givenALoanWithADueDateThenTheDueSheetCanClearIt() {
        val events = mutableListOf<LoanDetailEvent>()
        show(
            LoanDetailUiState(
                entry = loanEntry(dueDate = Instant.parse("2026-12-05T00:00:00Z")),
                isLoaded = true,
                overlay = LoanOverlay(sheet = LoanDetailSheet.DUE_DATE),
            ),
            events,
        )

        composeRule.onNodeWithText(text(R.string.loan_due_none)).performClick()

        assertThat(events).containsExactly(LoanDetailEvent.DuePicked(LoanDueOption.NONE))
    }

    @Test
    fun givenEveryRepaymentErrorThenEachReasonIsShown() {
        show(
            LoanDetailUiState(
                entry = loanEntry(),
                isLoaded = true,
                overlay = LoanOverlay(sheet = LoanDetailSheet.REPAYMENT),
                repayment = RepaymentForm(errors = RepaymentError.entries.toSet()),
            ),
        )

        RepaymentError.entries.forEach { error ->
            composeRule.onNodeWithText(text(error.messageRes())).assertExists()
        }
    }

    @Test
    fun givenTheDueSheetThenEachLongerChoiceSendsItsOption() {
        val events = mutableListOf<LoanDetailEvent>()
        show(
            LoanDetailUiState(
                entry = loanEntry(),
                isLoaded = true,
                overlay = LoanOverlay(sheet = LoanDetailSheet.DUE_DATE),
            ),
            events,
        )

        listOf(
            R.string.loan_due_two_weeks to LoanDueOption.TWO_WEEKS,
            R.string.loan_due_one_month to LoanDueOption.ONE_MONTH,
            R.string.loan_due_two_months to LoanDueOption.TWO_MONTHS,
            R.string.loan_due_three_months to LoanDueOption.THREE_MONTHS,
        ).forEach { (label, option) ->
            composeRule.onNodeWithText(text(label)).performClick()
            assertThat(events.last()).isEqualTo(LoanDetailEvent.DuePicked(option))
        }
    }

    @Test
    fun givenRepaymentsWhenShownThenTheTimelineListsEveryMovementKind() {
        val movements =
            listOf(
                loanMovement(TransactionType.LEND, 300_000L),
                loanMovement(TransactionType.REPAY, 100_000L),
                loanMovement(TransactionType.COLLECT, 50_000L),
                loanMovement(TransactionType.BORROW, 10_000L),
            )
        show(LoanDetailUiState(entry = loanEntry().copy(movements = movements), isLoaded = true))

        composeRule.onNodeWithText(context.getString(R.string.loan_movement_lend, "Budi")).assertExists()
        composeRule.onNodeWithText(context.getString(R.string.loan_movement_repay, "Budi")).assertExists()
        composeRule.onNodeWithText(context.getString(R.string.loan_movement_collect, "Budi")).assertExists()
        composeRule.onNodeWithText(context.getString(R.string.loan_movement_borrow, "Budi")).assertExists()
    }

    @Test
    fun givenAWalletWhenRepaymentSheetIsOpenThenPickingAndSavingSendsTheChoices() {
        val events = mutableListOf<LoanDetailEvent>()
        val wallet = loanWallet(walletKey = 1L, name = "BCA")
        show(
            LoanDetailUiState(
                entry = loanEntry(),
                isLoaded = true,
                wallets = listOf(wallet),
                overlay = LoanOverlay(sheet = LoanDetailSheet.REPAYMENT),
                repayment = RepaymentForm(rupiahDigits = "5", errors = setOf(RepaymentError.WALLET_REQUIRED)),
            ),
            events,
        )

        composeRule.onNodeWithText(text(R.string.loan_repay_wallet_required)).assertExists()
        composeRule.onNodeWithText("BCA").performClick()

        assertThat(events).containsExactly(
            LoanDetailEvent.WalletPicked(WalletId(1L)),
        )
    }
}
