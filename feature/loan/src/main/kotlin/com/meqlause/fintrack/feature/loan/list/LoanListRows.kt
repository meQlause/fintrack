package com.meqlause.fintrack.feature.loan.list

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.meqlause.fintrack.core.designsystem.component.ScreenRow
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.LoanStatus
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import com.meqlause.fintrack.feature.loan.R
import com.meqlause.fintrack.feature.loan.loanDateLabel

// S-17: one loan row, an initial in a round badge, the person over the due line and a bar, the amount at the right
@Composable
internal fun LoanRowItem(
    loanRow: LoanListRow,
    showDivider: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ScreenRow(
        modifier = modifier,
        showDivider = showDivider,
        onClick = onClick,
        leading = { LoanAvatar(name = loanRow.personName) },
        trailing = {
            Text(
                text = MoneyFormatter.format(loanRow.outstanding),
                style = FintrackScreenText.number(size = 14f, weight = 700),
                color = FintrackTheme.colors.texts.onSurface,
                maxLines = 1,
            )
        },
    ) {
        LoanRowTexts(loanRow)
    }
}

// S-17: the person's name, then the due line in the error colour when the loan is overdue, then the repayment bar
@Composable
private fun LoanRowTexts(loanRow: LoanListRow) {
    val dueColor =
        if (loanRow.dueInfo.overdue) {
            FintrackTheme.colors.semantics.error
        } else {
            FintrackTheme.colors.texts.onSurfaceVariant
        }
    Text(
        text = loanRow.personName,
        style = FintrackScreenText.style(size = 14f, weight = 600),
        color = FintrackTheme.colors.texts.onSurface,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
    Text(
        text = dueText(loanRow),
        style = FintrackScreenText.rowSub(weight = 500),
        color = dueColor,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
    RepaymentBar(progress = loanRow.progress, modifier = Modifier.padding(top = FintrackTheme.spacing.small))
}

// S-17: the 38 px round badge with the first letter of the person
@Composable
private fun LoanAvatar(name: String) {
    Box(
        modifier =
            Modifier
                .size(FintrackTheme.sizes.categoryIcon)
                .clip(CircleShape)
                .background(FintrackTheme.colors.brands.primaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = name.take(1).uppercase(),
            style = FintrackScreenText.style(size = 15f, weight = 700),
            color = FintrackTheme.colors.brands.onPrimaryContainer,
        )
    }
}

// FR-153: the progress of repayments; the track is the surface variant, the fill the brand colour
@Composable
internal fun RepaymentBar(
    progress: Float,
    modifier: Modifier = Modifier,
) {
    val track = FintrackTheme.colors.surfaces.surfaceVariant
    val fill = FintrackTheme.colors.brands.primary
    val shape = RoundedCornerShape(FintrackScreenSizes.thinBar)
    Canvas(modifier = modifier.fillMaxWidth().height(FintrackScreenSizes.thinBar).clip(shape)) {
        drawRect(color = track)
        drawRect(color = fill, size = size.copy(width = size.width * progress.coerceIn(0f, 1f)))
    }
}

// FR-154, FR-158: what the row says about the due date, or about a loan that is no longer open
@Composable
private fun dueText(loanRow: LoanListRow): String =
    when {
        loanRow.status == LoanStatus.SETTLED -> stringResource(R.string.loan_status_settled)
        loanRow.status == LoanStatus.WRITTEN_OFF -> stringResource(R.string.loan_status_written_off)
        loanRow.dueInfo.overdue -> stringResource(R.string.loan_due_overdue)
        loanRow.dueInfo.date == null -> stringResource(R.string.loan_due_none)
        else -> stringResource(R.string.loan_due_on, loanRow.dueInfo.date.loanDateLabel())
    }
