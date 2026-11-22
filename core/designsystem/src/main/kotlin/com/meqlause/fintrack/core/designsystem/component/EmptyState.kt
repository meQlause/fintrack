package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.meqlause.fintrack.core.designsystem.R
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// DOC-08: icon, title, body and an optional action for a list or screen with no data (S-03, S-05)
@Composable
fun EmptyState(
    copy: EmptyStateCopy,
    iconRes: Int,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = FintrackTheme.spacing.large, vertical = FintrackTheme.spacing.xLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
    ) {
        EmptyStateIcon(iconRes = iconRes)
        EmptyStateText(copy = copy)
        action?.invoke()
    }
}

@Composable
private fun ColumnScope.EmptyStateIcon(iconRes: Int) {
    Icon(
        painter = painterResource(iconRes),
        contentDescription = null,
        tint = FintrackTheme.colors.texts.faint,
        modifier = Modifier.size(FintrackTheme.sizes.iconLarge),
    )
}

@Composable
private fun ColumnScope.EmptyStateText(copy: EmptyStateCopy) {
    val texts = FintrackTheme.colors.texts
    Text(
        text = copy.title,
        style = FintrackTheme.typography.titleMedium,
        color = texts.onSurface,
        textAlign = TextAlign.Center,
    )
    Text(
        text = copy.body,
        style = FintrackTheme.typography.bodyMedium,
        color = texts.onSurfaceVariant,
        textAlign = TextAlign.Center,
    )
}

@Composable
internal fun EmptyStateSample() {
    EmptyState(
        copy =
            EmptyStateCopy(
                title = stringResource(R.string.sample_empty_title),
                body = stringResource(R.string.sample_empty_body),
            ),
        iconRes = R.drawable.ic_wallet,
    )
}

@Preview(name = "EmptyState light", showBackground = true)
@Composable
internal fun EmptyStateLightPreview() {
    ComponentCanvas(darkTheme = false) { EmptyStateSample() }
}

@Preview(name = "EmptyState dark", showBackground = true)
@Composable
internal fun EmptyStateDarkPreview() {
    ComponentCanvas(darkTheme = true) { EmptyStateSample() }
}
