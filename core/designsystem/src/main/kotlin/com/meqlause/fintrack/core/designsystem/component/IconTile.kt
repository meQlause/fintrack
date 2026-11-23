package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.meqlause.fintrack.core.designsystem.R
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// DOC-09: 32 dp soft-teal tile with a tinted icon, the tab header mark next to 20 sp titles
@Composable
fun IconTile(
    iconRes: Int,
    modifier: Modifier = Modifier,
) {
    val brands = FintrackTheme.colors.brands
    Box(
        modifier =
            modifier
                .size(FintrackTheme.sizes.iconTile)
                .clip(FintrackTheme.shapes.shortcutIcon)
                .background(brands.primaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = brands.onPrimaryContainer,
            modifier = Modifier.size(FintrackTheme.sizes.iconSmall),
        )
    }
}

@Composable
internal fun IconTileSample() {
    IconTile(iconRes = R.drawable.ic_wallet)
}

@Preview(name = "IconTile light", showBackground = true)
@Composable
internal fun IconTileLightPreview() {
    ComponentCanvas(darkTheme = false) { IconTileSample() }
}

@Preview(name = "IconTile dark", showBackground = true)
@Composable
internal fun IconTileDarkPreview() {
    ComponentCanvas(darkTheme = true) { IconTileSample() }
}
