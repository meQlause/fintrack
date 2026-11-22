package com.meqlause.fintrack.core.designsystem.theme

import android.provider.Settings
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext

private const val DEFAULT_ANIMATOR_SCALE = 1f

// DOC-08: FintrackTheme { } provides every token and wraps Material3; FintrackTheme.colors reads them back
object FintrackTheme {
    internal val localColors = staticCompositionLocalOf { lightFintrackColors }
    internal val localTypography = staticCompositionLocalOf { FintrackTypography() }
    internal val localSpacing = staticCompositionLocalOf { FintrackSpacing() }
    internal val localShapes = staticCompositionLocalOf { FintrackShapes() }
    internal val localMotion = staticCompositionLocalOf { FintrackMotion() }
    internal val localSizes = staticCompositionLocalOf { FintrackSizes() }
    internal val localAlphas = staticCompositionLocalOf { FintrackAlphas() }

    val colors: FintrackColors
        @Composable
        @ReadOnlyComposable
        get() = localColors.current

    val typography: FintrackTypography
        @Composable
        @ReadOnlyComposable
        get() = localTypography.current

    val spacing: FintrackSpacing
        @Composable
        @ReadOnlyComposable
        get() = localSpacing.current

    val shapes: FintrackShapes
        @Composable
        @ReadOnlyComposable
        get() = localShapes.current

    val motion: FintrackMotion
        @Composable
        @ReadOnlyComposable
        get() = localMotion.current

    val sizes: FintrackSizes
        @Composable
        @ReadOnlyComposable
        get() = localSizes.current

    val alphas: FintrackAlphas
        @Composable
        @ReadOnlyComposable
        get() = localAlphas.current

    // DOC-08: the theme entry point; the operator keeps the FintrackTheme(darkTheme) { } call shape
    @Composable
    operator fun invoke(
        darkTheme: Boolean = isSystemInDarkTheme(),
        content: @Composable () -> Unit,
    ) {
        val colors = if (darkTheme) darkFintrackColors else lightFintrackColors
        val typography = remember { FintrackTypography() }
        val spacing = remember { FintrackSpacing() }
        val shapes = remember { FintrackShapes() }
        val durationScale = systemAnimatorScale()
        val motion = remember(durationScale) { FintrackMotion(durationScale = durationScale) }
        val sizes = remember { FintrackSizes() }
        val alphas = remember { FintrackAlphas() }
        CompositionLocalProvider(
            localColors provides colors,
            localTypography provides typography,
            localSpacing provides spacing,
            localShapes provides shapes,
            localMotion provides motion,
            localSizes provides sizes,
            localAlphas provides alphas,
        ) {
            MaterialTheme(
                colorScheme = colors.toColorScheme(isDark = darkTheme),
                typography = typography.toMaterialTypography(),
                shapes = Shapes(small = shapes.small, medium = shapes.medium, large = shapes.large),
                content = content,
            )
        }
    }
}

// DOC-08 / NFR-010: "remove animations" in system settings sets the animator scale to 0, so durations drop to 0
@Composable
private fun systemAnimatorScale(): Float {
    val context = LocalContext.current
    return remember(context) {
        Settings.Global.getFloat(
            context.contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE,
            DEFAULT_ANIMATOR_SCALE,
        )
    }
}

private fun FintrackTypography.toMaterialTypography(): Typography =
    Typography(
        headlineSmall = headlineSmall,
        titleMedium = titleMedium,
        bodyLarge = bodyLarge,
        bodyMedium = bodyMedium,
        labelLarge = labelLarge,
        labelMedium = labelMedium,
    )
