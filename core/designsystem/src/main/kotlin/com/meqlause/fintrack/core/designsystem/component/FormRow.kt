package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.meqlause.fintrack.core.designsystem.R
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// DOC-08 principle 6: one compact `Label ……… Value ›` row that opens a picker drawer (S-18)
@Composable
fun FormRow(
    label: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FintrackTheme.colors
    Column(modifier = modifier.fillMaxWidth()) {
        HorizontalDivider(thickness = FintrackTheme.sizes.hairline, color = colors.surfaces.outline)
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .heightIn(min = FintrackTheme.sizes.formRowHeight)
                    .clickable(role = Role.Button, onClick = onClick)
                    .padding(horizontal = FintrackTheme.spacing.large),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
        ) {
            FormRowContent(label = label, value = value)
        }
    }
}

@Composable
private fun RowScope.FormRowContent(
    label: String,
    value: String,
) {
    val colors = FintrackTheme.colors
    Text(text = label, style = FintrackTheme.typography.bodyMedium, color = colors.texts.onSurfaceVariant)
    Spacer(modifier = Modifier.weight(1f))
    Text(
        text = value,
        style = FintrackTheme.typography.titleMedium,
        color = colors.texts.onSurface,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
    Icon(
        painter = painterResource(R.drawable.ic_chevron_right),
        contentDescription = null,
        tint = colors.texts.faint,
        modifier = Modifier.size(FintrackTheme.sizes.iconSmall),
    )
}

@Composable
internal fun FormRowSample() {
    FormRow(
        label = stringResource(R.string.sample_wallet_label),
        value = stringResource(R.string.sample_wallet_value),
        onClick = {},
    )
}

@Preview(name = "FormRow light", showBackground = true)
@Composable
internal fun FormRowLightPreview() {
    ComponentCanvas(darkTheme = false) { FormRowSample() }
}

@Preview(name = "FormRow dark", showBackground = true)
@Composable
internal fun FormRowDarkPreview() {
    ComponentCanvas(darkTheme = true) { FormRowSample() }
}
