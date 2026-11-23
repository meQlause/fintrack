package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// DOC-09: shared tab header: a 32 dp leading mark (logo on Beranda, tinted tile elsewhere) and a 20 sp title
@Composable
fun FintrackTabHeader(
    title: String,
    modifier: Modifier = Modifier,
    leading: @Composable () -> Unit,
    trailing: @Composable () -> Unit = {},
) {
    Row(
        modifier = modifier.fillMaxWidth().heightIn(min = FintrackTheme.sizes.tabHeaderHeight),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
    ) {
        leading()
        // NFR-010: the title is a heading for screen-reader navigation; the row grows at font scale 2.0
        Text(
            text = title,
            modifier = Modifier.semantics { heading() },
            style = FintrackTheme.typography.tabTitle,
            color = FintrackTheme.colors.texts.onSurface,
        )
        Spacer(modifier = Modifier.weight(1f))
        trailing()
    }
}
