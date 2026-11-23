package com.meqlause.fintrack.feature.wallet.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.FintrackCard
import com.meqlause.fintrack.core.designsystem.icon.IconCircle
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import com.meqlause.fintrack.feature.wallet.R
import com.meqlause.fintrack.feature.wallet.WalletItem
import com.meqlause.fintrack.feature.wallet.WalletLabels

// FR-014, FR-017, S-09: one wallet card; tapping it opens S-19, the Ubah label opens the editor
@Composable
internal fun WalletItemCard(
    item: WalletItem,
    onOpen: () -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FintrackCard(modifier = modifier, onClick = onOpen) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconCircle(
                    iconKey = item.wallet.profile.appearance.iconKey,
                    colorKey = item.wallet.profile.appearance.colorKey,
                )
                Spacer(modifier = Modifier.width(FintrackTheme.spacing.medium))
                Column {
                    Text(
                        text = item.wallet.profile.name,
                        style = FintrackTheme.typography.titleMedium,
                        color = FintrackTheme.colors.texts.onSurface,
                    )
                    Text(
                        text = stringResource(WalletLabels.type(item.wallet.kind.type)),
                        style = FintrackTheme.typography.bodyMedium,
                        color = FintrackTheme.colors.texts.onSurfaceVariant,
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = MoneyFormatter.format(item.balance),
                    style = FintrackTheme.typography.numberMedium,
                    color = FintrackTheme.colors.texts.onSurface,
                )
                Text(
                    text = stringResource(R.string.wallet_edit_action),
                    style = FintrackTheme.typography.labelLarge,
                    color = FintrackTheme.colors.brands.primary,
                    modifier = Modifier.clickable(onClick = onEdit).padding(top = FintrackTheme.spacing.small),
                )
            }
        }
    }
}
