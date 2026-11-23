package com.meqlause.fintrack.feature.wallet.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.meqlause.fintrack.core.designsystem.component.RowSlots
import com.meqlause.fintrack.core.designsystem.component.RowStyle
import com.meqlause.fintrack.core.designsystem.component.ScreenRow
import com.meqlause.fintrack.core.designsystem.icon.IconCircle
import com.meqlause.fintrack.core.designsystem.icon.IconLook
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import com.meqlause.fintrack.feature.wallet.WalletItem

// FR-014, FR-017, S-09: one wallet row inside its group card; a tap opens S-19, a long press opens the editor
@Composable
internal fun WalletItemRow(
    item: WalletItem,
    showDivider: Boolean,
    actions: WalletRowActions,
    modifier: Modifier = Modifier,
) {
    val appearance = item.wallet.profile.appearance
    ScreenRow(
        modifier = modifier,
        style = RowStyle(showDivider = showDivider, onClick = actions.onOpen, onLongClick = actions.onEdit),
        slots =
            RowSlots(leading = {
                IconCircle(look = IconLook(iconKey = appearance.iconKey, colorKey = appearance.colorKey))
            }, trailing = {
                Text(
                    text = MoneyFormatter.format(item.balance),
                    style = FintrackScreenText.rowAmount(),
                    color = FintrackTheme.colors.texts.onSurface,
                    maxLines = 1,
                )
            }),
    ) {
        Text(
            text = item.wallet.profile.name,
            style = FintrackScreenText.rowTitle(),
            color = FintrackTheme.colors.texts.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
