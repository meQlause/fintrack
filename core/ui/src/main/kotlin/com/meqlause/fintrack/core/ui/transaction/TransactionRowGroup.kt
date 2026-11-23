package com.meqlause.fintrack.core.ui.transaction

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.meqlause.fintrack.core.designsystem.component.FintrackCard
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// DOC-09: the prototype .card holding .row lines, each line after the first starts with a 1 px outline divider
@Composable
fun TransactionRowGroup(
    rows: List<TransactionRowParams>,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    FintrackCard(modifier = modifier.fillMaxWidth(), contentPadding = PaddingValues()) {
        rows.forEachIndexed { index, row ->
            if (index > 0) {
                HorizontalDivider(
                    thickness = FintrackTheme.sizes.hairline,
                    color = FintrackTheme.colors.surfaces.outline,
                )
            }
            TransactionRow(params = row, callbacks = TransactionRowCallbacks(onClick = { onClick(index) }))
        }
    }
}
