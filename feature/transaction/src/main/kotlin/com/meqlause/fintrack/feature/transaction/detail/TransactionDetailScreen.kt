package com.meqlause.fintrack.feature.transaction.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.meqlause.fintrack.core.designsystem.component.FintrackBottomSheet
import com.meqlause.fintrack.core.designsystem.component.FintrackCard
import com.meqlause.fintrack.core.designsystem.component.SheetHeader
import com.meqlause.fintrack.core.designsystem.component.UndoSnackbar
import com.meqlause.fintrack.core.designsystem.icon.IconCircle
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.ui.money.MoneyText
import com.meqlause.fintrack.feature.transaction.R
import com.meqlause.fintrack.feature.transaction.edit.ShortcutChip

// FR-034, FR-035, FR-038, S-06: stateless transaction detail as the prototype's bottom sheet; Ubah / Ulangi / Hapus below
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TransactionDetailScreen(
    state: TransactionDetailUiState,
    actions: TransactionDetailCallbacks,
    modifier: Modifier = Modifier,
) {
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
    }
}

// FR-034, S-06: the amount and the facts of one transaction, with its three actions underneath
@Composable
private fun TransactionDetailBody(
    detail: TransactionDetail,
    actions: TransactionDetailCallbacks,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconCircle(
            iconKey = detail.appearance.iconKey,
            colorKey = detail.appearance.colorKey,
            size = FintrackTheme.sizes.detailIcon,
            glyph = FintrackTheme.sizes.detailGlyph,
            shape = FintrackTheme.shapes.card,
            modifier = Modifier.padding(top = FintrackTheme.spacing.xSmall + FintrackTheme.spacing.xxSmall),
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
        FintrackCard(
            modifier = Modifier.fillMaxWidth().padding(top = FintrackTheme.spacing.section),
            contentPadding = PaddingValues(),
        ) {
            DetailRows(detail = detail)
        }
        ActionRow(
            isLoanLinked = detail.isLoanLinked,
            onEdit = actions.onEdit,
            onDuplicate = actions.onDuplicate,
            onDelete = actions.onDelete,
            modifier = Modifier.padding(top = FintrackTheme.spacing.stack),
        )
    }
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
