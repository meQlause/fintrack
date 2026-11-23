package com.meqlause.fintrack.feature.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import com.meqlause.fintrack.core.designsystem.theme.FintrackAppBadges
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.designsystem.R as DesignR

// FR-084, S-03: the "N terdeteksi" banner under the hero; overlapping app badges, the count and a chevron
@Composable
internal fun HomeInboxBanner(
    inbox: HomeInbox,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val brands = FintrackTheme.colors.brands
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(FintrackScreenSizes.bannerHeight)
                .clip(FintrackTheme.shapes.medium)
                .background(brands.primaryContainer)
                .clickable(role = Role.Button, onClick = onClick)
                .padding(start = FintrackScreenSizes.sheetTop, end = FintrackScreenSizes.fieldPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FintrackScreenSizes.sheetTop),
    ) {
        BannerBadges(sources = inbox.sources)
        Text(
            text = stringResource(R.string.home_inbox_banner, inbox.count),
            style = FintrackScreenText.style(size = 13.5f, weight = 700),
            color = brands.onPrimaryContainer,
            modifier = Modifier.weight(1f),
        )
        Icon(
            painter = painterResource(DesignR.drawable.ic_chevron_right),
            contentDescription = null,
            tint = brands.onPrimaryContainer,
            modifier = Modifier.size(FintrackScreenSizes.chevron),
        )
    }
}

@Composable
private fun BannerBadges(sources: List<String>) {
    val ring = FintrackTheme.colors.brands.primaryContainer
    val shape = RoundedCornerShape(FintrackScreenSizes.bannerBadgeCorner)
    Row(modifier = Modifier.padding(start = FintrackTheme.spacing.xSmall)) {
        sources.forEach { source ->
            Box(
                modifier =
                    Modifier
                        .offset(x = -FintrackScreenSizes.bannerOverlap)
                        .size(FintrackScreenSizes.bannerBadge)
                        .border(FintrackScreenSizes.checkBoxBorder, ring, shape)
                        .clip(shape)
                        .background(FintrackAppBadges.colorOf(source)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = FintrackAppBadges.letterOf(source),
                    style = FintrackScreenText.style(size = 11f, weight = 700),
                    color = Color.White,
                )
            }
        }
    }
}
