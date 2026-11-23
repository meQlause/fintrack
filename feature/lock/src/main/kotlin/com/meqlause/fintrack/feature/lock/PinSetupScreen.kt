package com.meqlause.fintrack.feature.lock

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.meqlause.fintrack.core.designsystem.component.NumericKeypad
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.feature.lock.navigation.PinSetupMode

// FR-100, DOC-10, S-12: the PIN setup; the recovery warning stays visible while a new PIN is chosen
@Composable
internal fun PinSetupScreen(
    state: PinSetupUiState,
    onDigit: (String) -> Unit,
    onBackspace: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shake = rememberPinShake(state.shakeCount)
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(FintrackTheme.colors.surfaces.background)
                .padding(horizontal = FintrackTheme.spacing.large),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        SetupPrompt(state = state, dotOffset = shake.value.dp, onCancel = onCancel)
        NumericKeypad(
            onDigit = onDigit,
            onBackspace = onBackspace,
            showTripleZero = false,
            modifier = Modifier.fillMaxWidth().padding(bottom = FintrackTheme.spacing.xxLarge),
        )
    }
}

// FR-100: the cancel action, the title of the current step, the dots, the notice and the warning on the new-PIN step
@Composable
private fun SetupPrompt(
    state: PinSetupUiState,
    dotOffset: Dp,
    onCancel: () -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(R.string.pin_setup_cancel),
            modifier = Modifier.fillMaxWidth().clickable(onClick = onCancel).padding(top = FintrackTheme.spacing.large),
            style = FintrackTheme.typography.labelLarge,
            color = FintrackTheme.colors.brands.primary,
        )
        Spacer(modifier = Modifier.height(FintrackTheme.spacing.xxxLarge))
        Text(
            text = stepTitle(state.mode, state.step),
            style = FintrackTheme.typography.headlineSmall,
            color = FintrackTheme.colors.texts.onSurface,
        )
        Spacer(modifier = Modifier.height(FintrackTheme.spacing.large))
        PinDots(filled = state.digits.length, modifier = Modifier.offset(x = dotOffset))
        Spacer(modifier = Modifier.height(FintrackTheme.spacing.medium))
        PinNoticeText(notice = state.notice, remainingSeconds = state.remainingSeconds)
        if (state.step == PinStep.NEW) {
            Spacer(modifier = Modifier.height(FintrackTheme.spacing.medium))
            Text(
                text = stringResource(R.string.pin_setup_warning),
                style = FintrackTheme.typography.bodyMedium,
                color = FintrackTheme.colors.texts.onSurfaceVariant,
            )
        }
    }
}

// FR-100: the heading of the step; a change or removal asks for the current PIN first
@Composable
private fun stepTitle(
    mode: PinSetupMode,
    step: PinStep,
): String =
    when (step) {
        PinStep.CURRENT -> {
            if (mode == PinSetupMode.REMOVE) {
                stringResource(R.string.pin_setup_title_remove)
            } else {
                stringResource(R.string.pin_setup_title_current)
            }
        }

        PinStep.NEW -> {
            if (mode == PinSetupMode.SET) {
                stringResource(R.string.pin_setup_title_new)
            } else {
                stringResource(R.string.pin_setup_title_new_change)
            }
        }

        PinStep.CONFIRM -> {
            stringResource(R.string.pin_setup_title_confirm)
        }
    }
