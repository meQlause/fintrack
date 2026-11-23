package com.meqlause.fintrack.feature.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// S-03 Loading: one grey block standing in for a card; the shimmer is a later polish step
@Composable
internal fun HomeSkeletonBlock(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(FintrackTheme.shapes.medium)
                .background(FintrackTheme.colors.surfaces.surfaceVariant),
    )
}
