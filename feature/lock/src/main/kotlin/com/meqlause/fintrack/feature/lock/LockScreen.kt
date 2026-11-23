package com.meqlause.fintrack.feature.lock

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import com.meqlause.fintrack.core.designsystem.component.FintrackLogo
import com.meqlause.fintrack.core.designsystem.component.NumericKeypad
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// S-02, FR-100, FR-102: the lock screen; opaque so nothing of the app shows behind it
@Composable
internal fun LockScreen(
    state: LockUiState,
    onDigit: (String) -> Unit,
    onBackspace: () -> Unit,
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
        LockHeader(
            digitCount = state.digits.length,
            notice = state.notice,
            remainingSeconds = state.remainingSeconds,
            dotOffset = shake.value.dp,
        )
        NumericKeypad(
            onDigit = onDigit,
            onBackspace = onBackspace,
            showTripleZero = false,
            modifier = Modifier.fillMaxWidth().padding(bottom = FintrackTheme.spacing.xxLarge),
        )
    }
}

// S-02: app name, the prompt and the dots; the dots move with the shake offset
@Composable
private fun LockHeader(
    digitCount: Int,
    notice: PinNotice,
    remainingSeconds: Long,
    dotOffset: androidx.compose.ui.unit.Dp,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(FintrackTheme.spacing.xxxLarge))
        FintrackLogo()
        Text(
            text = stringResource(R.string.lock_app_name),
            style = FintrackTheme.typography.titleMedium,
            color = FintrackTheme.colors.texts.onSurface,
        )
        Spacer(modifier = Modifier.height(FintrackTheme.spacing.xxLarge))
        Text(
            text = stringResource(R.string.lock_title),
            style = FintrackTheme.typography.headlineSmall,
            color = FintrackTheme.colors.texts.onSurface,
        )
        Spacer(modifier = Modifier.height(FintrackTheme.spacing.large))
        PinDots(filled = digitCount, modifier = Modifier.offset(x = dotOffset))
        Spacer(modifier = Modifier.height(FintrackTheme.spacing.medium))
        PinNoticeText(notice = notice, remainingSeconds = remainingSeconds)
    }
}
