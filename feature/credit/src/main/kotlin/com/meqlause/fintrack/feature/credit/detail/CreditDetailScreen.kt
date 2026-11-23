package com.meqlause.fintrack.feature.credit.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.BackAction
import com.meqlause.fintrack.core.designsystem.component.HeaderTitle
import com.meqlause.fintrack.core.designsystem.component.ScreenBackHeader
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.ui.picker.DayGridDrawer
import com.meqlause.fintrack.core.ui.picker.PickerActions
import com.meqlause.fintrack.feature.credit.R
import com.meqlause.fintrack.feature.credit.component.BillCard
import com.meqlause.fintrack.feature.credit.component.CycleRows
import com.meqlause.fintrack.feature.credit.component.InstalmentSection
import com.meqlause.fintrack.feature.credit.component.LimitBar
import com.meqlause.fintrack.feature.credit.component.PayBillSheet

// FR-131 to FR-138, S-14: stateless credit wallet detail; header, bill card, limit, cycle rows, then the sheets
@Composable
internal fun CreditDetailScreen(
    state: CreditDetailUiState,
    callbacks: CreditDetailCallbacks,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize().background(FintrackTheme.colors.surfaces.background)) {
        CreditDetailHeader(state = state, onBack = callbacks.onBack)
        when (state) {
            CreditDetailUiState.Loading -> {
                Unit
            }

            CreditDetailUiState.Missing -> {
                CreditMissingNotice()
            }

            is CreditDetailUiState.Success -> {
                CreditDetailBody(state = state, callbacks = callbacks)
            }
        }
    }
}

// S-14: the back header, titled with the wallet name and badged with its type once loaded
@Composable
private fun CreditDetailHeader(
    state: CreditDetailUiState,
    onBack: () -> Unit,
) {
    val success = state as? CreditDetailUiState.Success
    ScreenBackHeader(
        title =
            HeaderTitle(
                text = success?.name.orEmpty(),
                badge = success?.let { stringResource(CreditLabels.type(it.type)) },
            ),
        back = BackAction(label = stringResource(R.string.credit_back), onBack = onBack),
    )
}

// S-14: shown when the id is not a credit wallet
@Composable
private fun CreditMissingNotice() {
    Text(
        text = stringResource(R.string.credit_missing),
        style = FintrackTheme.typography.bodyLarge,
        color = FintrackTheme.colors.texts.onSurfaceVariant,
        modifier = Modifier.padding(FintrackScreenSizes.screenGutter),
    )
}

// S-14: the header and the three cards; the day drawer and the pay sheet open over them
@Composable
private fun CreditDetailBody(
    state: CreditDetailUiState.Success,
    callbacks: CreditDetailCallbacks,
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        CreditDetailCards(state = state, callbacks = callbacks)
        state.picker?.let { day ->
            DayGridDrawer(
                title = stringResource(pickerTitle(day)),
                selectedDay = dayOf(state, day),
                actions = PickerActions(onPick = callbacks.onPickDay, onClose = callbacks.onDismissPicker),
            )
        }
        state.payDraft?.let { draft ->
            PayBillSheet(draft = draft, sources = state.sources, callbacks = callbacks)
        }
    }
}

// S-14: the bill, the limit, the cycle days and the active instalments, in one scrolling column
@Composable
private fun CreditDetailCards(
    state: CreditDetailUiState.Success,
    callbacks: CreditDetailCallbacks,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = FintrackScreenSizes.screenGutter)
                .padding(top = FintrackTheme.spacing.small, bottom = FintrackTheme.spacing.xxLarge),
    ) {
        BillCard(state = state, onPay = callbacks.onOpenPay)
        LimitBar(
            state = state,
            modifier = Modifier.padding(top = FintrackTheme.spacing.small + FintrackTheme.spacing.xxSmall),
        )
        CycleRows(terms = state.terms, onOpenPicker = callbacks.onOpenPicker)
        InstalmentSection(
            instalments = state.instalments,
            onOpenInstalment = callbacks.onOpenInstalment,
            onOpenAll = callbacks.onOpenInstalments,
        )
    }
}

internal fun pickerTitle(creditDay: CreditDay): Int =
    if (creditDay == CreditDay.STATEMENT) R.string.credit_statement_picker_title else R.string.credit_due_picker_title

internal fun dayOf(
    state: CreditDetailUiState.Success,
    creditDay: CreditDay,
): Int = if (creditDay == CreditDay.STATEMENT) state.terms.statementDay else state.terms.dueDay
