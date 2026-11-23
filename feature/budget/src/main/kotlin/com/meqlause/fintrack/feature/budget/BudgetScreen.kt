package com.meqlause.fintrack.feature.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.EmptyState
import com.meqlause.fintrack.core.designsystem.component.EmptyStateCopy
import com.meqlause.fintrack.core.designsystem.component.FintrackButton
import com.meqlause.fintrack.core.designsystem.component.FintrackCard
import com.meqlause.fintrack.core.designsystem.component.FintrackTabHeader
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.feature.budget.component.BudgetEditorSheet
import com.meqlause.fintrack.feature.budget.component.BudgetStatusCard
import com.meqlause.fintrack.core.designsystem.R as DesignR

// FR-070..074, S-11: stateless Anggaran screen; the overall card, rows and editor come from BudgetUiState
@Composable
internal fun BudgetScreen(
    state: BudgetUiState,
    callbacks: BudgetCallbacks,
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
            title = stringResource(R.string.budget_title),
            leading = { BackLabel(onBack = callbacks.onBack) },
        )
        BudgetBody(state = state, callbacks = callbacks, modifier = Modifier.weight(1f))
        FintrackButton(text = stringResource(R.string.budget_add), onClick = callbacks.onAdd)
        state.editor?.let { editor ->
            BudgetEditorSheet(editor = editor, categories = state.categories, callbacks = callbacks)
        }
    }
}

// S-11: Loading shows nothing, no budget at all shows the empty state, otherwise the overall card and the rows
@Composable
private fun BudgetBody(
    state: BudgetUiState,
    callbacks: BudgetCallbacks,
    modifier: Modifier,
) {
    val overall = state.overall
    when {
        !state.loaded -> {
            Column(modifier = modifier) {}
        }

        overall == null && state.rows.isEmpty() -> {
            EmptyState(
                copy =
                    EmptyStateCopy(
                        title = stringResource(R.string.budget_empty_title),
                        body = stringResource(R.string.budget_empty_body),
                    ),
                iconRes = DesignR.drawable.ic_plus,
                modifier = modifier,
            )
        }

        else -> {
            LazyColumn(
                modifier = modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
            ) {
                overall?.let { row ->
                    item(key = "overall") { BudgetRowCard(row, callbacks) }
                }
                items(state.rows, key = { it.target.categoryId?.value ?: 0L }) { row ->
                    BudgetRowCard(row, callbacks)
                }
            }
        }
    }
}

// FR-071: one budget line; its title is the category name, or the overall label when there is none
@Composable
private fun BudgetRowCard(
    row: BudgetRow,
    callbacks: BudgetCallbacks,
) {
    BudgetStatusCard(
        title = row.target.categoryName ?: stringResource(R.string.budget_overall),
        status = row.status,
        onClick = { callbacks.onEdit(row) },
    )
}

// S-11: the back action shown in the tab header
@Composable
private fun BackLabel(onBack: () -> Unit) {
    FintrackCard(onClick = onBack) {
        Text(
            text = stringResource(R.string.budget_back),
            style = FintrackTheme.typography.labelLarge,
            color = FintrackTheme.colors.texts.onSurface,
        )
    }
}
