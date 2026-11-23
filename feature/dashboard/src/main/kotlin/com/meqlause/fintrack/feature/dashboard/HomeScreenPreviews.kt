package com.meqlause.fintrack.feature.dashboard

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

@Composable
private fun HomeSample(
    state: HomeUiState,
    darkTheme: Boolean,
) {
    FintrackTheme(darkTheme = darkTheme) {
        HomeScreen(state = state, callbacks = HomeCallbacks())
    }
}

// S-03: previews of every state and drawer, light and dark; the sample is the mockup seed of 2026-11-22
@Preview(name = "Beranda success light", showBackground = true, heightDp = 720)
@Composable
internal fun HomeSuccessLightPreview() = HomeSample(state = sampleSuccess(), darkTheme = false)

@Preview(name = "Beranda success dark", showBackground = true, heightDp = 720)
@Composable
internal fun HomeSuccessDarkPreview() = HomeSample(state = sampleSuccess(), darkTheme = true)

@Preview(name = "Beranda hidden light", showBackground = true, heightDp = 720)
@Composable
internal fun HomeHiddenLightPreview() = HomeSample(state = sampleSuccess(hidden = true), darkTheme = false)

@Preview(name = "Beranda empty light", showBackground = true, heightDp = 720)
@Composable
internal fun HomeEmptyLightPreview() = HomeSample(state = sampleSuccess(recent = emptyList()), darkTheme = false)

@Preview(name = "Beranda empty dark", showBackground = true, heightDp = 720)
@Composable
internal fun HomeEmptyDarkPreview() = HomeSample(state = sampleSuccess(recent = emptyList()), darkTheme = true)

@Preview(name = "Ringkasan drawer dark", showBackground = true, heightDp = 720)
@Composable
internal fun HomeSummaryDrawerDarkPreview() =
    HomeSample(state = sampleSuccess(drawer = HomeDrawer.SUMMARY), darkTheme = true)

@Preview(name = "Kategori drawer light", showBackground = true, heightDp = 720)
@Composable
internal fun HomeCategoriesDrawerLightPreview() =
    HomeSample(state = sampleSuccess(drawer = HomeDrawer.CATEGORIES), darkTheme = false)

// NFR-010: font scale 2.0 variant for the screenshot tests
@Preview(name = "Beranda font 200", showBackground = true, heightDp = 1200, fontScale = 2f)
@Composable
internal fun HomeFontScalePreview() = HomeSample(state = sampleSuccess(), darkTheme = false)
