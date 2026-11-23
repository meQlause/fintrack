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
        CycleRow(
            label = stringResource(R.string.credit_cycle_statement),
            dayNumber = terms.statementDay,
            note = stringResource(R.string.credit_cycle_every_month),
            iconRes = DesignR.drawable.ic_calendar,
            showDivider = false,
            onClick = { onOpenPicker(CreditDay.STATEMENT) },
        )
        CycleRow(
            label = stringResource(R.string.credit_cycle_due),
            dayNumber = terms.dueDay,
            note = stringResource(R.string.credit_cycle_next_month),
            iconRes = DesignR.drawable.ic_clock,
            showDivider = true,
            onClick = { onOpenPicker(CreditDay.DUE) },
        )
    }
}

// S-14: a grey icon square, the label over "Tanggal 15", the note and a chevron
@Composable
private fun CycleRow(
    label: String,
    dayNumber: Int,
    note: String,
    iconRes: Int,
    showDivider: Boolean,
    onClick: () -> Unit,
) {
    ScreenRow(
        style = RowStyle(showDivider = showDivider, onClick = onClick),
        slots =
            RowSlots(leading = { ScreenTintBox(iconRes = iconRes) }, trailing = {
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
            }),
    ) {
        Text(
            text = label,
            style = FintrackScreenText.rowSub(weight = 500),
            color = FintrackTheme.colors.texts.onSurfaceVariant,
        )
        Text(
            text = stringResource(R.string.credit_cycle_day, dayNumber),
            style = FintrackScreenText.rowTitle(),
            color = FintrackTheme.colors.texts.onSurface,
        )
    }
}
