package com.meqlause.fintrack.feature.inbox.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.meqlause.fintrack.feature.inbox.InboxRoute

// FR-084 … FR-091, S-07: registers Kotak Masuk; back returns to the screen it was opened from
public fun NavGraphBuilder.inboxDestination(onBack: () -> Unit) {
    composable<InboxDestination> { InboxRoute(onBack = onBack) }
}
