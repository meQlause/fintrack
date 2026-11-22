package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// DOC-09: shared tab header: a 32 dp leading mark (logo on Beranda, tinted tile elsewhere) and a 20 sp title
@Composable
fun FintrackTabHeader(
    title: String,
    modifier: Modifier = Modifier,
    leading: @Composable () -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth().height(FintrackTheme.sizes.tabHeaderHeight),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
    ) {
        leading()
        Text(
            text = title,
            style = FintrackTheme.typography.tabTitle,
            color = FintrackTheme.colors.texts.onSurface,
        )
    }
}
