package com.meqlause.fintrack.feature.transaction.edit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import com.meqlause.fintrack.core.designsystem.component.SheetBleed
import com.meqlause.fintrack.core.designsystem.icon.IconBox
import com.meqlause.fintrack.core.designsystem.icon.IconLook
import com.meqlause.fintrack.core.designsystem.icon.TintedIconBox
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.Category

// FR-031, S-04: the categories of the current kind as a row of icon tiles; the picked one wears a colour ring
@Composable
internal fun EditCategoryTiles(
    state: EditTransactionUiState,
    onEvent: (EditTransactionEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.then(SheetBleed(FintrackScreenSizes.screenGutter)),
        contentPadding =
            PaddingValues(horizontal = FintrackScreenSizes.screenGutter, vertical = FintrackTheme.spacing.xSmall),
        horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.xxSmall),
    ) {
        items(items = state.categoryChoices, key = { category -> category.categoryId.value }) { category ->
            CategoryTile(
                category = category,
                selected = category.categoryId == state.form.categoryId,
                onClick = { onEvent(EditTransactionEvent.CategoryPicked(category.categoryId)) },
            )
        }
    }
}

@Composable
private fun CategoryTile(
    category: Category,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val colors = FintrackTheme.colors
    val appearance = category.profile.appearance
    val ring = colors.categoryColor(appearance.colorKey)
    Column(
        modifier =
            Modifier
                .width(FintrackScreenSizes.categoryTileWidth)
                .clickable(role = Role.Button, onClick = onClick)
                .padding(vertical = FintrackTheme.spacing.xxSmall),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(FintrackScreenSizes.pillGap),
    ) {
        TintedIconBox(
            look = IconLook(iconKey = appearance.iconKey, colorKey = appearance.colorKey),
            tile = CATEGORY_TILE,
            modifier = Modifier.drawBehind { if (selected) drawRing(ring, colors.surfaces.surface) },
        )
        Text(
            text = category.profile.name,
            style = FintrackScreenText.style(size = 11f, weight = if (selected) 700 else 500),
            color = if (selected) colors.texts.onSurface else colors.texts.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

// S-04: the size, glyph and corner of a category tile
private val CATEGORY_TILE =
    IconBox(
        size = FintrackScreenSizes.categoryTile,
        glyph = FintrackScreenSizes.categoryTileGlyph,
        shape = RoundedCornerShape(FintrackScreenSizes.categoryTileCorner),
    )

// prototype box-shadow: a 2 px surface gap, then a 2 px ring of the category colour
private fun DrawScope.drawRing(
    ring: Color,
    halo: Color,
) {
    val step = FintrackScreenSizes.categoryRingStep.toPx()
    val corner = FintrackScreenSizes.categoryTileCorner.toPx()
    listOf(2f to ring, 1f to halo).forEach { (factor, color) ->
        val grow = step * factor
        drawRoundRect(
            color = color,
            topLeft = Offset(-grow, -grow),
            size = Size(size.width + grow * 2, size.height + grow * 2),
            cornerRadius = CornerRadius(corner + grow),
        )
    }
}
