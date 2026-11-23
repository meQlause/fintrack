package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

private const val BACKSPACE_KEY = "backspace"
private const val TRIPLE_ZERO_KEY = "000"

private val KEYPAD_ROWS =
    listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("000", "0", BACKSPACE_KEY),
    )

// DOC-08: 4x3 keypad for amounts; digit keys emit their text, the backspace key emits onBackspace
// S-02: showTripleZero false leaves the 000 corner empty for the PIN lock
@Composable
fun NumericKeypad(
    onDigit: (String) -> Unit,
    onBackspace: () -> Unit,
    modifier: Modifier = Modifier,
    showTripleZero: Boolean = true,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(FintrackScreenSizes.keypadGap),
    ) {
        KEYPAD_ROWS.forEach { keys ->
            KeypadRow(keys = keys, showTripleZero = showTripleZero, onDigit = onDigit, onBackspace = onBackspace)
        }
    }
}

@Composable
private fun KeypadRow(
    keys: List<String>,
    showTripleZero: Boolean,
    onDigit: (String) -> Unit,
    onBackspace: () -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(FintrackScreenSizes.keypadGap)) {
        keys.forEach { keyLabel ->
            if (keyLabel == TRIPLE_ZERO_KEY && !showTripleZero) {
                Spacer(modifier = Modifier.weight(1f))
            } else {
                KeypadKey(
                    label = keyLabel,
                    onClick = { if (keyLabel == BACKSPACE_KEY) onBackspace() else onDigit(keyLabel) },
                    modifier = Modifier.weight(1f),
                )
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
    val shape = FintrackTheme.shapes.keypadKey
    Box(
        modifier =
            modifier
                .heightIn(min = FintrackTheme.sizes.keypadKey)
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
            Text(
                text = label,
                style = FintrackScreenText.number(size = 20f, weight = 500),
                color = colors.texts.onSurface,
            )
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
