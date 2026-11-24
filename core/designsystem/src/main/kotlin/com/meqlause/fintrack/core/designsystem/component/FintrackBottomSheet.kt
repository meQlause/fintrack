package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.meqlause.fintrack.core.designsystem.R
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// DOC-08: modal sheet with a drag handle and the large top radius; under a SheetPortal it is drawn over the whole
// screen with a scrim and tapping the scrim calls onDismiss, otherwise it stays where it is declared
@Composable
fun FintrackBottomSheet(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    ReportSheetOpen()
    val portal = LocalSheetPortal.current
    if (portal == null) {
        SheetPanel(modifier = modifier, content = content)
        return
    }
    val entry = remember { SheetEntry(modifier, onDismiss, content) }
    SideEffect {
        entry.modifier = modifier
        entry.dismiss = onDismiss
        entry.content = content
    }
    DisposableEffect(portal) {
        portal.open(entry)
        onDispose { portal.close(entry) }
    }
}

internal const val SHEET_PANEL_TAG = "sheet_panel"
internal const val SHEET_HANDLE_TAG = "sheet_drag_handle"

// DOC-08: the panel itself, the drag handle over the body, on the background colour
@Composable
internal fun SheetPanel(
    modifier: Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier =
            modifier
                .testTag(SHEET_PANEL_TAG)
                .fillMaxWidth()
                .clip(FintrackTheme.shapes.sheet)
                .background(FintrackTheme.colors.surfaces.background)
                .padding(
                    start = FintrackScreenSizes.screenGutter,
                    top = FintrackScreenSizes.sheetTop,
                    end = FintrackScreenSizes.screenGutter,
                    bottom = FintrackScreenSizes.screenGutter,
                ),
    ) {
        SheetDragHandle()
        content()
    }
}

@Composable
private fun ColumnScope.SheetDragHandle() {
    Box(
        modifier =
            Modifier
                .testTag(SHEET_HANDLE_TAG)
                .align(Alignment.CenterHorizontally)
                .padding(bottom = FintrackScreenSizes.sheetHandleBottom)
                .width(FintrackTheme.sizes.dragHandleWidth)
                .height(FintrackTheme.sizes.dragHandleHeight)
                .clip(FintrackTheme.shapes.full)
                .background(FintrackTheme.colors.surfaces.outline),
    )
}

@Composable
internal fun FintrackBottomSheetSample() {
    FintrackBottomSheet {
        Text(
            text = stringResource(R.string.sample_empty_body),
            style = FintrackTheme.typography.bodyLarge,
            color = FintrackTheme.colors.texts.onSurface,
        )
        FintrackButton(text = stringResource(R.string.sample_save), onClick = {})
    }
}

@Preview(name = "FintrackBottomSheet light", showBackground = true)
@Composable
internal fun FintrackBottomSheetLightPreview() {
    ComponentCanvas(darkTheme = false) { FintrackBottomSheetSample() }
}

@Preview(name = "FintrackBottomSheet dark", showBackground = true)
@Composable
internal fun FintrackBottomSheetDarkPreview() {
    ComponentCanvas(darkTheme = true) { FintrackBottomSheetSample() }
}
