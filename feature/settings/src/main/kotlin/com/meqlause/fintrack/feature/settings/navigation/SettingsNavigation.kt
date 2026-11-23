package com.meqlause.fintrack.feature.settings.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.meqlause.fintrack.feature.settings.SettingsLinks
import com.meqlause.fintrack.feature.settings.SettingsRoute
import com.meqlause.fintrack.feature.settings.SettingsSecurityCallbacks

/** DOC-09, FR-100, FR-120, FR-121: registers Pengaturan in the host graph; the app wires its outgoing rows. */
public object SettingsNavigation {
    /** DOC-09: adds the Pengaturan destination with the rows that open another screen and the PIN rows. */
    public fun register(
        builder: NavGraphBuilder,
        links: SettingsLinks,
        pins: SettingsSecurityCallbacks,
    ) {
        builder.composable<SettingsDestination> {
            SettingsRoute(links = links, pins = pins)
        }
    }
}
