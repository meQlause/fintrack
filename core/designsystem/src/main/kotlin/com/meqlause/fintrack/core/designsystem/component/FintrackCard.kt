package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.meqlause.fintrack.core.designsystem.R
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// DOC-08: rounded surface card with a 1 dp outline hairline and no shadow; onClick adds the press action
@Composable
fun FintrackCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(all = FintrackTheme.spacing.large),
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = FintrackTheme.shapes.medium
    val pressModifier = onClick?.let { Modifier.clickable(onClick = it) } ?: Modifier
    Column(
        modifier =
            modifier
                .clip(shape)
                .background(FintrackTheme.colors.surfaces.surface)
                .border(FintrackTheme.sizes.hairline, FintrackTheme.colors.surfaces.outline, shape)
                .then(pressModifier)
                .padding(contentPadding),
        content = content,
    )
}

@Composable
internal fun FintrackCardSample() {
    FintrackCard {
        Text(
            text = stringResource(R.string.sample_balance_label),
            style = FintrackTheme.typography.bodyMedium,
            color = FintrackTheme.colors.texts.onSurfaceVariant,
        )
        Text(
            text = stringResource(R.string.token_sample_amount),
            style = FintrackTheme.typography.numberLarge,
            color = FintrackTheme.colors.texts.onSurface,
        )
    }
}

@Preview(name = "FintrackCard light", showBackground = true)
@Composable
internal fun FintrackCardLightPreview() {
    ComponentCanvas(darkTheme = false) { FintrackCardSample() }
}

@Preview(name = "FintrackCard dark", showBackground = true)
@Composable
internal fun FintrackCardDarkPreview() {
    ComponentCanvas(darkTheme = true) { FintrackCardSample() }
}
