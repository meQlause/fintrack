package com.meqlause.fintrack.feature.transaction.shortcut

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.PickRowText
import com.meqlause.fintrack.core.designsystem.component.SheetPickRow
import com.meqlause.fintrack.core.designsystem.icon.IconLook
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import com.meqlause.fintrack.feature.transaction.R
import com.meqlause.fintrack.feature.transaction.edit.EditSheet
import com.meqlause.fintrack.feature.transaction.edit.pickerTitleRes

private val FALLBACK_APPEARANCE = Appearance(iconKey = "other", colorKey = "gray")

// FR-039, BR-006: the Kategori and Dompet cards; each opens its drawer
@Composable
internal fun ShortcutPickRows(
    state: ShortcutFormUiState,
    onEvent: (ShortcutFormEvent) -> Unit,
) {
    val spacing = FintrackTheme.spacing
    val category = state.catalog.categories.firstOrNull { it.categoryId == state.draft.categoryId }
    val categoryLook = category?.profile?.appearance ?: FALLBACK_APPEARANCE
    SheetPickRow(
        text =
            PickRowText(
                label = stringResource(R.string.transaction_category_label),
                name = category?.profile?.name ?: stringResource(R.string.transaction_category_pick),
            ),
        look = IconLook(iconKey = categoryLook.iconKey, colorKey = categoryLook.colorKey),
        onClick = { onEvent(ShortcutFormEvent.SheetOpened(ShortcutSheet.CATEGORY)) },
        modifier = Modifier.padding(top = spacing.medium),
    )
    val wallet = state.catalog.wallets.firstOrNull { it.walletId == state.draft.walletId }
    val walletLook = wallet?.profile?.appearance ?: FALLBACK_APPEARANCE
    val balance = state.catalog.moneyByWallet[state.draft.walletId] ?: Money(0L)
    SheetPickRow(
        text =
            PickRowText(
                label = stringResource(pickerTitleRes(state.draft.type, EditSheet.WALLET)),
                name = wallet?.profile?.name.orEmpty(),
                fundsText = if (wallet == null) "" else MoneyFormatter.formatShort(balance),
            ),
        look = IconLook(iconKey = walletLook.iconKey, colorKey = walletLook.colorKey),
        onClick = { onEvent(ShortcutFormEvent.SheetOpened(ShortcutSheet.WALLET)) },
        modifier = Modifier.padding(top = spacing.small),
    )
}
