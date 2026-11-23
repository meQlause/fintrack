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
                .padding(top = FintrackTheme.spacing.stack),
    ) {
        HomeHeader(
            hasPending = (state as? HomeUiState.Success)?.inbox?.count?.let { it > 0 } ?: false,
            callbacks = callbacks,
            modifier = Modifier.padding(horizontal = FintrackTheme.spacing.gutter),
        )
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
    val gutter = Modifier.padding(horizontal = FintrackTheme.spacing.gutter)
    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        HomeHero(state = state, callbacks = callbacks, modifier = Modifier.padding(top = FintrackTheme.spacing.hero))
        if (state.inbox.count > 0) {
            HomeInboxBanner(
                inbox = state.inbox,
                onClick = callbacks.onOpenInbox,
                modifier = gutter.padding(top = FintrackTheme.spacing.stack),
            )
        }
        HomeQuickEntry(
            shortcuts = state.shortcuts,
            callbacks = callbacks,
            modifier = Modifier.padding(top = FintrackTheme.spacing.section),
        )
        HomeCards(state = state, callbacks = callbacks)
        HomeRecentList(
            state = state,
            callbacks = callbacks,
            modifier = gutter.padding(top = FintrackTheme.spacing.block),
        )
        HomeShortcutUndo(state = state, callbacks = callbacks)
    }
}

// FR-137, FR-042: the Tagihan card and the category card, each shown only when it has rows
@Composable
private fun HomeCards(
    state: HomeUiState.Success,
    callbacks: HomeCallbacks,
) {
    val gutter = Modifier.padding(horizontal = FintrackTheme.spacing.gutter)
    if (state.bills.bills.isNotEmpty()) {
        HomeBillsCard(
            state = state,
            callbacks = callbacks,
            modifier = gutter.padding(top = FintrackTheme.spacing.stack),
        )
    }
    if (state.categories.isNotEmpty()) {
        HomeCategoryCard(
            state = state,
            onOpen = callbacks.onOpenCategories,
            modifier = gutter.padding(top = FintrackTheme.spacing.stack),
        )
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
