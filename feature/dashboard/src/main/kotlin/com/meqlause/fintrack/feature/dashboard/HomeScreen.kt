package com.meqlause.fintrack.feature.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// DOC-03, S-03: stateless Beranda; the header stays fixed while the body scrolls and the drawers sit above it
@Composable
internal fun HomeScreen(
    state: HomeUiState,
    callbacks: HomeCallbacks,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(FintrackTheme.colors.surfaces.background)
                .padding(horizontal = FintrackTheme.spacing.large, vertical = FintrackTheme.spacing.large),
        verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.medium),
    ) {
        HomeHeader(onSettingsClick = callbacks.onOpenSettings, onInboxClick = callbacks.onOpenInbox)
        when (state) {
            HomeUiState.Loading -> HomeSkeleton(modifier = Modifier.weight(1f))
            is HomeUiState.Success -> HomeBody(state = state, callbacks = callbacks, modifier = Modifier.weight(1f))
        }
    }
    if (state is HomeUiState.Success) {
        HomeDrawers(state = state, callbacks = callbacks)
    }
}

// FR-040..043: the scrolling blocks in S-03 order: hero, quick entry, category card, recent transactions
@Composable
private fun HomeBody(
    state: HomeUiState.Success,
    callbacks: HomeCallbacks,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.medium),
    ) {
        HomeHero(state = state, callbacks = callbacks)
        HomeQuickEntry(
            shortcuts = state.shortcuts,
            onNewEntry = callbacks.onQuickEntry,
            onApplyShortcut = callbacks.onApplyShortcut,
            onManage = callbacks.onOpenShortcuts,
        )
        if (state.bills.bills.isNotEmpty()) {
            HomeBillsCard(
                bills = state.bills,
                hidden = state.hidden,
                onOpenCreditWallet = callbacks.onOpenCreditWallet,
                onOpenInstalments = callbacks.onOpenInstalments,
            )
        }
        if (state.categories.isNotEmpty()) {
            HomeCategoryCard(state = state, onOpen = callbacks.onOpenCategories)
        }
        HomeRecentList(state = state, callbacks = callbacks)
        HomeShortcutUndo(state = state, callbacks = callbacks)
    }
}

// S-03 Loading: placeholder blocks in place of the cards until the first snapshot arrives
@Composable
private fun HomeSkeleton(modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.medium)) {
        HomeSkeletonBlock(modifier = Modifier.weight(HERO_WEIGHT))
        HomeSkeletonBlock(modifier = Modifier.weight(CARD_WEIGHT))
        HomeSkeletonBlock(modifier = Modifier.weight(CARD_WEIGHT))
    }
}

@Composable
private fun HomeDrawers(
    state: HomeUiState.Success,
    callbacks: HomeCallbacks,
) {
    when (state.drawer) {
        HomeDrawer.SUMMARY -> {
            HomeSummaryDrawer(
                state = state,
                onClose = callbacks.onCloseDrawer,
                onOpenWallets = callbacks.onOpenWallets,
            )
        }

        HomeDrawer.CATEGORIES -> {
            HomeCategoriesDrawer(state = state, onClose = callbacks.onCloseDrawer)
        }

        HomeDrawer.SHORTCUTS -> {
            HomeShortcutDrawer(state = state, callbacks = callbacks)
        }

        null -> {
            Unit
        }
    }
}

private const val HERO_WEIGHT = 2f
private const val CARD_WEIGHT = 1f
