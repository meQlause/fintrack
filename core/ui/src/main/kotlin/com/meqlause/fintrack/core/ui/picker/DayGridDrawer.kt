package com.meqlause.fintrack.core.ui.picker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.meqlause.fintrack.core.designsystem.component.PickerDrawer
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.ui.R
import com.meqlause.fintrack.core.ui.UiPreviewCanvas

private const val DAYS_IN_GRID = 28
private const val DAYS_PER_ROW = 7

// S-18: 1-28 day grid for the statement or due day (FR-138); picking closes the drawer via the caller
@Composable
fun DayGridDrawer(
    title: String,
    selectedDay: Int?,
    onPick: (Int) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PickerDrawer(title = title, onClose = onClose, modifier = modifier) {
        (1..DAYS_IN_GRID).chunked(DAYS_PER_ROW).forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
            ) {
                week.forEach { day ->
                    DayCell(
                        dayNumber = day,
                        isSelected = day == selectedDay,
                        onClick = { onPick(day) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    dayNumber: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FintrackTheme.colors
    val description = stringResource(R.string.picker_day_description, dayNumber)
    Box(
        modifier =
            modifier
                .heightIn(min = FintrackTheme.sizes.touchTarget)
                .clip(FintrackTheme.shapes.medium)
                .background(if (isSelected) colors.brands.primaryContainer else colors.surfaces.surfaceVariant)
                .selectable(selected = isSelected, role = Role.RadioButton, onClick = onClick)
                .semantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = dayNumber.toString(),
            style = FintrackTheme.typography.titleMedium,
            color = if (isSelected) colors.brands.onPrimaryContainer else colors.texts.onSurface,
        )
    }
}

@Composable
internal fun DayGridDrawerSample() {
    DayGridDrawer(
        title = stringResource(R.string.picker_sample_title_day),
        selectedDay = 10,
        onPick = {},
        onClose = {},
    )
}

@Preview(name = "DayGridDrawer light", showBackground = true)
@Composable
internal fun DayGridDrawerLightPreview() {
    UiPreviewCanvas(darkTheme = false) { DayGridDrawerSample() }
}

@Preview(name = "DayGridDrawer dark", showBackground = true)
@Composable
internal fun DayGridDrawerDarkPreview() {
    UiPreviewCanvas(darkTheme = true) { DayGridDrawerSample() }
}
