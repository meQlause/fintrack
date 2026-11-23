package com.meqlause.fintrack.feature.loan.detail

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.FormRow
import com.meqlause.fintrack.core.designsystem.component.PickerDrawer
import com.meqlause.fintrack.core.domain.loan.LoanDueOption
import com.meqlause.fintrack.feature.loan.R

// FR-158, S-17: the due-date drawer; none, 1 or 2 weeks, or 1, 2 or 3 months counted from today
@Composable
internal fun LoanDueSheet(
    onEvent: (LoanDetailEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    PickerDrawer(
        title = stringResource(R.string.loan_due_drawer_title),
        onClose = { onEvent(LoanDetailEvent.DismissSheet) },
        modifier = modifier,
    ) {
        LoanDueOption.entries.forEach { option ->
            FormRow(
                label = stringResource(dueOptionRes(option)),
                value = "",
                onClick = { onEvent(LoanDetailEvent.DuePicked(option)) },
            )
        }
    }
}

private fun dueOptionRes(option: LoanDueOption): Int =
    when (option) {
        LoanDueOption.NONE -> R.string.loan_due_none
        LoanDueOption.ONE_WEEK -> R.string.loan_due_one_week
        LoanDueOption.TWO_WEEKS -> R.string.loan_due_two_weeks
        LoanDueOption.ONE_MONTH -> R.string.loan_due_one_month
        LoanDueOption.TWO_MONTHS -> R.string.loan_due_two_months
        LoanDueOption.THREE_MONTHS -> R.string.loan_due_three_months
    }
