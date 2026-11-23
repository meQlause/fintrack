package com.meqlause.fintrack.feature.dashboard.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.meqlause.fintrack.feature.dashboard.HomeEntryLinks
import com.meqlause.fintrack.feature.dashboard.HomeHostLinks
import com.meqlause.fintrack.feature.dashboard.HomeRoute

/** DOC-09, FR-040..043: registers Beranda in the host graph; every step out of S-03 is handed to the host. */
public object HomeNavigation {
    /** DOC-09: adds the Beranda destination with the host links and the entries into the money screens. */
    public fun register(
        builder: NavGraphBuilder,
        host: HomeHostLinks,
        entry: HomeEntryLinks,
    ) {
        builder.composable<HomeDestination> {
            HomeRoute(host = host, entry = entry)
        }
    }
}
