package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldDecorator
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// DOC-08: a single-line borderless input with a faint placeholder; the text lives in the caller, the state mirrors it
@Composable
fun ScreenInput(
    value: String,
    onValueChange: (String) -> Unit,
    look: InputLook,
    modifier: Modifier = Modifier,
) {
    val colors = FintrackTheme.colors
    val hint = look.placeholder
    val state = rememberTextFieldState(initialText = value)
    val latest by rememberUpdatedState(onValueChange)
    val shown by rememberUpdatedState(value)
    LaunchedEffect(value) {
        if (state.text.toString() != value) state.setTextAndPlaceCursorAtEnd(value)
    }
    LaunchedEffect(state) {
        snapshotFlow { state.text.toString() }.collect { text -> if (text != shown) latest(text) }
    }
    BasicTextField(
        state = state,
        keyboardOptions = look.keyboardOptions,
        textStyle = look.style.copy(color = colors.texts.onSurface),
        cursorBrush = SolidColor(colors.brands.primary),
        modifier = modifier,
        decorator =
            TextFieldDecorator { inner ->
                Box(contentAlignment = Alignment.CenterStart) {
                    if (state.text.isEmpty()) Text(text = hint, style = look.style, color = colors.texts.faint)
                    inner()
                }
            },
    )
}
