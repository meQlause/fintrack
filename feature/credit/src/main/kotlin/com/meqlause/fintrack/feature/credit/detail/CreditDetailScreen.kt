package com.meqlause.fintrack.feature.credit.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.FormRow
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.ui.picker.DayGridDrawer
import com.meqlause.fintrack.feature.credit.R
import com.meqlause.fintrack.feature.credit.component.BillCard
import com.meqlause.fintrack.feature.credit.component.CycleRows
import com.meqlause.fintrack.feature.credit.component.LimitBar
import com.meqlause.fintrack.feature.credit.component.PayBillSheet

// FR-131 to FR-138, S-14: stateless credit wallet detail; header, bill card, limit, cycle rows, then the sheets
@Composable
internal fun CreditDetailScreen(
    state: CreditDetailUiState,
    callbacks: CreditDetailCallbacks,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(FintrackTheme.colors.surfaces.background)
                .padding(horizontal = FintrackTheme.spacing.large),
    ) {
        Text(
            text = stringResource(R.string.credit_back),
            style = FintrackTheme.typography.bodyMedium,
            color = FintrackTheme.colors.brands.primary,
            modifier = Modifier.clickable(onClick = callbacks.onBack).padding(vertical = FintrackTheme.spacing.small),
        )
        when (state) {
            CreditDetailUiState.Loading -> {
                Unit
            }

            CreditDetailUiState.Missing -> {
                Text(
                    text = stringResource(R.string.credit_missing),
                    style = FintrackTheme.typography.bodyLarge,
                    color = FintrackTheme.colors.texts.onSurfaceVariant,
                )
            }

            is CreditDetailUiState.Success -> {
                CreditDetailBody(state = state, callbacks = callbacks)
            }
        }
    }
}

// S-14: the header and the three cards; the day drawer and the pay sheet open over them
@Composable
private fun CreditDetailBody(
    state: CreditDetailUiState.Success,
    callbacks: CreditDetailCallbacks,
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
        ) {
            CreditHeader(state = state)
            BillCard(state = state, onPay = callbacks.onOpenPay)
            LimitBar(state = state)
            CycleRows(terms = state.terms, onOpenPicker = callbacks.onOpenPicker)
        }
        state.picker?.let { day ->
            DayGridDrawer(
                title = stringResource(pickerTitle(day)),
                selectedDay = dayOf(state, day),
                onPick = callbacks.onPickDay,
                onClose = callbacks.onDismissPicker,
            )
        }
        state.pay?.let { draft ->
            PayBillSheet(draft = draft, sources = state.sources, callbacks = callbacks)
        }
    }
}

// S-14: the card name and its type badge (FR-139)
@Composable
private fun CreditHeader(state: CreditDetailUiState.Success) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = state.name,
            style = FintrackTheme.typography.titleMedium,
            color = FintrackTheme.colors.texts.onSurface,
        )
        Text(
            text = stringResource(CreditLabels.type(state.type)),
            style = FintrackTheme.typography.bodyMedium,
            color = FintrackTheme.colors.texts.onSurfaceVariant,
        )
    }
}

private fun pickerTitle(day: CreditDay): Int =
    if (day == CreditDay.STATEMENT) R.string.credit_statement_picker_title else R.string.credit_due_picker_title

private fun dayOf(
    state: CreditDetailUiState.Success,
    day: CreditDay,
): Int = if (day == CreditDay.STATEMENT) state.terms.statementDay else state.terms.dueDay
