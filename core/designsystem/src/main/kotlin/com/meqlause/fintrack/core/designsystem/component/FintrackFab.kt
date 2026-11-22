package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.meqlause.fintrack.core.designsystem.R
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import kotlinx.coroutines.launch

// DOC-09: centre add button of the bottom bar; press squishes, the plus spins and dots burst out
@Composable
fun FintrackFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val feel = remember { FabFeel() }
    val scope = rememberCoroutineScope()
    val motion = FintrackTheme.motion
    val label = stringResource(R.string.component_bottom_bar_add)
    Box(
        modifier =
            modifier
                .size(FintrackTheme.sizes.navBarFab)
                .graphicsLayer {
                    scaleX = feel.squish.value
                    scaleY = feel.squish.value
                }.clip(CircleShape)
                .background(FintrackTheme.colors.texts.onSurface)
                .clickable(role = Role.Button) {
                    scope.launch { feel.play(motion) }
                    onClick()
                }.semantics { contentDescription = label },
        contentAlignment = Alignment.Center,
    ) {
        FabGlyph(feel = feel)
    }
}

// DOC-09: the plus icon, spinning on press, with the burst of dots drawn over it
@Composable
private fun FabGlyph(feel: FabFeel) {
    Icon(
        painter = painterResource(R.drawable.ic_plus),
        contentDescription = null,
        tint = FintrackTheme.colors.surfaces.background,
        modifier =
            Modifier
                .size(FintrackTheme.sizes.navBarFabIcon)
                .graphicsLayer { rotationZ = feel.spin.value },
    )
    FabBurst(progress = feel.burst.value, modifier = Modifier.size(FintrackTheme.sizes.navBarFab))
}
