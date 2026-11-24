package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

// DOC-08: one sheet the portal shows; the content follows the composable that opened it
class SheetEntry(
    initialModifier: Modifier,
    initialDismiss: () -> Unit,
    initialContent: @Composable ColumnScope.() -> Unit,
) {
    /** DOC-08: the modifier of the panel, such as its maximum height. */
    var modifier: Modifier by mutableStateOf(initialModifier)

    /** DOC-08: what a tap on the dimmed area does. */
    var dismiss: () -> Unit by mutableStateOf(initialDismiss)

    /** DOC-08: the body of the panel, below the drag handle. */
    var content: @Composable ColumnScope.() -> Unit by mutableStateOf(initialContent)
}
