package com.meqlause.fintrack.core.ui.picker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.ScreenCard
import com.meqlause.fintrack.core.designsystem.icon.IconLook
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.ui.R

// S-18, DOC-08: one section of the wallet picker, a small icon and grey label over one card of option rows
@Composable
internal fun WalletPickerSection(
    group: WalletPickerGroup,
    rows: List<WalletPickerRow>,
    onPick: (WalletId) -> Unit,
) {
    Column(modifier = Modifier.padding(top = FintrackScreenSizes.groupGap)) {
        SectionHeader(group)
        ScreenCard {
            rows.forEachIndexed { index, row ->
                OptionRow(
                    option = row.toOption(),
                    isSelected = row.mark.isSelected,
                    showDivider = index > 0,
                    onClick = { onPick(row.walletId) },
                )
            }
        }
    }
}

// S-18: a wallet row wears the wallet's own icon and colour; a blocked one says why and fades
@Composable
private fun WalletPickerRow.toOption(): PickerOption =
    PickerOption(
        optionKey = walletId.value.toString(),
        label = name,
        hint = if (mark.isBlocked) stringResource(R.string.picker_already_selected) else shownBalanceText,
        look = IconLook(iconKey = appearance.iconKey, colorKey = appearance.colorKey),
        isEnabled = !mark.isBlocked,
    )

// DOC-08: the small icon and grey label above a section card
@Composable
private fun SectionHeader(group: WalletPickerGroup) {
    Row(
        modifier =
            Modifier.padding(
                start = FintrackScreenSizes.groupLabelInset,
                bottom = FintrackScreenSizes.groupLabelBottom,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.xSmall),
    ) {
        Icon(
            painter = painterResource(group.iconRes),
            contentDescription = null,
            tint = FintrackTheme.colors.texts.onSurfaceVariant,
            modifier = Modifier.size(FintrackTheme.sizes.iconSmall),
        )
        Text(
            text = stringResource(group.titleRes),
            style = FintrackScreenText.label(),
            color = FintrackTheme.colors.texts.onSurfaceVariant,
        )
    }
}
