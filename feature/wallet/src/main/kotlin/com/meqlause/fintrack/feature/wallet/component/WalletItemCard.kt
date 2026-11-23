package com.meqlause.fintrack.feature.wallet.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.FintrackCard
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import com.meqlause.fintrack.feature.wallet.R
import com.meqlause.fintrack.feature.wallet.WalletItem
import com.meqlause.fintrack.feature.wallet.WalletLabels

// FR-014, S-09: one wallet card with its type and its computed balance; tapping it opens the editor
@Composable
internal fun WalletItemCard(
    item: WalletItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FintrackCard(modifier = modifier, onClick = onClick) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
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
            Text(
                text = MoneyFormatter.format(item.balance),
                style = FintrackTheme.typography.numberMedium,
                color = FintrackTheme.colors.texts.onSurface,
            )
        }
    }
}
