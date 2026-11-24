package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.TextStyle

// DOC-08: the faint placeholder, the text style and the keyboard of a borderless input
data class InputLook(
    val placeholder: String,
    val style: TextStyle,
    val keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
)
