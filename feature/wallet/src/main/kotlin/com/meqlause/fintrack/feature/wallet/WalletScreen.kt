package com.meqlause.fintrack.feature.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.EmptyState
import com.meqlause.fintrack.core.designsystem.component.EmptyStateCopy
import com.meqlause.fintrack.core.designsystem.component.FintrackButton
import com.meqlause.fintrack.core.designsystem.component.FintrackCard
import com.meqlause.fintrack.core.designsystem.component.FintrackTabHeader
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import com.meqlause.fintrack.feature.wallet.component.WalletEditorSheet
import com.meqlause.fintrack.feature.wallet.component.WalletItemCard
import com.meqlause.fintrack.feature.wallet.component.WalletPickerOverlay
import com.meqlause.fintrack.core.designsystem.R as DesignR

// DOC-03, FR-010..016, S-09: stateless Dompet screen; list, editor and drawers all come from WalletUiState
@Composable
internal fun WalletScreen(
    state: WalletUiState,
    callbacks: WalletCallbacks,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(FintrackTheme.colors.surfaces.background)
                .padding(horizontal = FintrackTheme.spacing.large),
        verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
    ) {
        FintrackTabHeader(title = stringResource(R.string.wallet_tab_title), leading = {})
        WalletBody(state = state, callbacks = callbacks, modifier = Modifier.weight(1f))
        FintrackButton(text = stringResource(R.string.wallet_add), onClick = callbacks.onAdd)
        state.editorOrNull()?.let { draft ->
            WalletEditorSheet(draft = draft, callbacks = callbacks)
            state.pickerOrNull()?.let { open ->
                WalletPickerOverlay(picker = open, draft = draft, callbacks = callbacks)
            }
        }
    }
}

// S-09: the list area; Loading shows nothing, Empty shows the empty state, Success shows the wallets
@Composable
private fun WalletBody(
    state: WalletUiState,
    callbacks: WalletCallbacks,
    modifier: Modifier,
) {
    when (state) {
        WalletUiState.Loading -> {
            Column(modifier = modifier) {}
        }

        is WalletUiState.Empty -> {
            EmptyState(
                copy =
                    EmptyStateCopy(
                        title = stringResource(R.string.wallet_empty_title),
                        body = stringResource(R.string.wallet_empty_body),
                    ),
                iconRes = DesignR.drawable.ic_wallet,
                modifier = modifier,
            )
        }

        is WalletUiState.Success -> {
            WalletList(state = state, callbacks = callbacks, modifier = modifier)
        }
    }
}

// S-09: the total, the groups with their balances, then the collapsed archived section
@Composable
private fun WalletList(
    state: WalletUiState.Success,
    callbacks: WalletCallbacks,
    modifier: Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
    ) {
        item(key = "total") { TotalCard(total = state.total) }
        state.sections.forEach { section ->
            item(key = "group-${section.group.name}") { GroupHeading(group = section.group) }
            items(section.items, key = { it.wallet.walletId.value }) { row ->
                WalletItemCard(item = row, onClick = { callbacks.onEdit(row) })
            }
        }
        archivedItems(state = state, callbacks = callbacks)
    }
}

// FR-012, S-09: archived wallets sit under a heading that opens and closes the section
private fun LazyListScope.archivedItems(
    state: WalletUiState.Success,
    callbacks: WalletCallbacks,
) {
    if (state.archived.isEmpty()) return
    item(key = "archived-heading") {
        ArchivedHeading(count = state.archived.size, onToggle = callbacks.onToggleArchived)
    }
    if (state.archivedExpanded) {
        items(state.archived, key = { it.wallet.walletId.value }) { row ->
            WalletItemCard(item = row, onClick = { callbacks.onEdit(row) })
        }
    }
}

// FR-014: the total of the included, non-archived wallets
@Composable
private fun TotalCard(total: Money) {
    FintrackCard {
        Text(
            text = stringResource(R.string.wallet_total_label),
            style = FintrackTheme.typography.bodyMedium,
            color = FintrackTheme.colors.texts.onSurfaceVariant,
        )
        Text(
            text = MoneyFormatter.format(total),
            style = FintrackTheme.typography.numberLarge,
            color = FintrackTheme.colors.texts.onSurface,
        )
    }
}

// S-09: the heading of one group in the list
@Composable
private fun GroupHeading(group: WalletGroupKey) {
    Text(
        text = stringResource(WalletLabels.group(group)),
        style = FintrackTheme.typography.labelLarge,
        color = FintrackTheme.colors.texts.onSurfaceVariant,
        modifier = Modifier.padding(top = FintrackTheme.spacing.small),
    )
}

// S-09: "Diarsipkan (n)" with a count; tapping it shows or hides the archived wallets
@Composable
private fun ArchivedHeading(
    count: Int,
    onToggle: () -> Unit,
) {
    Text(
        text = stringResource(R.string.wallet_archived_title, count),
        style = FintrackTheme.typography.labelLarge,
        color = FintrackTheme.colors.texts.onSurfaceVariant,
        modifier = Modifier.clickable(onClick = onToggle).padding(vertical = FintrackTheme.spacing.small),
    )
}

private fun WalletUiState.editorOrNull(): WalletEditor? =
    when (this) {
        WalletUiState.Loading -> null
        is WalletUiState.Empty -> editor
        is WalletUiState.Success -> editor
    }

private fun WalletUiState.pickerOrNull(): WalletPicker? =
    when (this) {
        WalletUiState.Loading -> null
        is WalletUiState.Empty -> picker
        is WalletUiState.Success -> picker
    }
