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
import com.meqlause.fintrack.core.domain.loan.LoanEntry
import com.meqlause.fintrack.core.domain.loan.RepaymentError
import com.meqlause.fintrack.core.model.Agreement
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.Loan
import com.meqlause.fintrack.core.model.LoanDirection
import com.meqlause.fintrack.core.model.LoanId
import com.meqlause.fintrack.core.model.LoanStatus
import com.meqlause.fintrack.core.model.Memo
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Movement
import com.meqlause.fintrack.core.model.Origin
import com.meqlause.fintrack.core.model.Routing
import com.meqlause.fintrack.core.model.Timing
import com.meqlause.fintrack.core.model.Transaction
import com.meqlause.fintrack.core.model.TransactionId
import com.meqlause.fintrack.core.model.TransactionSource
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.Wallet
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.model.WalletKind
import com.meqlause.fintrack.core.model.WalletProfile
import com.meqlause.fintrack.core.model.WalletStatus
import com.meqlause.fintrack.core.model.WalletTerms
import com.meqlause.fintrack.core.model.WalletType
import com.meqlause.fintrack.feature.loan.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.Instant

// FR-152, FR-155, FR-158, S-17: the loan detail screen sends the right event for every action it shows
@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w390dp-h844dp")
class LoanDetailScreenUiTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val now = Instant.parse("2026-11-23T07:05:00Z")

    private fun text(id: Int): String = context.getString(id)

    private fun entry(
        status: LoanStatus = LoanStatus.OPEN,
        direction: LoanDirection = LoanDirection.LENT,
        dueDate: Instant? = null,
    ): LoanEntry =
        LoanEntry(
            loan =
                Loan(
                    loanId = LoanId(1L),
                    agreement =
                        Agreement(
                            direction = direction,
                            personName = "Budi",
                            principal = Money(300_000L),
                            startDate = now,
                            dueDate = dueDate,
                            note = "",
                        ),
                    status = status,
                    closedAt = null,
                    audit = Audit(now, now),
                ),
            movements = emptyList(),
        )

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
        show(LoanDetailUiState(entry = entry(), isLoaded = true), events)

        composeRule.onNodeWithText(text(R.string.loan_repay)).performClick()

        assertThat(events).containsExactly(LoanDetailEvent.OpenRepayment)
    }

    @Test
    fun givenAnOpenLoanWhenWriteOffPressedThenTheWriteOffDialogIsRequested() {
        val events = mutableListOf<LoanDetailEvent>()
        show(LoanDetailUiState(entry = entry(), isLoaded = true), events)

        composeRule.onNodeWithText(text(R.string.loan_write_off)).performClick()

        assertThat(events).containsExactly(LoanDetailEvent.DialogOpened(LoanDialog.WRITE_OFF))
    }

    @Test
    fun givenAnOpenLoanWhenDeletePressedThenTheDeleteDialogIsRequested() {
        val events = mutableListOf<LoanDetailEvent>()
        show(LoanDetailUiState(entry = entry(), isLoaded = true), events)

        composeRule.onNodeWithText(text(R.string.loan_delete)).performClick()

        assertThat(events).containsExactly(LoanDetailEvent.DialogOpened(LoanDialog.DELETE))
    }

    @Test
    fun givenAnOpenLoanWhenDueRowPressedThenTheDueSheetIsRequested() {
        val events = mutableListOf<LoanDetailEvent>()
        show(LoanDetailUiState(entry = entry(), isLoaded = true), events)

        composeRule.onNodeWithText(text(R.string.loan_due_change)).performClick()

        assertThat(events).containsExactly(LoanDetailEvent.OpenDueDate)
    }

    @Test
    fun givenASettledLoanThenItShowsItsStatusAndNoRepaymentAction() {
        show(LoanDetailUiState(entry = entry(status = LoanStatus.SETTLED), isLoaded = true))

        composeRule.onNodeWithText(text(R.string.loan_status_settled)).assertExists()
        composeRule.onNodeWithText(text(R.string.loan_repay)).assertDoesNotExist()
    }

    @Test
    fun givenAWrittenOffLoanThenItShowsItsStatus() {
        show(LoanDetailUiState(entry = entry(status = LoanStatus.WRITTEN_OFF), isLoaded = true))

        composeRule.onNodeWithText(text(R.string.loan_status_written_off)).assertExists()
    }

    @Test
    fun givenABorrowedLoanThenItShowsTheBorrowedStatus() {
        show(LoanDetailUiState(entry = entry(direction = LoanDirection.BORROWED), isLoaded = true))

        composeRule.onNodeWithText(text(R.string.loan_status_borrowed)).assertExists()
        composeRule.onNodeWithText(text(R.string.loan_writeoff_title)).assertDoesNotExist()
    }

    @Test
    fun givenAWriteOffDialogWhenConfirmedThenTheConfirmationIsSent() {
        val events = mutableListOf<LoanDetailEvent>()
        show(
            LoanDetailUiState(
                entry = entry(),
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
                entry = entry(),
                isLoaded = true,
                overlay = LoanOverlay(dialog = LoanDialog.DELETE),
            ),
            events,
        )

        composeRule.onNodeWithText(text(R.string.loan_delete_body)).assertExists()
        composeRule.onNodeWithText(text(R.string.loan_dialog_cancel)).performClick()

        assertThat(events).containsExactly(LoanDetailEvent.DialogDismissed)
    }

    @Test
    fun givenTheDueSheetWhenAnOptionIsPickedThenItIsSent() {
        val events = mutableListOf<LoanDetailEvent>()
        show(
            LoanDetailUiState(
                entry = entry(),
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
                entry = entry(),
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
                entry = entry(),
                isLoaded = true,
                wallets = listOf(wallet(id = 1L, name = "BCA")),
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
                entry = entry(),
                isLoaded = true,
                wallets = listOf(wallet(id = 1L, name = "BCA")),
                overlay = LoanOverlay(sheet = LoanDetailSheet.REPAYMENT),
                repayment = RepaymentForm(rupiahDigits = "5"),
            ),
        )

        composeRule.onNodeWithText(text(R.string.loan_repay_save)).assertIsNotEnabled()
    }

    @Test
    fun givenAZeroPrincipalLoanThenItsProgressIsEmpty() {
        val zero =
            entry().copy(
                loan = entry().loan.copy(agreement = entry().loan.agreement.copy(principal = Money(0L))),
            )
        show(LoanDetailUiState(entry = zero, isLoaded = true))

        composeRule.onNodeWithText(text(R.string.loan_repay)).assertExists()
    }

    @Test
    fun givenALoanWithADueDateThenTheDueSheetCanClearIt() {
        val events = mutableListOf<LoanDetailEvent>()
        show(
            LoanDetailUiState(
                entry = entry(dueDate = Instant.parse("2026-12-05T00:00:00Z")),
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
                entry = entry(),
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
                entry = entry(),
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
                movement(TransactionType.LEND, 300_000L),
                movement(TransactionType.REPAY, 100_000L),
                movement(TransactionType.COLLECT, 50_000L),
                movement(TransactionType.BORROW, 10_000L),
            )
        show(LoanDetailUiState(entry = entry().copy(movements = movements), isLoaded = true))

        composeRule.onNodeWithText(context.getString(R.string.loan_movement_lend, "Budi")).assertExists()
        composeRule.onNodeWithText(context.getString(R.string.loan_movement_repay, "Budi")).assertExists()
        composeRule.onNodeWithText(context.getString(R.string.loan_movement_collect, "Budi")).assertExists()
        composeRule.onNodeWithText(context.getString(R.string.loan_movement_borrow, "Budi")).assertExists()
    }

    @Test
    fun givenAWalletWhenRepaymentSheetIsOpenThenPickingAndSavingSendsTheChoices() {
        val events = mutableListOf<LoanDetailEvent>()
        val wallet = wallet(id = 1L, name = "BCA")
        show(
            LoanDetailUiState(
                entry = entry(),
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

    private fun movement(
        type: TransactionType,
        amount: Long,
    ): Transaction =
        Transaction(
            transactionId = TransactionId(amount),
            movement = Movement(type, Money(amount)),
            routing = Routing(WalletId(1L), null, null, LoanId(1L)),
            memo = Memo(),
            origin = Origin(TransactionSource.MANUAL, null),
            timing = Timing(now, Audit(now, now)),
        )

    private fun wallet(
        id: Long,
        name: String,
    ): Wallet =
        Wallet(
            walletId = WalletId(id),
            kind = WalletKind(type = WalletType.CASH, investKind = null),
            terms = WalletTerms(initialBalance = Money(0L), credit = null),
            profile = WalletProfile(name = name, appearance = Appearance("wallet", "blue"), sortOrder = 0),
            status = WalletStatus(),
            audit = Audit(now, now),
        )
}
