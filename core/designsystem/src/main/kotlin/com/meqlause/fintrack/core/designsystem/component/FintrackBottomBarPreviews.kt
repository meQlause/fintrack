package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.meqlause.fintrack.core.designsystem.R

// DOC-09: the bar pinned to the bottom of a themed canvas, with the given tab selected
@Composable
internal fun BottomBarSample(
    selectedIndex: Int,
    darkTheme: Boolean,
) {
    ComponentCanvas(darkTheme = darkTheme) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            FintrackBottomBar(
                items = sampleBarItems(),
                selectedIndex = selectedIndex,
                callbacks = FintrackBottomBarCallbacks(onSelect = {}, onAddClick = {}),
            )
        }
    }
}

@Composable
private fun sampleBarItems(): List<FintrackBarItem> =
    listOf(
        FintrackBarItem(stringResource(R.string.sample_tab_home), R.drawable.ic_home),
        FintrackBarItem(stringResource(R.string.sample_tab_history), R.drawable.ic_history),
        FintrackBarItem(stringResource(R.string.sample_tab_investment), R.drawable.ic_trend),
        FintrackBarItem(stringResource(R.string.sample_tab_wallet), R.drawable.ic_wallet),
    )

@Preview(name = "BottomBar Beranda light", showBackground = true)
@Composable
internal fun BottomBarHomeLightPreview() = BottomBarSample(selectedIndex = 0, darkTheme = false)

@Preview(name = "BottomBar Beranda dark", showBackground = true)
@Composable
internal fun BottomBarHomeDarkPreview() = BottomBarSample(selectedIndex = 0, darkTheme = true)

@Preview(name = "BottomBar Riwayat light", showBackground = true)
@Composable
internal fun BottomBarHistoryLightPreview() = BottomBarSample(selectedIndex = 1, darkTheme = false)

@Preview(name = "BottomBar Riwayat dark", showBackground = true)
@Composable
internal fun BottomBarHistoryDarkPreview() = BottomBarSample(selectedIndex = 1, darkTheme = true)

@Preview(name = "BottomBar Investasi light", showBackground = true)
@Composable
internal fun BottomBarInvestmentLightPreview() = BottomBarSample(selectedIndex = 2, darkTheme = false)

@Preview(name = "BottomBar Investasi dark", showBackground = true)
@Composable
internal fun BottomBarInvestmentDarkPreview() = BottomBarSample(selectedIndex = 2, darkTheme = true)

@Preview(name = "BottomBar Dompet light", showBackground = true)
@Composable
internal fun BottomBarWalletLightPreview() = BottomBarSample(selectedIndex = 3, darkTheme = false)

@Preview(name = "BottomBar Dompet dark", showBackground = true)
@Composable
internal fun BottomBarWalletDarkPreview() = BottomBarSample(selectedIndex = 3, darkTheme = true)
