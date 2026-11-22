package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
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

private const val BACKSPACE_KEY = "backspace"

private val KEYPAD_ROWS =
    listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("000", "0", BACKSPACE_KEY),
    )

// DOC-08: 4x3 keypad for amounts; digit keys emit their text, the backspace key emits onBackspace
@Composable
fun NumericKeypad(
    onDigit: (String) -> Unit,
    onBackspace: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
    ) {
        KEYPAD_ROWS.forEach { keys ->
            Row(horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small)) {
                keys.forEach { keyLabel ->
                    KeypadKey(
                        label = keyLabel,
                        onClick = { if (keyLabel == BACKSPACE_KEY) onBackspace() else onDigit(keyLabel) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun KeypadKey(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier,
) {
    val colors = FintrackTheme.colors
    val shape = FintrackTheme.shapes.full
    Box(
        modifier =
            modifier
                .height(FintrackTheme.sizes.touchTarget)
                .clip(shape)
                .background(colors.surfaces.surface)
                .border(FintrackTheme.sizes.hairline, colors.surfaces.outline, shape)
                .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (label == BACKSPACE_KEY) {
            Icon(
                painter = painterResource(R.drawable.ic_backspace),
                contentDescription = stringResource(R.string.component_keypad_backspace),
                tint = colors.texts.onSurface,
                modifier = Modifier.size(FintrackTheme.sizes.iconMedium),
            )
        } else {
            Text(text = label, style = FintrackTheme.typography.numberMedium, color = colors.texts.onSurface)
        }
    }
}

@Composable
internal fun NumericKeypadSample() {
    NumericKeypad(onDigit = {}, onBackspace = {})
}

@Preview(name = "NumericKeypad light", showBackground = true)
@Composable
internal fun NumericKeypadLightPreview() {
    ComponentCanvas(darkTheme = false) { NumericKeypadSample() }
}

@Preview(name = "NumericKeypad dark", showBackground = true)
@Composable
internal fun NumericKeypadDarkPreview() {
    ComponentCanvas(darkTheme = true) { NumericKeypadSample() }
}
