package com.meqlause.fintrack.core.designsystem.theme

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.meqlause.fintrack.core.designsystem.R

// DOC-08: visual check of every token; recorded in light, dark and font scale 2.0 (NFR-010)
@Composable
fun TokenSwatch(modifier: Modifier = Modifier) {
    val spacing = FintrackTheme.spacing
    Column(
        modifier =
            modifier
                .background(FintrackTheme.colors.surfaces.background)
                .padding(spacing.large)
                .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(spacing.xLarge),
    ) {
        ColorSamples()
        TypeSamples()
        SpacingSamples()
        ShapeSamples()
        MotionSamples()
    }
}

@Composable
private fun ColorSamples() {
    val colors = FintrackTheme.colors
    val chips =
        listOf(
            colors.surfaces.background,
            colors.surfaces.surface,
            colors.surfaces.surfaceVariant,
            colors.surfaces.outline,
            colors.texts.onSurface,
            colors.texts.onSurfaceVariant,
            colors.texts.faint,
            colors.brands.primary,
            colors.brands.onPrimary,
            colors.brands.primaryContainer,
            colors.brands.onPrimaryContainer,
            colors.semantics.income,
            colors.semantics.expense,
            colors.semantics.transfer,
            colors.semantics.warning,
            colors.semantics.error,
        )
    Column(verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small)) {
        chips.chunked(size = CHIPS_PER_ROW).forEach { rowChips ->
            Row(horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small)) {
                rowChips.forEach { chipColor -> ColorChip(color = chipColor) }
            }
        }
    }
}

@Composable
private fun ColorChip(color: Color) {
    Box(
        modifier =
            Modifier
                .size(FintrackTheme.spacing.xxxLarge)
                .clip(FintrackTheme.shapes.full)
                .background(color)
                .border(
                    width = HAIRLINE,
                    color = FintrackTheme.colors.surfaces.outline,
                    shape = FintrackTheme.shapes.full,
                ),
    )
}

@Composable
private fun TypeSamples() {
    val type = FintrackTheme.typography
    val amount = stringResource(R.string.token_sample_amount)
    val brand = stringResource(R.string.token_sample_brand)
    val digits = stringResource(R.string.token_sample_digits)
    val samples =
        listOf(
            type.numberHero to amount,
            type.numberLarge to amount,
            type.numberMedium to amount,
            type.headlineSmall to brand,
            type.titleMedium to brand,
            type.bodyLarge to brand,
            type.bodyMedium to brand,
            type.labelLarge to brand,
            type.labelMedium to digits,
        )
    Column(verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.xSmall)) {
        samples.forEach { (style, sample) ->
            Text(text = sample, style = style, color = FintrackTheme.colors.texts.onSurface)
        }
    }
}

@Composable
private fun SpacingSamples() {
    val spacing = FintrackTheme.spacing
    val steps =
        listOf(
            spacing.xxSmall,
            spacing.xSmall,
            spacing.small,
            spacing.medium,
            spacing.large,
            spacing.xLarge,
            spacing.xxLarge,
            spacing.xxxLarge,
        )
    Column(verticalArrangement = Arrangement.spacedBy(spacing.xSmall)) {
        steps.forEach { step -> SpacingBar(width = step) }
    }
}

@Composable
private fun SpacingBar(width: Dp) {
    Box(
        modifier =
            Modifier
                .width(width)
                .height(FintrackTheme.spacing.small)
                .background(FintrackTheme.colors.brands.primary),
    )
}

@Composable
private fun ShapeSamples() {
    val shapes = FintrackTheme.shapes
    Row(horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small)) {
        ShapeChip(shape = shapes.small)
        ShapeChip(shape = shapes.medium)
        ShapeChip(shape = shapes.large)
        ShapeChip(shape = shapes.full)
    }
}

@Composable
private fun ShapeChip(shape: Shape) {
    Box(
        modifier =
            Modifier
                .size(FintrackTheme.spacing.xxxLarge)
                .clip(shape)
                .background(FintrackTheme.colors.brands.primaryContainer),
    )
}

@Composable
private fun MotionSamples() {
    val motion = FintrackTheme.motion
    val spacing = FintrackTheme.spacing
    Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
        MotionSample(label = stringResource(R.string.token_motion_quick), token = motion.quick)
        MotionSample(label = stringResource(R.string.token_motion_medium), token = motion.medium)
        MotionSample(label = stringResource(R.string.token_motion_slow), token = motion.slow)
    }
}

@Composable
private fun MotionSample(
    label: String,
    token: MotionToken,
) {
    var target by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) { target = 1f }
    val progress by animateFloatAsState(
        targetValue = target,
        animationSpec = tween(durationMillis = token.durationMillis, easing = token.easing),
        label = label,
    )
    val colors = FintrackTheme.colors
    val spacing = FintrackTheme.spacing
    val labelStyle = FintrackTheme.typography.labelMedium
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(spacing.small)) {
        Text(text = label, style = labelStyle, color = colors.texts.onSurfaceVariant)
        Text(
            text = stringResource(R.string.token_motion_duration, token.durationMillis),
            style = labelStyle,
            color = colors.texts.onSurfaceVariant,
        )
        MotionBar(progress = progress, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun MotionBar(
    progress: Float,
    modifier: Modifier = Modifier,
) {
    val colors = FintrackTheme.colors
    val spacing = FintrackTheme.spacing
    Box(
        modifier =
            modifier
                .height(spacing.small)
                .clip(FintrackTheme.shapes.full)
                .background(colors.surfaces.surfaceVariant),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth(fraction = progress)
                    .height(spacing.small)
                    .background(colors.brands.primary),
        )
    }
}

private const val CHIPS_PER_ROW = 4
private val HAIRLINE = Dp(value = 1f)
