package com.meqlause.fintrack.feature.transaction.history.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.meqlause.fintrack.feature.transaction.history.HistoryRoute

// DOC-09, FR-036, FR-037: registers S-05 in the host graph; the app decides where a tapped row leads
public fun NavGraphBuilder.historyDestination(onOpenTransaction: (Long) -> Unit) {
    composable<HistoryDestination> { HistoryRoute(onOpenTransaction = onOpenTransaction) }
}
