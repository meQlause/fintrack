package com.meqlause.fintrack.feature.lock

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// S-02, FR-103: the line under the dots; a lockout shows its countdown in place of the notice
@Composable
internal fun PinNoticeText(
    notice: PinNotice,
    remainingSeconds: Long,
    modifier: Modifier = Modifier,
) {
    val text =
        when {
            remainingSeconds > 0L -> stringResource(R.string.lock_cooldown, remainingSeconds)
            notice == PinNotice.WRONG -> stringResource(R.string.lock_wrong)
            notice == PinNotice.MISMATCH -> stringResource(R.string.pin_setup_mismatch)
            else -> ""
        }
    Text(
        text = text,
        modifier = modifier,
        style = FintrackTheme.typography.bodyMedium,
        color = FintrackTheme.colors.semantics.error,
    )
}
