package com.meqlause.fintrack.feature.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import com.meqlause.fintrack.core.designsystem.component.PickerDrawer
import com.meqlause.fintrack.core.designsystem.component.ScreenCard
import com.meqlause.fintrack.core.designsystem.component.ScreenRow
import com.meqlause.fintrack.core.designsystem.icon.TintedIconBox
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// DOC-08, S-03: the prototype's info drawer: a hero figure, grouped cards of icon rows and a dark action button
@Composable
internal fun HomeInfoDrawer(
    content: HomeInfoContent,
    onClose: () -> Unit,
    onAction: () -> Unit,
) {
    PickerDrawer(title = content.title, onClose = onClose) {
        HomeInfoHero(content = content)
        content.sections.forEach { section -> HomeInfoSectionBlock(section = section) }
        Box(
            modifier =
                Modifier
                    .padding(top = FintrackScreenSizes.screenTop)
                    .fillMaxWidth()
                    .height(FintrackScreenSizes.dashedHeight)
                    .clip(FintrackTheme.shapes.medium)
                    .background(FintrackTheme.colors.texts.onSurface)
                    .clickable(role = Role.Button, onClick = onAction),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = content.actionLabel,
                style = FintrackScreenText.style(size = 14.5f, weight = 700),
                color = FintrackTheme.colors.surfaces.background,
            )
        }
    }
}

@Composable
private fun HomeInfoHero(content: HomeInfoContent) {
    val colors = FintrackTheme.colors
    Column(modifier = Modifier.fillMaxWidth().padding(top = FintrackScreenSizes.pillGap)) {
        Text(text = content.heroLabel, style = FintrackScreenText.label(), color = colors.texts.onSurfaceVariant)
        Text(
            text = content.heroValue,
            style = FintrackScreenText.number(size = 28f, weight = 700),
            color = colors.texts.onSurface,
            modifier = Modifier.padding(top = FintrackTheme.spacing.xxSmall),
        )
        Text(
            text = content.heroNote,
            style = FintrackScreenText.style(size = 12f, weight = 400),
            color = colors.texts.onSurfaceVariant,
            modifier = Modifier.padding(top = FintrackTheme.spacing.xxSmall),
        )
        HorizontalDivider(
            modifier = Modifier.padding(top = FintrackScreenSizes.screenTop),
            thickness = FintrackTheme.sizes.hairline,
            color = colors.surfaces.outline,
        )
    }
}

@Composable
private fun HomeInfoSectionBlock(section: HomeInfoSection) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    start = FintrackScreenSizes.groupLabelInset,
                    top = FintrackTheme.spacing.large,
                    end = FintrackScreenSizes.groupLabelInset,
                    bottom = FintrackTheme.spacing.small,
                ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
    ) {
        Text(
            text = section.title,
            style = FintrackScreenText.label(),
            color = FintrackTheme.colors.texts.onSurfaceVariant,
        )
        Text(
            text = section.total,
            style = FintrackScreenText.number(size = 13f, weight = 700),
            color = toneColor(section.tone),
        )
    }
    ScreenCard {
        section.rows.forEachIndexed { index, row -> HomeInfoRowLine(entry = row, showDivider = index > 0) }
    }
}

@Composable
private fun HomeInfoRowLine(
    entry: HomeInfoRow,
    showDivider: Boolean,
) {
    val colors = FintrackTheme.colors
    ScreenRow(
        showDivider = showDivider,
        contentPadding =
            PaddingValues(
                horizontal = FintrackScreenSizes.rowPaddingHorizontal,
                vertical = FintrackScreenSizes.infoRowVertical,
            ),
        leading = {
            TintedIconBox(
                iconKey = entry.iconKey,
                colorKey = entry.colorKey,
                size = FintrackScreenSizes.smallIconBox + FintrackTheme.spacing.xxSmall,
                glyph = FintrackScreenSizes.circleGlyph,
                corner = FintrackScreenSizes.smallIconCorner,
            )
        },
        trailing = {
            Text(
                text = entry.value,
                style = FintrackScreenText.number(size = 14f, weight = 700),
                color = toneColor(entry.tone),
            )
        },
    ) {
        Text(
            text = entry.label,
            style = FintrackScreenText.style(size = 14f, weight = 600),
            color = colors.texts.onSurface,
            maxLines = 1,
        )
        Text(
            text = entry.caption,
            style = FintrackScreenText.style(size = 12f, weight = 400),
            color = colors.texts.onSurfaceVariant,
            maxLines = 1,
            modifier = Modifier.padding(top = FintrackTheme.spacing.xxSmall),
        )
    }
}

@Composable
private fun toneColor(tone: HomeTone): Color {
    val colors = FintrackTheme.colors
    return when (tone) {
        HomeTone.NORMAL -> colors.texts.onSurface
        HomeTone.INCOME -> colors.semantics.income
        HomeTone.EXPENSE -> colors.semantics.expense
    }
}
