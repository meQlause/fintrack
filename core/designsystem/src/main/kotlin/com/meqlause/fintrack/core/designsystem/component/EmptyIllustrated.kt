package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.meqlause.fintrack.core.designsystem.R
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// DOC-08: the mockup empty state, the receipt art, a bold title, an optional hint and an optional white "+ Catat" pill
@Composable
fun EmptyIllustrated(
    title: String,
    modifier: Modifier = Modifier,
    hint: String? = null,
    action: EmptyAction? = null,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(top = FintrackScreenSizes.emptyTop),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.stack),
    ) {
        EmptyIllustration()
        Text(
            text = title,
            style = FintrackScreenText.style(size = 15f, weight = 700),
            color = FintrackTheme.colors.texts.onSurface,
            textAlign = TextAlign.Center,
        )
        hint?.let {
            Text(
                text = it,
                style = FintrackScreenText.style(size = 12.5f, weight = 400),
                color = FintrackTheme.colors.texts.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
        action?.let { EmptyPill(it) }
    }
}

@Composable
private fun EmptyPill(action: EmptyAction) {
    val colors = FintrackTheme.colors
    Row(
        modifier =
            Modifier
                .height(FintrackScreenSizes.emptyPill)
                .clip(FintrackTheme.shapes.full)
                .background(colors.surfaces.surface)
                .border(FintrackTheme.sizes.hairline, colors.surfaces.outline, FintrackTheme.shapes.full)
                .clickable(onClick = action.onClick)
                .padding(horizontal = FintrackTheme.spacing.large),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_plus),
            contentDescription = null,
            tint = colors.texts.onSurface,
            modifier = Modifier.size(FintrackTheme.sizes.tinyGlyph),
        )
        Text(
            text = action.label,
            style = FintrackScreenText.style(size = 12.5f, weight = 600),
            color = colors.texts.onSurface,
        )
    }
}

@Composable
internal fun EmptyIllustratedSample() {
    EmptyIllustrated(
        title = stringResource(R.string.sample_empty_title),
        action = EmptyAction(label = stringResource(R.string.sample_empty_action), onClick = {}),
    )
}

@Preview(name = "EmptyIllustrated light", showBackground = true)
@Composable
internal fun EmptyIllustratedLightPreview() {
    ComponentCanvas(darkTheme = false) { EmptyIllustratedSample() }
}

@Preview(name = "EmptyIllustrated dark", showBackground = true)
@Composable
internal fun EmptyIllustratedDarkPreview() {
    ComponentCanvas(darkTheme = true) { EmptyIllustratedSample() }
}
