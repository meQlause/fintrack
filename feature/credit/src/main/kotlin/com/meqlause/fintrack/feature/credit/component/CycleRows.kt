package com.meqlause.fintrack.feature.credit.component

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.RowSlots
import com.meqlause.fintrack.core.designsystem.component.RowStyle
import com.meqlause.fintrack.core.designsystem.component.ScreenGroup
import com.meqlause.fintrack.core.designsystem.component.ScreenRow
import com.meqlause.fintrack.core.designsystem.component.ScreenTintBox
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.CreditTerms
import com.meqlause.fintrack.feature.credit.R
import com.meqlause.fintrack.feature.credit.detail.CreditDay
import com.meqlause.fintrack.core.designsystem.R as DesignR

// FR-138, S-14: the statement and due days; each row opens the day grid drawer and edits the day at any time
@Composable
internal fun CycleRows(
    terms: CreditTerms,
    onOpenPicker: (CreditDay) -> Unit,
    modifier: Modifier = Modifier,
) {
    ScreenGroup(label = stringResource(R.string.credit_cycle_title), modifier = modifier) {
        val statement =
            CycleInfo(
                label = stringResource(R.string.credit_cycle_statement),
                dayNumber = terms.statementDay,
                note = stringResource(R.string.credit_cycle_every_month),
                iconRes = DesignR.drawable.ic_calendar,
            )
        CycleRow(info = statement, showDivider = false, onClick = { onOpenPicker(CreditDay.STATEMENT) })
        val dueInfo =
            CycleInfo(
                label = stringResource(R.string.credit_cycle_due),
                dayNumber = terms.dueDay,
                note = stringResource(R.string.credit_cycle_next_month),
                iconRes = DesignR.drawable.ic_clock,
            )
        CycleRow(info = dueInfo, showDivider = true, onClick = { onOpenPicker(CreditDay.DUE) })
    }
}

// S-14: what one cycle row shows, its label, day, note and icon
private data class CycleInfo(
    val label: String,
    val dayNumber: Int,
    val note: String,
    val iconRes: Int,
)

// S-14: a grey icon square, the label over "Tanggal 15", the note and a chevron
@Composable
private fun CycleRow(
    info: CycleInfo,
    showDivider: Boolean,
    onClick: () -> Unit,
) {
    ScreenRow(
        style = RowStyle(showDivider = showDivider, onClick = onClick),
        slots = RowSlots(leading = { ScreenTintBox(iconRes = info.iconRes) }, trailing = { CycleTrailing(info.note) }),
    ) {
        Text(
            text = info.label,
            style = FintrackScreenText.rowSub(weight = 500),
            color = FintrackTheme.colors.texts.onSurfaceVariant,
        )
        Text(
            text = stringResource(R.string.credit_cycle_day, info.dayNumber),
            style = FintrackScreenText.rowTitle(),
            color = FintrackTheme.colors.texts.onSurface,
        )
    }
}

// S-14: the note and the chevron at the end of a cycle row
@Composable
private fun CycleTrailing(note: String) {
    Text(
        text = note,
        style = FintrackScreenText.style(size = 13f, weight = 500),
        color = FintrackTheme.colors.texts.onSurfaceVariant,
    )
    Icon(
        painter = painterResource(DesignR.drawable.ic_chevron_right),
        contentDescription = null,
        tint = FintrackTheme.colors.texts.onSurfaceVariant,
        modifier = Modifier.size(FintrackScreenSizes.chevron),
    )
}
