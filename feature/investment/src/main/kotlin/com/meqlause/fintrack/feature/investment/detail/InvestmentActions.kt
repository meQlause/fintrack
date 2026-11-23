package com.meqlause.fintrack.feature.investment.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.ScreenCard
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.feature.investment.R
import com.meqlause.fintrack.core.designsystem.R as DesignR

// S-20: Update nilai, Tambah dana and Tarik as three equal cards, an icon over a 12 px label
@Composable
internal fun InvestmentActions(callbacks: InvestmentDetailCallbacks) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = FintrackTheme.spacing.small + FintrackTheme.spacing.xxSmall),
        horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
    ) {
        InvestmentAction(
            text = stringResource(R.string.investment_action_update),
            iconRes = DesignR.drawable.ic_edit,
            onClick = callbacks.onOpenValuation,
            modifier = Modifier.weight(1f),
        )
        InvestmentAction(
            text = stringResource(R.string.investment_action_add),
            iconRes = DesignR.drawable.ic_plus,
            onClick = callbacks.onTransfer,
            modifier = Modifier.weight(1f),
        )
        InvestmentAction(
            text = stringResource(R.string.investment_action_withdraw),
            iconRes = DesignR.drawable.ic_download,
            onClick = callbacks.onTransfer,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun InvestmentAction(
    text: String,
    iconRes: Int,
    onClick: () -> Unit,
    modifier: Modifier,
) {
    ScreenCard(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth().height(FintrackScreenSizes.actionTall).clickable(onClick = onClick),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(FintrackScreenSizes.actionTallGap, Alignment.CenterVertically),
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = FintrackTheme.colors.texts.onSurface,
                modifier = Modifier.size(FintrackScreenSizes.actionTallGlyph),
            )
            Text(
                text = text,
                style = FintrackScreenText.style(size = 12f, weight = 600),
                color = FintrackTheme.colors.texts.onSurface,
            )
        }
    }
}
