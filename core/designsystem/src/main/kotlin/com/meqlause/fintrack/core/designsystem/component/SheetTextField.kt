package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// DOC-08: the prototype .field, a 42 px tall rounded input with a hairline border and a faint placeholder
@Composable
fun SheetTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    val style = FintrackScreenText.style(size = 15f, weight = 400)
    SheetInput(value = value, onValueChange = onValueChange, look = InputLook(placeholder, style), modifier = modifier)
}

// DOC-08: the shared body of the sheet inputs; the look carries the placeholder, the text style and the keyboard
@Composable
internal fun SheetInput(
    value: String,
    onValueChange: (String) -> Unit,
    look: InputLook,
    modifier: Modifier = Modifier,
) {
    val colors = FintrackTheme.colors
    val shape = FintrackScreenSizes.buttonCorner
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(FintrackScreenSizes.fieldHeight)
                .clip(shape)
                .background(colors.surfaces.surface)
                .border(FintrackTheme.sizes.hairline, colors.surfaces.outline, shape)
                .padding(horizontal = FintrackScreenSizes.fieldPadding),
        contentAlignment = Alignment.CenterStart,
    ) {
        ScreenInput(value = value, onValueChange = onValueChange, look = look, modifier = Modifier.fillMaxWidth())
    }
}
