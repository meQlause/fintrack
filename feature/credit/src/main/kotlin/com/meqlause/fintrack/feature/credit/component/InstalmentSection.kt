package com.meqlause.fintrack.feature.credit.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.ScreenGroup
import com.meqlause.fintrack.core.designsystem.component.ScreenRow
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import com.meqlause.fintrack.feature.credit.R
import com.meqlause.fintrack.feature.credit.instalment.InstalmentRow

// FR-142, S-14: the active instalments of this card, each opening S-16; "Semua cicilan" opens the full list
@Composable
internal fun InstalmentSection(
    instalments: List<InstalmentRow>,
    onOpenInstalment: (Long) -> Unit,
    onOpenAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ScreenGroup(label = stringResource(R.string.instalment_section_title), modifier = modifier) {
        instalments.forEachIndexed { index, row ->
            InstalmentCard(item = row, showDivider = index > 0, onClick = { onOpenInstalment(row.planId) })
        }
        if (instalments.isEmpty()) {
            Text(
                text = stringResource(R.string.instalment_section_empty),
                style = FintrackScreenText.style(size = 13f, weight = 400),
                color = FintrackTheme.colors.texts.onSurfaceVariant,
                modifier = Modifier.padding(FintrackScreenSizes.rowPaddingHorizontal),
            )
        }
        Text(
            text = stringResource(R.string.instalment_section_all),
            style = FintrackScreenText.style(size = 12.5f, weight = 700),
            color = FintrackTheme.colors.brands.primary,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onOpenAll)
                    .padding(FintrackScreenSizes.rowPaddingHorizontal),
        )
    }
}

// S-14: plan title, "Rp500.000/bln" line with a 3 px progress bar, and the billed count with the principal left
@Composable
private fun InstalmentCard(
    item: InstalmentRow,
    showDivider: Boolean,
    onClick: () -> Unit,
) {
    ScreenRow(
        showDivider = showDivider,
        onClick = onClick,
        leading = {},
        trailing = {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = stringResource(R.string.instalment_progress, item.billed, item.tenor),
                    style = FintrackScreenText.number(size = 14.5f, weight = 700),
                    color = FintrackTheme.colors.texts.onSurface,
                )
                Text(
                    text =
                        stringResource(
                            R.string.instalment_remaining,
                            "Rp" + MoneyFormatter.formatCompact(item.remaining),
                        ),
                    style = FintrackScreenText.rowSub(),
                    color = FintrackTheme.colors.texts.onSurfaceVariant,
                )
            }
        },
    ) {
        Text(
            text = item.title,
            style = FintrackScreenText.rowTitle(),
            color = FintrackTheme.colors.texts.onSurface,
        )
        Text(
            text = stringResource(R.string.instalment_monthly, MoneyFormatter.format(item.monthly)),
            style = FintrackScreenText.rowSubNumber(),
            color = FintrackTheme.colors.texts.onSurfaceVariant,
        )
        ProgressLine(fraction = if (item.tenor > 0) item.billed.toFloat() / item.tenor else 0f)
    }
}

@Composable
private fun ProgressLine(fraction: Float) {
    Box(
        modifier =
            Modifier
                .padding(top = FintrackTheme.spacing.small)
                .fillMaxWidth()
                .height(FintrackScreenSizes.thinBar)
                .clip(RoundedCornerShape(FintrackScreenSizes.thinBar))
                .background(FintrackTheme.colors.surfaces.surfaceVariant),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction)
                    .background(FintrackTheme.colors.brands.primary),
        )
    }
}
