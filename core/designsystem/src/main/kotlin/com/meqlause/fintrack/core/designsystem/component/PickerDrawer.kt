package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import com.meqlause.fintrack.core.designsystem.R
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// S-18: picker drawer shell with a fixed header and a scrollable body, capped at the drawer maximum height
@Composable
fun PickerDrawer(
    title: String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    FintrackBottomSheet(modifier = modifier.heightIn(max = FintrackTheme.sizes.drawerMaxHeight)) {
        DrawerHeader(title = title, onClose = onClose)
        Column(
            modifier = Modifier.weight(1f, fill = false).fillMaxWidth().verticalScroll(rememberScrollState()),
            content = content,
        )
    }
}

@Composable
private fun DrawerHeader(
    title: String,
    onClose: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = FintrackTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = title,
            style = FintrackTheme.typography.titleMedium,
            color = FintrackTheme.colors.texts.onSurface,
        )
        Box(
            modifier =
                Modifier
                    .size(FintrackTheme.sizes.touchTarget)
                    .clip(FintrackTheme.shapes.full)
                    .clickable(role = Role.Button, onClick = onClose),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_close),
                contentDescription = stringResource(R.string.component_close),
                tint = FintrackTheme.colors.texts.onSurfaceVariant,
                modifier = Modifier.size(FintrackTheme.sizes.iconSmall),
            )
        }
    }
}

@Composable
internal fun PickerDrawerSample() {
    PickerDrawer(title = stringResource(R.string.sample_drawer_title), onClose = {}) {
        Text(
            text = stringResource(R.string.sample_section_debit),
            style = FintrackTheme.typography.labelLarge,
            color = FintrackTheme.colors.texts.onSurfaceVariant,
        )
        FintrackCard {
            Text(
                text = stringResource(R.string.sample_wallet_value),
                style = FintrackTheme.typography.titleMedium,
                color = FintrackTheme.colors.texts.onSurface,
            )
        }
    }
}

@Preview(name = "PickerDrawer light", showBackground = true)
@Composable
internal fun PickerDrawerLightPreview() {
    ComponentCanvas(darkTheme = false) { PickerDrawerSample() }
}

@Preview(name = "PickerDrawer dark", showBackground = true)
@Composable
internal fun PickerDrawerDarkPreview() {
    ComponentCanvas(darkTheme = true) { PickerDrawerSample() }
}
