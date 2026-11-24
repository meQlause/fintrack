package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.designsystem.theme.toSpec
import kotlin.math.roundToInt

// DOC-09: one tab of the bottom bar; the selected icon lifts into the bubble and turns onPrimary
@Composable
internal fun RowScope.BarTabButton(
    item: FintrackBarItem,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .weight(1f)
                .fillMaxHeight()
                .selectable(
                    selected = selected,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    role = Role.Tab,
                    onClick = onClick,
                ).semantics { contentDescription = item.label }
                .padding(top = FintrackTheme.sizes.navBarIconTop),
        contentAlignment = Alignment.TopCenter,
    ) {
        BarTabIcon(iconRes = item.iconRes, selected = selected)
    }
}

@Composable
private fun BarTabIcon(
    iconRes: Int,
    selected: Boolean,
) {
    val sizes = FintrackTheme.sizes
    val brands = FintrackTheme.colors.brands
    val texts = FintrackTheme.colors.texts
    val liftPx = with(LocalDensity.current) { sizes.navBarLift.toPx() }
    val lift by animateFloatAsState(
        targetValue = if (selected) -liftPx else 0f,
        animationSpec = FintrackTheme.motion.bubble,
    )
    val tint by animateColorAsState(
        targetValue = if (selected) brands.onPrimary else texts.onSurfaceVariant,
        animationSpec = FintrackTheme.motion.medium.toSpec(),
    )
    Icon(
        painter = painterResource(iconRes),
        contentDescription = null,
        tint = tint,
        modifier =
            Modifier
                .size(sizes.navBarTabIcon)
                .offset { IntOffset(0, lift.roundToInt()) },
    )
}
