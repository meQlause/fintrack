package com.meqlause.fintrack.core.ui.picker

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import com.meqlause.fintrack.core.designsystem.icon.IconCircle
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.ui.R

// S-18: one section of the wallet picker, an icon header followed by its radio rows
@Composable
internal fun WalletPickerSection(
    group: WalletPickerGroup,
    rows: List<WalletPickerRow>,
    onPick: (WalletId) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
        ) {
            Icon(
                painter = painterResource(group.iconRes),
                contentDescription = null,
                tint = FintrackTheme.colors.texts.onSurfaceVariant,
                modifier = Modifier.size(FintrackTheme.sizes.iconSmall),
            )
            Text(
                text = stringResource(group.titleRes),
                style = FintrackTheme.typography.labelLarge,
                color = FintrackTheme.colors.texts.onSurfaceVariant,
            )
        }
        rows.forEach { row -> WalletPickerItem(pickerRow = row, onPick = onPick) }
    }
}

@Composable
private fun WalletPickerItem(
    pickerRow: WalletPickerRow,
    onPick: (WalletId) -> Unit,
) {
    val colors = FintrackTheme.colors
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .heightIn(min = FintrackTheme.sizes.formRowHeight)
                .clickable(enabled = !pickerRow.isBlocked, role = Role.RadioButton) { onPick(pickerRow.walletId) }
                .padding(vertical = FintrackTheme.spacing.xSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.medium),
    ) {
        IconCircle(iconKey = pickerRow.appearance.iconKey, colorKey = pickerRow.appearance.colorKey)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = pickerRow.name,
                style = FintrackTheme.typography.titleMedium,
                color = if (pickerRow.isBlocked) colors.texts.faint else colors.texts.onSurface,
            )
            Text(
                text = blockedOrBalance(pickerRow),
                style = FintrackTheme.typography.bodyMedium,
                color = colors.texts.onSurfaceVariant,
            )
        }
        RadioButton(selected = pickerRow.isSelected, onClick = null, enabled = !pickerRow.isBlocked)
    }
}

@Composable
private fun blockedOrBalance(pickerRow: WalletPickerRow): String =
    if (pickerRow.isBlocked) stringResource(R.string.picker_already_selected) else pickerRow.shownBalanceText
