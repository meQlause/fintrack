package com.meqlause.fintrack.feature.inbox.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.meqlause.fintrack.feature.inbox.InboxRoute

// FR-084 … FR-091, S-07: registers Kotak Masuk; back returns to the screen it was opened from
public object InboxNavigation {
    public fun register(
        builder: NavGraphBuilder,
        onBack: () -> Unit,
    ) {
        builder.composable<InboxDestination> { InboxRoute(onBack = onBack) }
    }
}
