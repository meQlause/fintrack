package com.meqlause.fintrack.feature.lock

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.meqlause.fintrack.core.designsystem.component.NumericKeypad
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// FR-100: the numeric keypad of a PIN screen; a PIN has no triple zero
@Composable
internal fun PinKeypad(callbacks: PinKeypadCallbacks) {
    NumericKeypad(
        onDigit = callbacks.onDigit,
        onBackspace = callbacks.onBackspace,
        showTripleZero = false,
        modifier = Modifier.fillMaxWidth().padding(bottom = FintrackTheme.spacing.xxLarge),
    )
}
