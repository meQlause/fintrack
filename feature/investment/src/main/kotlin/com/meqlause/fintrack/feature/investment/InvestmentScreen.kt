package com.meqlause.fintrack.feature.investment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.FintrackButton
import com.meqlause.fintrack.core.designsystem.component.FintrackTabHeader
import com.meqlause.fintrack.core.designsystem.component.IconTile
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.feature.investment.component.CreateInvestmentSheet
import com.meqlause.fintrack.feature.investment.component.KindPickerDrawer
import com.meqlause.fintrack.core.designsystem.R as DesignR

// S-08: the Investasi tab; the header, the list, the add button and the creation sheet
@Composable
internal fun InvestmentScreen(
    state: InvestmentUiState,
    callbacks: InvestmentCallbacks,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(FintrackTheme.colors.surfaces.background)
                .padding(horizontal = FintrackTheme.spacing.large),
        verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
    ) {
        FintrackTabHeader(
            title = stringResource(R.string.investment_tab_title),
            leading = { IconTile(iconRes = DesignR.drawable.ic_trend) },
        )
        InvestmentBody(
            portfolio = state.portfolio,
            onOpenAccount = callbacks.onOpenAccount,
            modifier = Modifier.weight(1f),
        )
        FintrackButton(text = stringResource(R.string.investment_add), onClick = callbacks.onAdd)
        state.draft?.let { draft ->
            if (draft.kindPickerOpen) {
                KindPickerDrawer(selected = draft.kind, callbacks = callbacks)
            } else {
                CreateInvestmentSheet(draft = draft, callbacks = callbacks)
            }
        }
    }
}
