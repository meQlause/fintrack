package com.meqlause.fintrack.feature.transaction.edit.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.meqlause.fintrack.feature.transaction.edit.EditTransactionRoute

// DOC-09, FR-030..FR-034: registers S-04 in the host graph; the app decides where the sheet returns to
public fun NavGraphBuilder.editTransactionDestination(onDismiss: () -> Unit) {
    composable<EditTransactionDestination> { entry ->
        val destination = entry.toRoute<EditTransactionDestination>()
        EditTransactionRoute(
            transactionId = destination.transactionId,
            quickEntryId = destination.quickEntryId,
            onDismiss = onDismiss,
        )
    }
}
