package com.meqlause.fintrack.feature.credit.component

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.FormRow
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.CreditTerms
import com.meqlause.fintrack.feature.credit.R
import com.meqlause.fintrack.feature.credit.detail.CreditDay

// FR-138, S-14: the statement and due days; each row opens the day grid drawer and edits the day at any time
@Composable
internal fun CycleRows(
    terms: CreditTerms,
    onOpenPicker: (CreditDay) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.credit_cycle_title),
            style = FintrackTheme.typography.labelLarge,
            color = FintrackTheme.colors.texts.onSurfaceVariant,
        )
        FormRow(
            label = stringResource(R.string.credit_cycle_statement),
            value = stringResource(R.string.credit_cycle_statement_value, terms.statementDay),
            onClick = { onOpenPicker(CreditDay.STATEMENT) },
        )
        FormRow(
            label = stringResource(R.string.credit_cycle_due),
            value = stringResource(R.string.credit_cycle_due_value, terms.dueDay),
            onClick = { onOpenPicker(CreditDay.DUE) },
        )
    }
}
