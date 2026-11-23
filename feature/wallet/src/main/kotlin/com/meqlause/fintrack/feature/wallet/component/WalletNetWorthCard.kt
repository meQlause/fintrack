package com.meqlause.fintrack.feature.wallet.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.ScreenCard
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.domain.networth.NetWorth
import com.meqlause.fintrack.feature.wallet.R

// BR-053, FR-161, S-09: the segmented Aset vs Utang bar and the two columns of parts (prototype .card, 16 px padding)
@Composable
internal fun WalletNetWorthCard(
    netWorth: NetWorth,
    modifier: Modifier = Modifier,
) {
    val parts = NetWorthParts.from(netWorth)
    ScreenCard(modifier = modifier) {
        Column(modifier = Modifier.padding(FintrackTheme.spacing.large)) {
            NetWorthBar(parts = parts.every)
            NetWorthColumns(parts = parts)
        }
    }
}

// BR-053: segments sized by their share, 2 px apart, in a 10 px pill; nothing to show leaves the bar empty
@Composable
private fun NetWorthBar(parts: List<NetWorthPart>) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(FintrackTheme.sizes.netWorthBarHeight)
                .clip(RoundedCornerShape(FintrackScreenSizes.netWorthBarCorner)),
        horizontalArrangement = Arrangement.spacedBy(FintrackScreenSizes.netWorthBarGap),
    ) {
        parts.filter { it.amount > 0L }.forEach { part ->
            Row(
                modifier =
                    Modifier
                        .weight(part.amount.toFloat())
                        .fillMaxHeight()
                        .background(FintrackTheme.colors.categoryColor(part.colorKey)),
            ) {}
        }
    }
}

// BR-053: the Aset and the Utang column side by side, with a hairline between
@Composable
private fun NetWorthColumns(parts: NetWorthParts) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(top = FintrackScreenSizes.rowPaddingHorizontal),
    ) {
        val assets =
            NetWorthSide(stringResource(R.string.wallet_networth_assets), parts.assetTotal, parts.assets, false)
        NetWorthColumn(
            side = assetSide(parts),
            modifier = Modifier.weight(1f).padding(end = FintrackScreenSizes.rowPaddingHorizontal),
        )
        VerticalDivider(thickness = FintrackTheme.sizes.hairline, color = FintrackTheme.colors.surfaces.outline)
        NetWorthColumn(
            side = debtSide(parts),
            modifier = Modifier.weight(1f).padding(start = FintrackScreenSizes.rowPaddingHorizontal),
        )
    }
}

// BR-053: the Aset column, its total in the income colour
@Composable
private fun assetSide(parts: NetWorthParts): NetWorthSide =
    NetWorthSide(
        title = stringResource(R.string.wallet_networth_assets),
        total = parts.assetTotal,
        rows = parts.assets,
        isDebt = false,
    )

// BR-053: the Utang column, its total in the expense colour
@Composable
private fun debtSide(parts: NetWorthParts): NetWorthSide =
    NetWorthSide(
        title = stringResource(R.string.wallet_networth_debts),
        total = parts.debtTotal,
        rows = parts.debts,
        isDebt = true,
    )
