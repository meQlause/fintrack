package com.meqlause.fintrack.feature.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.EmptyState
import com.meqlause.fintrack.core.designsystem.component.EmptyStateCopy
import com.meqlause.fintrack.core.designsystem.component.FintrackButton
import com.meqlause.fintrack.core.designsystem.component.FintrackTabHeader
import com.meqlause.fintrack.core.designsystem.component.IconTile
import com.meqlause.fintrack.core.designsystem.component.ScreenCard
import com.meqlause.fintrack.core.designsystem.component.ScreenDashedButton
import com.meqlause.fintrack.core.designsystem.component.ScreenGroup
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import com.meqlause.fintrack.feature.wallet.component.WalletEditorSheet
import com.meqlause.fintrack.feature.wallet.component.WalletItemRow
import com.meqlause.fintrack.feature.wallet.component.WalletLoanRow
import com.meqlause.fintrack.feature.wallet.component.WalletNetWorthCard
import com.meqlause.fintrack.feature.wallet.component.WalletPickerOverlay
import com.meqlause.fintrack.feature.wallet.component.WalletRowActions
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
                .padding(horizontal = FintrackScreenSizes.screenGutter)
                .padding(top = FintrackScreenSizes.screenTop),
    ) {
        FintrackTabHeader(
            title = stringResource(R.string.wallet_tab_title),
            leading = { IconTile(iconRes = DesignR.drawable.ic_wallet) },
        )
        WalletBody(state = state, callbacks = callbacks, modifier = Modifier.weight(1f))
        state.editorOrNull()?.let { draft ->
            val open = state.pickerOrNull()
            if (open == null) {
                WalletEditorSheet(draft = draft, callbacks = callbacks)
            } else {
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
            Column(modifier = modifier) {
                EmptyState(
                    copy =
                        EmptyStateCopy(
                            title = stringResource(R.string.wallet_empty_title),
                            body = stringResource(R.string.wallet_empty_body),
                        ),
                    iconRes = DesignR.drawable.ic_wallet,
                    modifier = Modifier.weight(1f),
                )
                FintrackButton(text = stringResource(R.string.wallet_add), onClick = callbacks.onAdd)
            }
        }

        is WalletUiState.Success -> {
            WalletList(state = state, callbacks = callbacks, modifier = modifier)
        }
    }
}

// S-09: Kekayaan bersih, its card, the groups with their balances, Pinjaman, the archive and the dashed add button
@Composable
private fun WalletList(
    state: WalletUiState.Success,
    callbacks: WalletCallbacks,
    modifier: Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(bottom = FintrackScreenSizes.screenBottom),
    ) {
        item(key = "net-worth") { NetWorthHero(total = state.netWorth.total) }
        item(key = "net-worth-card") {
            WalletNetWorthCard(
                netWorth = state.netWorth,
                modifier = Modifier.padding(top = FintrackScreenSizes.rowPaddingHorizontal),
            )
        }
        state.sections.forEach { section ->
            item(key = "group-${section.group.name}") {
                ScreenGroup(label = stringResource(WalletLabels.group(section.group))) {
                    WalletRows(items = section.items, callbacks = callbacks)
                }
            }
        }
        archivedItems(state = state, callbacks = callbacks)
        footerItems(state = state, callbacks = callbacks)
    }
}

// FR-150, S-09: the Pinjaman card and the dashed add button close the list
private fun LazyListScope.footerItems(
    state: WalletUiState.Success,
    callbacks: WalletCallbacks,
) {
    item(key = "loans") {
        WalletLoanRow(
            netWorth = state.netWorth,
            onOpen = callbacks.onOpenLoans,
            modifier = Modifier.padding(top = FintrackScreenSizes.groupGap),
        )
    }
    item(key = "add") {
        ScreenDashedButton(
            text = stringResource(R.string.wallet_add_short),
            onClick = callbacks.onAdd,
            modifier = Modifier.padding(top = FintrackScreenSizes.rowPaddingHorizontal),
        )
    }
}

// FR-012, S-09: archived wallets sit under a heading that opens and closes the section
private fun LazyListScope.archivedItems(
    state: WalletUiState.Success,
    callbacks: WalletCallbacks,
) {
    if (state.archived.isEmpty()) return
    item(key = "archived") {
        Column(modifier = Modifier.padding(top = FintrackScreenSizes.groupGap)) {
            Text(
                text = stringResource(R.string.wallet_archived_title, state.archived.size),
                style = FintrackScreenText.label(),
                color = FintrackTheme.colors.texts.onSurfaceVariant,
                modifier = Modifier.clickable(onClick = callbacks.onToggleArchived),
            )
            if (state.archivedExpanded) {
                ScreenCard(modifier = Modifier.padding(top = FintrackScreenSizes.groupLabelBottom)) {
                    WalletRows(items = state.archived, callbacks = callbacks)
                }
            }
        }
    }
}

// S-09: the rows of one group card; the divider is each row's top border except the first
@Composable
private fun WalletRows(
    items: List<WalletItem>,
    callbacks: WalletCallbacks,
) {
    items.forEachIndexed { index, row ->
        WalletItemRow(
            item = row,
            showDivider = index > 0,
            actions =
                WalletRowActions(onOpen = {
                    callbacks.onOpenDetail(row.wallet.walletId, row.wallet.kind.type)
                }, onEdit = { callbacks.onEdit(row) }),
        )
    }
}

// S-09: "Kekayaan bersih" label over the 30 px total, 18 px under the header
@Composable
private fun NetWorthHero(total: Money) {
    Column(modifier = Modifier.padding(top = FintrackTheme.spacing.large + FintrackTheme.spacing.xxSmall)) {
        Text(
            text = stringResource(R.string.wallet_networth_label),
            style = FintrackScreenText.label(),
            color = FintrackTheme.colors.texts.onSurfaceVariant,
        )
        Text(
            text = MoneyFormatter.format(total),
            style = FintrackScreenText.netWorth(),
            color = FintrackTheme.colors.texts.onSurface,
            modifier = Modifier.padding(top = FintrackTheme.spacing.xxSmall),
        )
    }
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
