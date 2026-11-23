package com.meqlause.fintrack.feature.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.designsystem.R as DesignR

// DOC-09, S-03: "Catat cepat" opens the add sheet (S-04) through the host's navigation lambda
@Composable
internal fun HomeQuickEntry(
    onQuickEntry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.xSmall),
    ) {
        Text(
            text = stringResource(R.string.home_quick_title),
            style = FintrackTheme.typography.labelLarge,
            color = FintrackTheme.colors.texts.onSurfaceVariant,
        )
        Row(
            modifier =
                Modifier
                    .clip(FintrackTheme.shapes.full)
                    .background(FintrackTheme.colors.surfaces.surfaceVariant)
                    .clickable(role = Role.Button, onClick = onQuickEntry)
                    .padding(horizontal = FintrackTheme.spacing.medium, vertical = FintrackTheme.spacing.small),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
        ) {
            Icon(
                painter = painterResource(DesignR.drawable.ic_plus),
                contentDescription = stringResource(R.string.home_quick_add_description),
                tint = FintrackTheme.colors.texts.onSurface,
                modifier = Modifier.size(FintrackTheme.sizes.iconSmall),
            )
            Text(
                text = stringResource(R.string.home_quick_add),
                style = FintrackTheme.typography.labelLarge,
                color = FintrackTheme.colors.texts.onSurface,
            )
        }
    }
}
