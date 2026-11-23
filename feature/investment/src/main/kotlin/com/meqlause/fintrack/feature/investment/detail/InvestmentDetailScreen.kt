package com.meqlause.fintrack.feature.investment.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.EmptyState
import com.meqlause.fintrack.core.designsystem.component.EmptyStateCopy
import com.meqlause.fintrack.core.designsystem.component.ScreenBackHeader
import com.meqlause.fintrack.core.designsystem.component.ScreenGroup
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.domain.investment.InvestmentDetail
import com.meqlause.fintrack.core.model.InvestKind
import com.meqlause.fintrack.feature.investment.InvestmentLabels
import com.meqlause.fintrack.feature.investment.R
import com.meqlause.fintrack.feature.investment.component.ActivityRow
import com.meqlause.fintrack.feature.investment.component.ValuationDrawer
import com.meqlause.fintrack.core.designsystem.R as DesignR

// FR-173, S-20: the investment detail; loading is blank, a missing account shows the empty state
@Composable
internal fun InvestmentDetailScreen(
    state: InvestmentDetailUiState,
    callbacks: InvestmentDetailCallbacks,
    modifier: Modifier = Modifier,
) {
    val wallet = state.detail?.account?.wallet
    Column(modifier = modifier.fillMaxSize().background(FintrackTheme.colors.surfaces.background)) {
        ScreenBackHeader(
            title = wallet?.profile?.name.orEmpty(),
            backLabel = stringResource(R.string.investment_back),
            onBack = callbacks.onBack,
            badge = wallet?.let { stringResource(InvestmentLabels.kindRes(it.kind.investKind ?: InvestKind.OTHER)) },
        )
        DetailContent(state = state, callbacks = callbacks, modifier = Modifier.weight(1f))
    }
    state.valuation?.let { digits ->
        ValuationDrawer(digits = digits, callbacks = callbacks.valuation)
    }
}

// S-20: nothing while loading, the missing state when the account is gone, otherwise the detail list
@Composable
private fun DetailContent(
    state: InvestmentDetailUiState,
    callbacks: InvestmentDetailCallbacks,
    modifier: Modifier,
) {
    val detail = state.detail
    when {
        !state.loaded -> Unit
        detail == null -> MissingBody(modifier = modifier)
        else -> DetailList(detail = detail, callbacks = callbacks, modifier = modifier)
    }
}

// S-20: the account is missing or no longer an active investment
@Composable
private fun MissingBody(modifier: Modifier) {
    EmptyState(
        copy =
            EmptyStateCopy(
                title = stringResource(R.string.investment_missing_title),
                body = stringResource(R.string.investment_missing_body),
            ),
        iconRes = DesignR.drawable.ic_trend,
        modifier = modifier,
    )
}

// S-20: the value summary, then the timeline of transfers and valuations
@Composable
private fun DetailList(
    detail: InvestmentDetail,
    callbacks: InvestmentDetailCallbacks,
    modifier: Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding =
            PaddingValues(
                start = FintrackScreenSizes.screenGutter,
                end = FintrackScreenSizes.screenGutter,
                top = FintrackTheme.spacing.xSmall + FintrackTheme.spacing.xxSmall,
                bottom = FintrackTheme.spacing.xxLarge,
            ),
    ) {
        item { ValueSummary(valuation = detail.account.valuation) }
        item { InvestmentActions(callbacks = callbacks) }
        item {
            ScreenGroup(label = stringResource(R.string.investment_timeline_title)) {
                detail.activity.forEachIndexed { index, activity ->
                    ActivityRow(activity = activity, accountName = null, showDivider = index > 0)
                }
                if (detail.activity.isEmpty()) {
                    Text(
                        text = stringResource(R.string.investment_activity_none),
                        style = FintrackScreenText.style(size = 13f, weight = 400),
                        color = FintrackTheme.colors.texts.onSurfaceVariant,
                        modifier = Modifier.padding(FintrackScreenSizes.screenGutter),
                    )
                }
            }
        }
    }
}
