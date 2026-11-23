package com.meqlause.fintrack.feature.investment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.FintrackTabHeader
import com.meqlause.fintrack.core.designsystem.component.IconTile
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
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
                .padding(horizontal = FintrackScreenSizes.screenGutter)
                .padding(top = FintrackScreenSizes.screenTop),
    ) {
        FintrackTabHeader(
            title = stringResource(R.string.investment_tab_title),
            leading = { IconTile(iconRes = DesignR.drawable.ic_trend) },
            trailing = { AddButton(onAdd = callbacks.onAdd) },
        )
        InvestmentBody(
            portfolio = state.portfolio,
            onOpenAccount = callbacks.onOpenAccount,
            modifier = Modifier.weight(1f),
        )
        state.draft?.let { draft ->
            if (draft.kindPickerOpen) {
                KindPickerDrawer(selected = draft.kind, callbacks = callbacks)
            } else {
                CreateInvestmentSheet(draft = draft, callbacks = callbacks)
            }
        }
    }
}

// S-08: the "+" at the right of the header opens the creation sheet
@Composable
private fun AddButton(onAdd: () -> Unit) {
    IconButton(onClick = onAdd) {
        Icon(
            painter = painterResource(DesignR.drawable.ic_plus),
            contentDescription = stringResource(R.string.investment_add_description),
            tint = FintrackTheme.colors.texts.onSurface,
        )
    }
}
