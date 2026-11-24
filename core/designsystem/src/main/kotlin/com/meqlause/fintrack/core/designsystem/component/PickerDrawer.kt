package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.meqlause.fintrack.core.designsystem.R
import com.meqlause.fintrack.core.designsystem.icon.IconCircle
import com.meqlause.fintrack.core.designsystem.icon.IconLook
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// S-18: picker drawer shell, a fixed header and a scrollable body, as tall as its content up to 75% of the screen
@Composable
fun PickerDrawer(
    title: String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val screenHeight =
        with(LocalDensity.current) {
            LocalWindowInfo.current.containerSize.height
                .toDp()
        }
    val maxHeight = screenHeight * FintrackTheme.sizes.drawerMaxFraction
    FintrackBottomSheet(
        modifier = modifier.heightIn(max = maxHeight),
        onDismiss = onClose,
    ) {
        SheetHeader(title = title, onClose = onClose)
        Column(
            modifier = Modifier.weight(1f, fill = false).fillMaxWidth().verticalScroll(rememberScrollState()),
            content = content,
        )
    }
}

@Composable
internal fun PickerDrawerSample() {
    PickerDrawer(title = stringResource(R.string.sample_drawer_title), onClose = {}) {
        Text(
            text = stringResource(R.string.sample_section_debit),
            style = FintrackScreenText.label(),
            color = FintrackTheme.colors.texts.onSurfaceVariant,
            modifier =
                Modifier.padding(
                    top = FintrackScreenSizes.groupGap,
                    bottom = FintrackScreenSizes.groupLabelBottom,
                ),
        )
        ScreenCard {
            repeat(SAMPLE_ROWS) { index ->
                ScreenRow(
                    slots = RowSlots(leading = { IconCircle(look = IconLook("cash", "green")) }),
                    style = RowStyle(showDivider = index > 0),
                ) {
                    Text(
                        text = stringResource(R.string.sample_wallet_value),
                        style = FintrackScreenText.style(size = 14f, weight = 600),
                        color = FintrackTheme.colors.texts.onSurface,
                    )
                }
            }
        }
    }
}

private const val SAMPLE_ROWS = 2

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
