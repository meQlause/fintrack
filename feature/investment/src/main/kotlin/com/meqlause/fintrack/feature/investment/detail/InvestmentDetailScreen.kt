package com.meqlause.fintrack.feature.investment.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.EmptyState
import com.meqlause.fintrack.core.designsystem.component.EmptyStateCopy
import com.meqlause.fintrack.core.designsystem.component.FintrackButton
import com.meqlause.fintrack.core.designsystem.component.FintrackTabHeader
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.domain.investment.InvestmentAccount
import com.meqlause.fintrack.core.domain.investment.InvestmentDetail
import com.meqlause.fintrack.core.domain.investment.InvestmentValuation
import com.meqlause.fintrack.core.ui.format.AmountSign
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import com.meqlause.fintrack.feature.investment.InvestmentDates
import com.meqlause.fintrack.feature.investment.R
import com.meqlause.fintrack.feature.investment.SectionTitle
import com.meqlause.fintrack.feature.investment.component.ActivityRow
import com.meqlause.fintrack.feature.investment.component.GainText
import com.meqlause.fintrack.feature.investment.component.ValuationDrawer
import java.time.Instant
import com.meqlause.fintrack.core.designsystem.R as DesignR

// FR-173, S-20: the investment detail; loading is blank, a missing account shows the empty state
@Composable
internal fun InvestmentDetailScreen(
    state: InvestmentDetailUiState,
    callbacks: InvestmentDetailCallbacks,
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
            title =
                state.detail
                    ?.account
                    ?.wallet
                    ?.profile
                    ?.name
                    .orEmpty(),
            leading = { BackButton(onBack = callbacks.onBack) },
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
        verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
    ) {
        item { ValueSummary(account = detail.account, callbacks = callbacks) }
        item { SectionTitle(text = stringResource(R.string.investment_timeline_title)) }
        if (detail.activity.isEmpty()) {
            item { Text(text = stringResource(R.string.investment_activity_none)) }
        }
        items(detail.activity) { activity -> ActivityRow(activity = activity, subtitle = "") }
    }
}

// S-20: "Nilai sekarang", the gain, the last update, modal and gain, then the three actions
@Composable
private fun ValueSummary(
    account: InvestmentAccount,
    callbacks: InvestmentDetailCallbacks,
) {
    val colors = FintrackTheme.colors
    val typography = FintrackTheme.typography
    val valuation = account.valuation
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.investment_value_label),
            style = typography.labelMedium,
            color = colors.texts.onSurfaceVariant,
        )
        Text(
            text = MoneyFormatter.format(valuation.currentValue),
            style = typography.numberHero,
            color = colors.texts.onSurface,
            maxLines = 1,
        )
        GainText(valuation = valuation, style = typography.bodyMedium)
        Text(
            text = lastUpdateText(valuation.lastValuedAt),
            modifier = Modifier.padding(top = FintrackTheme.spacing.xxSmall),
            style = typography.labelMedium,
            color = colors.texts.onSurfaceVariant,
        )
        StatsRow(valuation = valuation)
        ActionRow(callbacks = callbacks)
    }
}

// S-20: the modal and the gain side by side
@Composable
private fun StatsRow(valuation: InvestmentValuation) {
    Row(modifier = Modifier.fillMaxWidth().padding(top = FintrackTheme.spacing.medium)) {
        StatColumn(
            label = stringResource(R.string.investment_modal_label),
            value = MoneyFormatter.format(valuation.modal),
            modifier = Modifier.weight(1f),
        )
        StatColumn(
            label = stringResource(R.string.investment_gain_label),
            value = MoneyFormatter.formatSigned(valuation.gain, AmountSign.PLUS),
            modifier = Modifier.weight(1f),
        )
    }
}

// S-20: the update, add and withdraw actions
@Composable
private fun ActionRow(callbacks: InvestmentDetailCallbacks) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = FintrackTheme.spacing.medium),
        horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
    ) {
        FintrackButton(
            text = stringResource(R.string.investment_action_update),
            onClick = callbacks.onOpenValuation,
            modifier = Modifier.weight(1f),
        )
        FintrackButton(
            text = stringResource(R.string.investment_action_add),
            onClick = callbacks.onTransfer,
            modifier = Modifier.weight(1f),
        )
        FintrackButton(
            text = stringResource(R.string.investment_action_withdraw),
            onClick = callbacks.onTransfer,
            modifier = Modifier.weight(1f),
        )
    }
}

// FR-172: "Terakhir update" with the date of the latest valuation, or a note that there is none yet
@Composable
private fun lastUpdateText(lastValuedAt: Instant?): String =
    if (lastValuedAt == null) {
        stringResource(R.string.investment_never_updated)
    } else {
        stringResource(R.string.investment_last_update, InvestmentDates.shortDate(lastValuedAt))
    }

// S-20: one labelled figure in the stats row
@Composable
private fun StatColumn(
    label: String,
    value: String,
    modifier: Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = FintrackTheme.typography.labelMedium,
            color = FintrackTheme.colors.texts.onSurfaceVariant,
        )
        Text(
            text = value,
            style = FintrackTheme.typography.numberMedium,
            color = FintrackTheme.colors.texts.onSurface,
            maxLines = 1,
        )
    }
}

// S-20: the back arrow in the header; the chevron is mirrored to point left
@Composable
private fun BackButton(onBack: () -> Unit) {
    Icon(
        painter = painterResource(DesignR.drawable.ic_chevron_right),
        contentDescription = stringResource(R.string.investment_back),
        modifier = Modifier.size(FintrackTheme.sizes.iconMedium).rotate(BACK_ROTATION).clickable(onClick = onBack),
        tint = FintrackTheme.colors.texts.onSurface,
    )
}

private const val BACK_ROTATION = 180f
