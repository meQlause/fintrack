package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import com.meqlause.fintrack.core.designsystem.R
import com.meqlause.fintrack.core.designsystem.icon.IconBox
import com.meqlause.fintrack.core.designsystem.icon.IconLook
import com.meqlause.fintrack.core.designsystem.icon.TintedIconBox
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// DOC-08: a card row that opens a picker, an optional tinted tile, the grey label, the bold value and a chevron
@Composable
fun SheetValueRow(
    text: SheetValueText,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    look: IconLook? = null,
) {
    val colors = FintrackTheme.colors
    SheetCardRow(modifier = modifier, onClick = onClick) {
        look?.let { ValueRowTile(it) }
        Text(
            text = text.label,
            style = FintrackScreenText.style(size = 14f, weight = 400),
            color = colors.texts.onSurfaceVariant,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = text.value,
            style = FintrackScreenText.style(size = 14.5f, weight = 700),
            color = colors.texts.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Icon(
            painter = painterResource(R.drawable.ic_chevron_right),
            contentDescription = null,
            tint = colors.texts.onSurfaceVariant,
            modifier = Modifier.size(FintrackScreenSizes.chevron),
        )
    }
}

@Composable
private fun ValueRowTile(look: IconLook) {
    TintedIconBox(
        look = look,
        tile =
            IconBox(
                size = FintrackScreenSizes.pickRowIcon,
                glyph = FintrackScreenSizes.pickRowGlyph,
                shape = RoundedCornerShape(FintrackScreenSizes.pickRowCorner),
            ),
    )
}
