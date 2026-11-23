package com.meqlause.fintrack.feature.transaction.edit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.ScreenCard
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.LoanDirection
import com.meqlause.fintrack.feature.transaction.R
import com.meqlause.fintrack.feature.transaction.history.HistorySegmentOption
import com.meqlause.fintrack.feature.transaction.history.HistorySegmented
import com.meqlause.fintrack.feature.transaction.history.SegmentChoice
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.meqlause.fintrack.core.designsystem.R as DesignR

private val DUE_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.forLanguageTag("id"))

// FR-150, FR-151, FR-157, FR-158, S-04: the Pinjaman block; direction, then a card with person, wallet and due date
@Composable
internal fun EditLoanRows(
    state: EditTransactionUiState,
    onEvent: (EditTransactionEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val loan = state.loan ?: return
    val info = EditWalletInfo.forWallet(state, state.form.walletId)
    Column(modifier = modifier.fillMaxWidth().padding(top = FintrackTheme.spacing.xxSmall)) {
        DirectionSwitch(direction = loan.direction, onEvent = onEvent)
        ScreenCard(modifier = Modifier.padding(top = FintrackScreenSizes.sheetTop)) {
            EditLoanPerson(
                name = loan.personName,
                hint = stringResource(personHintRes(loan.direction)),
                onChange = { name -> onEvent(EditTransactionEvent.LoanEdited(LoanChange.PersonChanged(name))) },
            )
            LoanValueRow(
                text = LoanRowText(stringResource(walletLabelRes(loan.direction)), info.name, info.fundsText),
                muted = false,
                onClick = { onEvent(EditTransactionEvent.SheetOpened(EditSheet.WALLET)) },
            )
            LoanValueRow(
                text = LoanRowText(stringResource(R.string.loan_due_label), dueLabel(loan.dueDate)),
                muted = loan.dueDate == null,
                onClick = { onEvent(EditTransactionEvent.SheetOpened(EditSheet.LOAN_DUE)) },
            )
        }
    }
}

// S-04: Pinjamkan or Pinjam
@Composable
private fun DirectionSwitch(
    direction: LoanDirection,
    onEvent: (EditTransactionEvent) -> Unit,
) {
    HistorySegmented(
        options = directionOptions(),
        choice =
            SegmentChoice(
                selected = direction,
                onPick = { picked -> onEvent(EditTransactionEvent.LoanEdited(LoanChange.DirectionPicked(picked))) },
            ),
        trackHeight = FintrackTheme.sizes.segmentTrackHeight,
    )
}

// S-04: one prototype .frow, a muted label, the bold value, an optional faint note and a chevron
@Composable
private fun LoanValueRow(
    text: LoanRowText,
    muted: Boolean,
    onClick: () -> Unit,
) {
    val colors = FintrackTheme.colors
    HorizontalDivider(thickness = FintrackTheme.sizes.hairline, color = colors.surfaces.outline)
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .heightIn(min = FintrackScreenSizes.formRowTall)
                .clickable(onClick = onClick)
                .padding(horizontal = FintrackScreenSizes.fieldPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FintrackScreenSizes.pickRowVertical),
    ) {
        LoanRowTexts(text = text, muted = muted)
        LoanChevron()
    }
}

// S-04: the muted label, the bold value and the optional faint note
@Composable
private fun RowScope.LoanRowTexts(
    text: LoanRowText,
    muted: Boolean,
) {
    val colors = FintrackTheme.colors
    Text(
        text = text.label,
        style = FintrackScreenText.style(size = 14f, weight = 500),
        color = colors.texts.onSurfaceVariant,
        modifier = Modifier.weight(1f),
    )
    Text(
        text = text.value,
        style = FintrackScreenText.number(size = 14f, weight = 700),
        color = if (muted) colors.texts.onSurfaceVariant else colors.texts.onSurface,
    )
    if (text.note.isNotEmpty()) {
        Text(
            text = text.note,
            style = FintrackScreenText.number(size = 12f, weight = 500),
            color = colors.texts.faint,
        )
    }
}

// S-04: the faint chevron at the end of a loan row
@Composable
private fun LoanChevron() {
    Icon(
        painter = painterResource(DesignR.drawable.ic_chevron_right),
        contentDescription = null,
        tint = FintrackTheme.colors.texts.faint,
        modifier = Modifier.size(FintrackScreenSizes.chevron),
    )
}

// S-04: the label, the bold value and the optional faint note of a loan row
private data class LoanRowText(
    val label: String,
    val value: String,
    val note: String = "",
)

// S-04: Pinjamkan (arrow up) and Pinjam (arrow down)
@Composable
private fun directionOptions(): List<HistorySegmentOption<LoanDirection>> =
    listOf(
        HistorySegmentOption(LoanDirection.LENT, stringResource(R.string.loan_direction_lent), DesignR.drawable.ic_up),
        HistorySegmentOption(
            LoanDirection.BORROWED,
            stringResource(R.string.loan_direction_borrowed),
            DesignR.drawable.ic_down,
        ),
    )

private fun personHintRes(direction: LoanDirection): Int =
    if (direction == LoanDirection.LENT) R.string.loan_person_hint else R.string.loan_person_hint_borrowed

// FR-150, FR-151: a lent amount leaves the wallet, a borrowed amount comes into it
private fun walletLabelRes(direction: LoanDirection): Int =
    if (direction == LoanDirection.LENT) R.string.loan_wallet_lent else R.string.loan_wallet_borrowed

// FR-158: the chosen due date, or the words "Tidak ada"
@Composable
private fun dueLabel(dueDate: LocalDate?): String =
    dueDate?.format(DUE_FORMAT) ?: stringResource(R.string.loan_due_none)
