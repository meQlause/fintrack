package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import com.meqlause.fintrack.core.designsystem.R
import com.meqlause.fintrack.core.designsystem.icon.TintedIconBox
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// DOC-08: the prototype .pickrow, a card with an icon tile, a label over a bold name, a balance and a chevron
@Composable
fun SheetPickRow(
    label: String,
    name: String,
    balance: String,
    iconKey: String,
    colorKey: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FintrackTheme.colors
    val shape = FintrackTheme.shapes.medium
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(shape)
                .background(colors.surfaces.surface)
                .border(FintrackTheme.sizes.hairline, colors.surfaces.outline, shape)
                .clickable(role = Role.Button, onClick = onClick)
                .padding(
                    horizontal = FintrackTheme.spacing.medium,
                    vertical = FintrackScreenSizes.pickRowVertical,
                ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.medium),
    ) {
        TintedIconBox(
            iconKey = iconKey,
            colorKey = colorKey,
            size = FintrackScreenSizes.pickRowIcon,
            glyph = FintrackScreenSizes.pickRowGlyph,
            corner = FintrackScreenSizes.pickRowCorner,
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, style = FintrackScreenText.label(), color = colors.texts.onSurfaceVariant)
            Text(
                text = name,
                modifier = Modifier.padding(top = FintrackTheme.sizes.hairline),
                style = FintrackScreenText.style(size = 14.5f, weight = 700),
                color = colors.texts.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Text(
            text = balance,
            style = FintrackScreenText.number(size = 12f, weight = 600),
            color = colors.texts.onSurfaceVariant,
        )
        Icon(
            painter = painterResource(R.drawable.ic_chev_d),
            contentDescription = null,
            tint = colors.texts.onSurfaceVariant,
            modifier = Modifier.size(FintrackScreenSizes.chevron),
        )
    }
}
