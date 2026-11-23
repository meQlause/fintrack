package com.meqlause.fintrack.feature.transaction.detail.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.meqlause.fintrack.feature.transaction.detail.TransactionDetailRoute

// FR-034, FR-035, S-06: registers the transaction detail; Ubah hands the id to the host, which opens the S-04 sheet
public fun NavGraphBuilder.transactionDetailDestination(
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
) {
    composable<TransactionDetailDestination> { entry ->
        val destination = entry.toRoute<TransactionDetailDestination>()
        TransactionDetailRoute(transactionId = destination.transactionId, onBack = onBack, onEdit = onEdit)
    }
}
