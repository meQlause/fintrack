package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText

// DOC-08: the sheet input of an amount, the same field as SheetTextField with the number keyboard
@Composable
fun SheetNumberField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    val look =
        InputLook(
            placeholder = placeholder,
            style = FintrackScreenText.style(size = 15f, weight = 400),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )
    SheetInput(value = value, onValueChange = onValueChange, look = look, modifier = modifier)
}
