package com.meqlause.fintrack.feature.transaction.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.meqlause.fintrack.core.designsystem.component.FintrackCard
import com.meqlause.fintrack.core.designsystem.component.UndoSnackbar
import com.meqlause.fintrack.core.designsystem.icon.IconCircle
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.ui.money.MoneyText
import com.meqlause.fintrack.feature.transaction.R

// FR-034, FR-035, FR-038, S-06: stateless transaction detail; amount and facts on top, Ubah / Catat lagi / Hapus below
@Composable
internal fun TransactionDetailScreen(
    state: TransactionDetailUiState,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit,
    onUndo: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize().background(FintrackTheme.colors.surfaces.background)) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = FintrackTheme.spacing.large)) {
            Text(
                text = stringResource(R.string.transaction_detail_back),
                style = FintrackTheme.typography.bodyMedium,
                color = FintrackTheme.colors.brands.primary,
                modifier = Modifier.clickable(onClick = onBack).padding(vertical = FintrackTheme.spacing.small),
            )
            val detail = state.detail
            when {
                !state.isLoaded -> {
                    Unit
                }

                detail == null -> {
                    MissingText()
                }

                else -> {
                    TransactionDetailBody(
                        detail = detail,
                        onEdit = onEdit,
                        onDuplicate = onDuplicate,
                        onDelete = onDelete,
                    )
                }
            }
        }
        if (state.isDeletePending) {
            UndoSnackbar(
                message = stringResource(R.string.transaction_detail_deleted),
                onUndo = onUndo,
                modifier = Modifier.align(Alignment.BottomCenter).padding(FintrackTheme.spacing.large),
            )
        }
    }
}

// FR-034, S-06: the amount and the facts of one transaction, with its three actions underneath
@Composable
private fun TransactionDetailBody(
    detail: TransactionDetail,
    onEdit: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit,
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
            onEdit = onEdit,
            onDuplicate = onDuplicate,
            onDelete = onDelete,
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
