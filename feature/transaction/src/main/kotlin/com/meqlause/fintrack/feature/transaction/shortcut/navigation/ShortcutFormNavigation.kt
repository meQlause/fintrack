package com.meqlause.fintrack.feature.transaction.shortcut.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.meqlause.fintrack.feature.transaction.shortcut.ShortcutFormRoute

// DOC-09, FR-039: registers the shortcut form in the host graph; the app decides where it returns to
public object ShortcutFormNavigation {
    public fun register(
        builder: NavGraphBuilder,
        onDismiss: () -> Unit,
    ) {
        builder.composable<ShortcutFormDestination> { entry ->
            val destination = entry.toRoute<ShortcutFormDestination>()
            ShortcutFormRoute(quickEntryId = destination.quickEntryId, onDismiss = onDismiss)
        }
    }
}
