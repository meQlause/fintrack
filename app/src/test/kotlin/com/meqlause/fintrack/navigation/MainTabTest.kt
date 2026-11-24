package com.meqlause.fintrack.navigation

import androidx.navigation.NavDestination
import com.meqlause.fintrack.feature.dashboard.navigation.HomeDestination
import com.meqlause.fintrack.feature.settings.navigation.SettingsDestination
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

// DOC-09: the floating bottom bar belongs to the four main tabs only
class MainTabTest {
    private fun destinationOf(route: String): NavDestination = NavDestination("test").apply { this.route = route }

    @Test
    fun `given a main tab route when asked then the bottom bar is shown (DOC-09)`() {
        assertTrue(MainTab.showsBarOn(destinationOf(HomeDestination::class.java.name)))
    }

    @Test
    fun `given a sub-screen route when asked then the bottom bar is hidden (DOC-09)`() {
        assertFalse(MainTab.showsBarOn(destinationOf(SettingsDestination::class.java.name)))
        assertFalse(MainTab.showsBarOn(null))
    }
}
