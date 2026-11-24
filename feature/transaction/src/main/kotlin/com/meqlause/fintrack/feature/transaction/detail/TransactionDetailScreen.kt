package com.meqlause.fintrack.feature.transaction.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.meqlause.fintrack.core.designsystem.component.FintrackBottomSheet
import com.meqlause.fintrack.core.designsystem.component.FintrackCard
import com.meqlause.fintrack.core.designsystem.component.LocalSheetPortal
import com.meqlause.fintrack.core.designsystem.component.SheetHeader
import com.meqlause.fintrack.core.designsystem.component.UndoSnackbar
import com.meqlause.fintrack.core.designsystem.icon.IconBox
import com.meqlause.fintrack.core.designsystem.icon.IconCircle
import com.meqlause.fintrack.core.designsystem.icon.IconLook
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.ui.money.MoneyText
import com.meqlause.fintrack.feature.transaction.R
import com.meqlause.fintrack.feature.transaction.edit.ShortcutChip

// FR-034, FR-035, FR-038, S-06: stateless transaction detail as the prototype's bottom sheet
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TransactionDetailScreen(
    state: TransactionDetailUiState,
    actions: TransactionDetailCallbacks,
    modifier: Modifier = Modifier,
) {
    // DOC-08: this screen is its own modal window, so its sheets and drawers stay inside it
    CompositionLocalProvider(LocalSheetPortal provides null) {
        ModalBottomSheet(
            onDismissRequest = actions.onBack,
            sheetState = rememberBottomSheetState(SheetValue.Hidden, setOf(SheetValue.Hidden, SheetValue.Expanded)),
            dragHandle = null,
            containerColor = Color.Transparent,
        ) {
            FintrackBottomSheet(modifier = modifier) {
                SheetHeader(title = stringResource(R.string.transaction_detail_title), onClose = actions.onBack) {
                    ShortcutChip(
                        onClick = actions.onShortcuts,
                        modifier = Modifier.padding(end = FintrackTheme.spacing.xxSmall),
                    )
                }
                DetailContent(state = state, actions = actions)
            }
        }
    }
}

// S-06: the facts once loaded, a note for an unknown id, and the undo bar while a delete is pending
@Composable
private fun DetailContent(
    state: TransactionDetailUiState,
    actions: TransactionDetailCallbacks,
) {
    val detail = state.detail
    when {
        !state.isLoaded -> {
            Unit
        }

        detail == null -> {
            MissingText()
        }

        else -> {
            TransactionDetailBody(detail = detail, actions = actions)
        }
    }
    if (state.isDeletePending) {
        UndoSnackbar(
            message = stringResource(R.string.transaction_detail_deleted),
            onUndo = actions.onUndo,
            modifier = Modifier.padding(top = FintrackTheme.spacing.medium),
        )
    }
}

// FR-034, S-06: the amount and the facts of one transaction, with its three actions underneath
@Composable
private fun TransactionDetailBody(
    detail: TransactionDetail,
    actions: TransactionDetailCallbacks,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        DetailHero(detail = detail)
        FintrackCard(
            modifier = Modifier.fillMaxWidth().padding(top = FintrackTheme.spacing.section),
            contentPadding = PaddingValues(),
        ) {
            DetailRows(detail = detail)
        }
        ActionRow(
            isLoanLinked = detail.isLoanLinked,
            actions = actions,
            modifier = Modifier.padding(top = FintrackTheme.spacing.stack),
        )
    }
}

// S-06: the category icon, the amount in its type colour and the title
@Composable
private fun DetailHero(detail: TransactionDetail) {
    IconCircle(
        look = IconLook(iconKey = detail.appearance.iconKey, colorKey = detail.appearance.colorKey),
        modifier = Modifier.padding(top = FintrackTheme.spacing.xSmall + FintrackTheme.spacing.xxSmall),
        tile =
            IconBox(
                size = FintrackTheme.sizes.detailIcon,
                glyph = FintrackTheme.sizes.detailGlyph,
                shape = FintrackTheme.shapes.card,
            ),
    )
    MoneyText(
        money = detail.amount,
        type = detail.type,
        modifier = Modifier.padding(top = FintrackTheme.spacing.medium),
        style = FintrackTheme.typography.numberDetail,
    )
    Text(
        text = detail.title,
        modifier = Modifier.padding(top = FintrackTheme.spacing.xxSmall),
        style = FintrackTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
        color = FintrackTheme.colors.texts.onSurfaceVariant,
    )
}

// FR-034: the detail of a transaction id that no stored row carries (or that was just deleted)
@Composable
private fun MissingText() {
    Text(
        text = stringResource(R.string.transaction_detail_missing),
        style = FintrackTheme.typography.bodyMedium,
        color = FintrackTheme.colors.texts.onSurfaceVariant,
        modifier = Modifier.padding(top = FintrackTheme.spacing.medium),
    )
}
