package com.meqlause.fintrack.feature.transaction.edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.SheetCheckRow
import com.meqlause.fintrack.core.designsystem.component.SheetTextField
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.feature.transaction.R

// FR-039, BR-047, S-04: "Simpan sebagai pintasan" above Simpan; ticking it asks for the shortcut's Nama
@Composable
internal fun EditShortcutKeepRow(
    state: EditTransactionUiState,
    onEvent: (EditTransactionEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        SheetCheckRow(
            label = stringResource(R.string.shortcut_keep_label),
            checked = state.keepAsShortcut,
            onCheckedChange = { onEvent(EditTransactionEvent.ShortcutToggled(it)) },
        )
        if (state.keepAsShortcut) {
            SheetTextField(
                value = state.shortcutName,
                onValueChange = { onEvent(EditTransactionEvent.ShortcutNameChanged(it)) },
                placeholder = stringResource(R.string.shortcut_name_hint),
                modifier = Modifier.padding(top = FintrackTheme.spacing.small),
            )
        }
        if (state.shortcutErrors.isNotEmpty()) {
            Text(
                text = stringResource(R.string.shortcut_name_invalid),
                style = FintrackTheme.typography.labelMedium,
                color = FintrackTheme.colors.texts.onSurfaceVariant,
            )
        }
    }
}
